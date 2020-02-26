/* FLOW OF CODE
    1. A network scan starts up, the results of the scan are handled by a Handler
    2. User can click on a Network to set that as their target server
    3. User can refresh the scan if there are no scans currently running
 */

package com.salabon.lazycards.NetworkScanner;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import android.os.Bundle;
import android.os.Handler;

import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.salabon.lazycards.Cards.DefaultPreferences;
import com.salabon.lazycards.R;

import java.util.List;

public class NetworkScannerActivity extends AppCompatActivity
implements HostSelectDialog.HostSelectDialogListener {
    static final int PING_COMPLETE = 1;
    static final int PING_STARTED = 2;

    private static final String TAG = "NetworkScannerActivity";
    private static final String HOST_IP = "host_ip";
    private static final String DIALOG_HOST_SELECT = "dialogHostSelect";

    private EditText mTargetServer;
    private EditText mHostPort;
    private RecyclerView mHostRecyclerView;
    private ProgressBar mProgressBar;

    private NetworkAdapter mAdapter;

    private String mCurrentHostIp;
    private boolean mRescanAvailable;

    private NetworkManager mNetworkManager;


    public static Intent newIntent(Context context){
        return new Intent(context, NetworkScannerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_scanner);

        if(savedInstanceState != null){
            mCurrentHostIp = savedInstanceState.getString(HOST_IP);
        }
        else{
            setCurrentHost();
        }

        setTargetServer();
        setHostPort();
        mHostRecyclerView = findViewById(R.id.network_recycler_view);
        mHostRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mProgressBar = findViewById(R.id.network_scanner_progressbar);
        mProgressBar.setMax(NetworkManager.MAX_TASKS);
        mProgressBar.setProgress(0);
        mProgressBar.setVisibility(View.VISIBLE);

        Handler responseHandler = setHandler();
        mNetworkManager = new NetworkManager(responseHandler);
        mNetworkManager.startScan(mCurrentHostIp);
        mNetworkManager.setNetworkTaskListener(
                new NetworkManager.NetworkTaskListener() {
                    @Override
                    public void onTaskComplete(int oldPos, int newPos) {
                        //updateUI();
                        for(int i = oldPos; i < newPos; i++){
                            mAdapter.notifyItemInserted(i);
                        }
                    }
                }
        );
        updateUI();
    }

    private void updateUI(){
        List<Host> hosts = NetworkLab.get().getHosts();

        if(mAdapter == null){
            mAdapter = new NetworkAdapter(hosts);
            mHostRecyclerView.setAdapter(mAdapter);
        }
        else{
            mAdapter.setHosts(hosts);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void setCurrentHost(){
        WifiManager wm = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);
        int ip = wm.getConnectionInfo().getIpAddress();

        mCurrentHostIp = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));
    }

    // Change the current host that will receive the flashcards
    private void setTargetServer(){
        mTargetServer = findViewById(R.id.network_scanner_host_edit_text);
        String ip = DefaultPreferences.getIp(this);
        if(ip != null) {
            mTargetServer.setText(ip);
        }

        mTargetServer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DefaultPreferences.setIp(NetworkScannerActivity.this, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setHostPort(){
        mHostPort = findViewById(R.id.network_scanner_port_edit_text);
        String port = DefaultPreferences.getPort(this);

        if(port != null){
            mHostPort.setText(port);
        }

        mHostPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DefaultPreferences.setPort(NetworkScannerActivity.this, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private Handler setHandler(){
        Handler responseHandler = new Handler(getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                if(mProgressBar == null){
                    return;
                }
                switch (msg.what){
                    case PING_COMPLETE:
                        synchronized (mProgressBar) {
                            mProgressBar.setMax(NetworkManager.MAX_TASKS);
                            int progress = mProgressBar.getProgress();
                            mProgressBar.setProgress(++progress);
                            if (mProgressBar.getProgress() == mProgressBar.getMax()) {
                                mProgressBar.setVisibility(View.GONE);

                                mRescanAvailable = true;
                                invalidateOptionsMenu();

                                NetworkLab.get().sortHostsList();
                                // Must update the adapter's list because it's holding ref to old one
                                mAdapter.setHosts(NetworkLab.get().getHosts());
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                        break;
                    case PING_STARTED:
                        mProgressBar.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                        //do nothing
                }
            }
        };
        return responseHandler;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_network_scanner, menu);

        MenuItem rescanItem = menu.findItem(R.id.network_scanner_rescan);

        if(mRescanAvailable){
            rescanItem.setEnabled(true);
            rescanItem.setVisible(true);
        }
        else{
            rescanItem.setEnabled(false);
            rescanItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.network_scanner_rescan:
                // Allow a rescan only if the previous scan has completed
                if(mProgressBar.getProgress() == mProgressBar.getMax()) {
                    // Get rid of the option to rescan since we started a new one
                    mRescanAvailable = false;
                    invalidateOptionsMenu();

                    mNetworkManager.cancelAll();
                    NetworkLab.get().emptyHostsList();
                    mProgressBar.setProgress(0);
                    mNetworkManager.startScan(mCurrentHostIp);

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(HOST_IP, mCurrentHostIp);
    }

    @Override
    protected void onStop(){
        super.onStop();
        NetworkLab.get().emptyHostsList();
        mNetworkManager.cancelAll();
    }

    @Override
    public void onDialogPositiveClick(String host) {
        DefaultPreferences.setIp(this, host);
        mTargetServer.setText(host);
    }

    private class NetworkHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mHostIcon;
        private TextView mHostLabel;
        private TextView mHostIp;

        private Host mHost;

        NetworkHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_network, parent, false));

            itemView.setOnClickListener(this);

            mHostIcon = itemView.findViewById(R.id.host_icon);
            mHostLabel = itemView.findViewById(R.id.host_label);
            mHostIp = itemView.findViewById(R.id.ip_label);
        }

        void bind(Host host){
            mHost = host;

            mHostLabel.setText(R.string.host_label);
            mHostIp.setText(mHost.getIp());
        }

        @Override
        public void onClick(View v) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            HostSelectDialog dialog = HostSelectDialog.newInstance(mHost.getIp());
            dialog.show(fragmentManager, DIALOG_HOST_SELECT);
        }
    }

    private class NetworkAdapter extends RecyclerView.Adapter<NetworkHolder>{
        List<Host> mHosts;

        NetworkAdapter(List<Host> hosts){
            mHosts = hosts;
        }

        @NonNull
        @Override
        public NetworkHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new NetworkHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull NetworkHolder holder, int position) {
            Host host = mHosts.get(position);
            holder.bind(host);
        }

        @Override
        public int getItemCount() {
            return mHosts.size();
        }

        void setHosts(List<Host> hosts){
            mHosts = hosts;
        }
    }
}
