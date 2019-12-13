package NetworkScanner;

public class Host {
    private String mIp;

    private boolean mIsServer;

    public Host(String ip){
        mIp = ip;
    }

    public String getIp() {
        return mIp;
    }

    public void setIp(String isIp) {
        mIp = isIp;
    }

    public boolean isServer() {
        return mIsServer;
    }

    public void setIsServer(boolean isServer) {
        mIsServer = isServer;
    }
}
