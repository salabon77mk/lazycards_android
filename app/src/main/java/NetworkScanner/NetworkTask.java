package NetworkScanner;

import java.util.List;

public class NetworkTask implements PingerRunnable.PingerRunnableMethods {
    // The hosts that exist
    private List<String> mReachableHosts;

    // The thread that is doing work for this task
    Thread mThreadThis;

    // The current thread
    private Thread mCurrentThread;

    // Reference to the Runnable that will ping a host
    private PingerRunnable mPingerRunnable;

    // ThreadPool singleton
    private static NetworkManager sNetworkManager;

    NetworkTask(String domain, int index){
        mPingerRunnable = new PingerRunnable(this, index, domain);
    }

    static void setNetworkManager(NetworkManager networkManager){
        sNetworkManager = networkManager;
    }

    void handleState(int state){
        sNetworkManager.handleState(this, state);
    }

    public void setCurrentThread(Thread thread){
        synchronized (sNetworkManager){
            mCurrentThread = thread;
        }
    }

    public Thread getCurrentThread(){
        synchronized (sNetworkManager){
            return mCurrentThread;
        }
    }

    protected PingerRunnable getPingerRunnable(){
        return mPingerRunnable;
    }

    @Override
    public void setDownloadThread(Thread thread) {
        setCurrentThread(thread);
    }

    @Override
    public List<String> getHosts() {
        return mReachableHosts;
    }

    @Override
    public void setHosts(List<String> hosts) {
        mReachableHosts = hosts;
    }

    @Override
    public void setCurrentState(int state) {
        int outState;

        switch(state){
            case PingerRunnable.PING_STATE_STARTED:
                outState = NetworkManager.PING_STARTED;
                break;
            case PingerRunnable.PING_STATE_COMPLETE:
                outState = NetworkManager.PING_COMPLETE;
                break;
            default:
                outState = NetworkManager.PING_STARTED;
        }

        handleState(outState);
    }


}
