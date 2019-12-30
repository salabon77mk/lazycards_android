/* Overall flow of the code
   TYPICAL SCENARIO
   1. Instantiate network scanner somewhere else, instantiation creates a thread pool
   2. Start a new scan (startScan)
   3. startScan creates NetworkTasks which ping hosts on the network
   4. These NetworkTasks launch a runnable
   5. When that runnable is done, it changes its state, notifying the handlers here
   6. Once a scan is complete, enter ResponseHandlerUpdate

   CANCEL SCENARIO
   1. If user hits refresh to start a new scan, we must first cancel all the threads, done in cancelAll
 */

package NetworkScanner;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class NetworkManager {

    // Have to use AtomicIntegers to update the RecyclerView, using prim ints will make the UI not update properly
    private AtomicInteger oldPos = new AtomicInteger(0);
    private AtomicInteger newPos = new AtomicInteger(0);

    static final int PING_FAILED = -1;
    static final int PING_STARTED = 1;
    static final int PING_COMPLETE = 2;

    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    private static final int KEEP_ALIVE_TIME = 1;

    private static final int MAX_PINGS = 256;

    // These two fields must when multiplied be equal to MAX_PINGS
    static final int MAX_TASKS = 128;
    static final int PINGS_PER_TASK = 2;  // used in PingerRunnable

    private static final int CORE_POOL_SIZE = 128;
    private static final int MAX_POOL_SIZE = 128;

    private final BlockingQueue<Runnable> mPingerQueue;

    private final HashSet<NetworkTask> mTaskHashSet;

    private final ThreadPoolExecutor mPingerPool;

    private Handler mRequestHandler;
    private final Handler mResponseHandler;

    private static NetworkManager sNetworkManager = null;

    static {
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    }

    NetworkTaskListener mNetworkTaskListener;

    protected interface NetworkTaskListener{
        void onTaskComplete(int oldPos, int newPos);
    }

    void setNetworkTaskListener(NetworkTaskListener listener){
        mNetworkTaskListener = listener;
    }

    NetworkManager(Handler responseHandler){
        assert MAX_PINGS == MAX_TASKS * PINGS_PER_TASK : "MAX_TASKS: " + MAX_TASKS + " and PINGS_PER_TASK: " +
                PINGS_PER_TASK + "when multiplied do not equal MAX_PINGS: " + MAX_PINGS;

        mTaskHashSet = new HashSet<>();
        mResponseHandler = responseHandler;

        mPingerQueue = new LinkedBlockingQueue<>();

        mPingerPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT, mPingerQueue);

        mRequestHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message inputMessage){
                NetworkTask networkTask = (NetworkTask) inputMessage.obj;

                switch (inputMessage.what){
                    case PING_COMPLETE:
                        responseHandlerUpdate(networkTask);
                        break;
                    case PING_STARTED:
                        break; // nothing to do
                    case PING_FAILED:
                        mTaskHashSet.remove(networkTask);
                        break;
                    default:
                        super.handleMessage(inputMessage);
                }
            }
        };
    }

    void handleState(NetworkTask task,  int state){
        // just pass the message along
        mRequestHandler.obtainMessage(state, task).sendToTarget();
    }

    void startScan(String host){
        mResponseHandler.obtainMessage(NetworkScannerActivity.PING_STARTED).sendToTarget();
        String domain = getDomain(host);
        NetworkTask.setNetworkManager(this);
        int interval = Integer.numberOfTrailingZeros(PINGS_PER_TASK);
        for(int i = 0; i < MAX_TASKS; i++){
            NetworkTask networkTask = new NetworkTask(domain, i << interval);
            mTaskHashSet.add(networkTask);
            this.mPingerPool.execute(networkTask.getPingerRunnable());
        }
    }

    // Given an ip of 127.0.0.1, the domain would be 127.0.0.
    private String getDomain(String host){
        int idx = host.lastIndexOf('.');
        return host.substring(0, idx) + '.';
    }

    private void responseHandlerUpdate(final NetworkTask networkTask){
        mResponseHandler.post(new Runnable(){
            @Override
            public void run(){
                mResponseHandler.obtainMessage(NetworkScannerActivity.PING_COMPLETE).sendToTarget();
                List<String> reachableHosts = networkTask.getHosts();
                // no longer need the task
                mTaskHashSet.remove(networkTask);

                if(reachableHosts.isEmpty()){
                    return;
                }

                List<Host> hosts = new ArrayList<>();
                for(String reachableHost: reachableHosts){
                    hosts.add(new Host(reachableHost));
                }

                NetworkLab netLab = NetworkLab.get();
                synchronized (netLab.getHosts()){
                    for(Host host: hosts){
                        netLab.addHost(host);
                        newPos.getAndAdd(1);
                    }
                }
                mNetworkTaskListener.onTaskComplete(oldPos.get(),newPos.get());
                oldPos.getAndSet(newPos.get());
            }
        });
    }

    void cancelAll(){
        // This boolean ensures we don't queue up multiple cancel requests which may crash the app
        synchronized (this){
            for(NetworkTask task : mTaskHashSet){
                Thread thread = task.mThreadThis;

                if(thread != null){
                    thread.interrupt();
                }
            }
        }
    }
}

