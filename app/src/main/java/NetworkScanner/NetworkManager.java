package NetworkScanner;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


// I think this entire class can be reworked away from singleton
// TODO implement an interface listener that will allow handler to update main thread
public class NetworkManager {

    static final int PING_STARTED = 1;
    static final int PING_COMPLETE = 2;

    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    private static final int KEEP_ALIVE_TIME = 1;

    private static final int MAX_PINGS = 256;

    // These two fields must when multiplied be equal to MAX_PINGS
    private static final int MAX_TASKS = 64;
    protected static final int PINGS_PER_TASK = 4;  // used in PingerRunnable

    private static final int CORE_POOL_SIZE = 64;
    private static final int MAX_POOL_SIZE = 64;

    private final BlockingQueue<Runnable> mPingerQueue;

    private final ThreadPoolExecutor mPingerPool;

    private Handler mRequestHandler;
    private final Handler mResponseHandler;

    private static NetworkManager sNetworkManager = null;

    static {
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    }

    NetworkTaskListener mNetworkTaskListener;

    protected interface NetworkTaskListener{
        void onTaskComplete();
    }

    protected void setNetworkTaskListener(NetworkTaskListener listener){
        mNetworkTaskListener = listener;
    }

    // TODO This is where we'll start up our thread pools
    // construct needs a handler from main thread
    protected NetworkManager(Handler responseHandler){
        assert MAX_PINGS == MAX_TASKS * PINGS_PER_TASK : "MAX_TASKS: " + MAX_TASKS + " and PINGS_PER_TASK: " +
                PINGS_PER_TASK + "when multiplied do not equal MAX_PINGS: " + MAX_PINGS;

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
                    default:
                        super.handleMessage(inputMessage);
                }
            }
        };
    }

    protected void handleState(NetworkTask task,  int state){
        // just pass the message along
        mRequestHandler.obtainMessage(state, task).sendToTarget();
    }

    protected void newPingerTasks(String host){
        String domain = getDomain(host);
        NetworkTask.setNetworkManager(this);
        int interval = Integer.numberOfTrailingZeros(PINGS_PER_TASK);
        for(int i = 0; i < MAX_TASKS; i++){
            NetworkTask networkTask = new NetworkTask(domain, i << interval);
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
                List<String> reachableHosts = networkTask.getHosts();
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
                    }
                }
                mNetworkTaskListener.onTaskComplete();
            }
        });
    }

    public void cancelAll(){
        NetworkTask[] tasks = new NetworkTask[this.mPingerQueue.size()];
        this.mPingerQueue.toArray(tasks);

        synchronized (this){
            for(int i = 0; i < tasks.length; i++){
                Thread thread = tasks[i].mThreadThis;

                if(thread != null){
                    thread.interrupt();
                }
            }
        }
    }
}
