package NetworkScanner;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import android.os.Bundle;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.salabon.lazycards.DefaultPreferences;
import com.salabon.lazycards.R;

import java.util.List;

// TODO
// Fix landscape orientation

public class NetworkScannerActivity extends AppCompatActivity {
    private static final String TAG = "NetworkScannerActivity";
    private static final String HOST_IP = "host_ip";

    private TextView mTargetServer;
    private Button mChangeHostButton;
    private RecyclerView mHostRecyclerView;

    private NetworkAdapter mAdapter;

    private String mCurrentHostIp;

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
        createChangeHostButton();
        mHostRecyclerView = findViewById(R.id.network_recycler_view);
        mHostRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Handler responseHandler = new Handler();

        mNetworkManager = new NetworkManager(responseHandler);
        mNetworkManager.newPingerTasks(mCurrentHostIp);
        mNetworkManager.setNetworkTaskListener(
                new NetworkManager.NetworkTaskListener() {
                    @Override
                    public void onTaskComplete() {
                        updateUI();
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
        mTargetServer = findViewById(R.id.network_scanner_host_text_view);
        String ip = DefaultPreferences.getIp(this);
        if(ip == null){
            mTargetServer.setText(R.string.host_not_set_text);
        }
        else{
            mTargetServer.setText(ip);
        }
    }

    private void createChangeHostButton(){
        mChangeHostButton = findViewById(R.id.change_host_button);
        mChangeHostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO make a dialog that asks the user for an IP
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(HOST_IP, mCurrentHostIp);
    }

    @Override
    protected void onStop(){
        super.onStop();
        mNetworkManager.cancelAll();
    }

    private class NetworkHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mHostIcon;
        private TextView mHostLabel;
        private TextView mHostIp;

        private Host mHost;

        public NetworkHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_network, parent, false));

            itemView.setOnClickListener(this);

            mHostIcon = itemView.findViewById(R.id.host_icon);
            mHostLabel = itemView.findViewById(R.id.host_label);
            mHostIp = itemView.findViewById(R.id.ip_label);
        }

        public void bind(Host host){
            mHost = host;

            mHostLabel.setText(R.string.host_label);
            mHostIp.setText(mHost.getIp());
        }

        // TODO on click listener on to the view that will set the host
        @Override
        public void onClick(View v) {

        }
    }

    private class NetworkAdapter extends RecyclerView.Adapter<NetworkHolder>{
        List<Host> mHosts;

        public NetworkAdapter(List<Host> hosts){
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

        public void setHosts(List<Host> hosts){
            mHosts = hosts;
        }
    }


}
