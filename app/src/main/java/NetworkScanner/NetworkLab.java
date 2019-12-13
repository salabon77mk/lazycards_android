package NetworkScanner;

import java.util.ArrayList;
import java.util.List;

public class NetworkLab {
    private static NetworkLab sNetworkLab;
    private List<Host> mHostList;

    public static NetworkLab get(){
        if(sNetworkLab == null){
            sNetworkLab = new NetworkLab();
        }
        return sNetworkLab;
    }

    private NetworkLab(){
        mHostList = new ArrayList<>();

        //TODO Delete this when network scanner is done
        // Prepopulates with some random hosts
        String ipBase = "255.255.0.";
        for(int i = 0; i < 50; i++){
            Host host = new Host( ipBase + i);
            mHostList.add(host);
        }
    }

    public void addHost(Host host){
        mHostList.add(host);
    }

    public List<Host> getHosts(){
        return mHostList;
    }
}
