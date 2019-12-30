package NetworkScanner;

import java.util.ArrayList;
import java.util.List;

public class NetworkLab {
    private static final int MAX_IP_VALUE = 256; // assuming IPv4

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
    }

    public void addHost(Host host){
        mHostList.add(host);
    }

    public List<Host> getHosts(){
        return mHostList;
    }

    public void emptyHostsList(){
        mHostList.clear();
    }

    public void sortHostsList(){
        int[] values = new int[MAX_IP_VALUE];
        for(int i = 0; i < mHostList.size(); i++){
            Host currHost = mHostList.get(i);
            values[currHost.getLastIpByte()] = i + 1;
        }

        List<Host> sortedHosts = new ArrayList<>();
        for(int i = 0; i < values.length; i++){
            if(values[i] != 0){
                Host currHost = mHostList.get(values[i] - 1);
                sortedHosts.add(currHost);
            }
        }
        mHostList = sortedHosts;
    }
}
