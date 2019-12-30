/* FLOW OF THE CODE (refer to NetworkManager for the big picture
    1. Create runnables
    2. When the state of a runnable changes, the NetworkManager is notified of the change
 */

package NetworkScanner;

import java.util.ArrayList;
import java.util.List;

public class NetworkTask implements PingerRunnable.PingerRunnableMethods {
    // The hosts that exist
    private List<String> mReachableHosts = new ArrayList<>();

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

    // Has to be a synchronized operation since the Thread object can be modified by
    // processes outside of the app
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

    PingerRunnable getPingerRunnable(){
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
            case PingerRunnable.PING_STATE_FAILED:
                outState = NetworkManager.PING_FAILED;
                break;
            default:
                outState = NetworkManager.PING_STARTED;
        }

        handleState(outState);
    }
}
