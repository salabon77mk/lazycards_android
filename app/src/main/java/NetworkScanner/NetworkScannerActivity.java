package NetworkScanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

public class NetworkScannerActivity extends AppCompatActivity {
    //TODO add recyclerview and make it update the UI
    private TextView mCurrentHost;
    private Button mChangeHostButton;
    private RecyclerView mHostRecyclerView;
    private NetworkAdapter mAdapter;

    public static Intent newIntent(Context context){
        return new Intent(context, NetworkScannerActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_scanner);

        setCurrentHost();
        createChangeHostButton();
        mHostRecyclerView = findViewById(R.id.network_recycler_view);
        mHostRecyclerView.setLayoutManager(new LinearLayoutManager(this));

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
        }

    }

    // Change the current host that will receive the flashcards
    private void setCurrentHost(){
        mCurrentHost = findViewById(R.id.network_scanner_host_text_view);
        String ip = DefaultPreferences.getIp(this);
        if(ip == null){
            mCurrentHost.setText(R.string.host_not_set_text);
        }
        else{
            mCurrentHost.setText(ip);
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

    //TODO
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
