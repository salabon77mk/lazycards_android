package NetworkScanner;


public class Host {
//    private static final String TAG = "Host";
    private String mIp;
    private boolean mIsServer;
    private int mLastIpByte;

    public Host(String ip){
        mIp = ip;
        int lastIdx = mIp.lastIndexOf('.');
        String lastByte = mIp.substring(lastIdx + 1);
        mLastIpByte = Integer.valueOf(lastByte);
//        Log.i(TAG, "" + mLastIpByte);
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

    public int getLastIpByte() {
        return mLastIpByte;
    }

    public void setLastIpByte(int lastIpByte) {
        mLastIpByte = lastIpByte;
    }
}
