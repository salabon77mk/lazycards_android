package com.salabon.lazycards.NetworkScanner;


import android.os.Process;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

class PingerRunnable implements Runnable {
    private static final String TAG = "PingerCallable";

    static final int PING_STATE_FAILED = -1;
    static final int PING_STATE_STARTED = 1;
    static final int PING_STATE_COMPLETE = 2;

    private static final int TIMEOUT = 4000;

    // The interval constant that will help set up the start and end address
    private static final int INTERVAL = NetworkManager.PINGS_PER_TASK;

    private String mDomain;
    private final int mStartAddressIdx;
    private final int mEndAddressIdx;

    private List<String> reachableHosts = new ArrayList<>();

    //private static final int PORT = 80;

    final PingerRunnableMethods mNetworkTask;

    interface PingerRunnableMethods{
        // Set the current downloading thread
        void setDownloadThread(Thread thread);

        List<String> getHosts();

        // Will be called once the ping is complete
        void setHosts(List<String> hosts);

        // Set the current state of the thread
        void setCurrentState(int state);
    }


    PingerRunnable(PingerRunnableMethods networkTask, int startIndex, String domain){
        mNetworkTask = networkTask;
        mDomain = domain;
        mStartAddressIdx = startIndex;
        mEndAddressIdx = mStartAddressIdx + INTERVAL;
    }

    @Override
    public void run() {
        // Allows network task to interrupt this thread
        mNetworkTask.setCurrentState(PING_STATE_STARTED);
        mNetworkTask.setDownloadThread(Thread.currentThread());

        // We don't need this to have a high priority
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        try {
            if(Thread.interrupted()){
                throw new InterruptedException();
            }

            for (int i = mStartAddressIdx; i < mEndAddressIdx; i++) {
                String address = mDomain + i;
                try {
                    if (InetAddress.getByName(address).isReachable(TIMEOUT)) {
                        reachableHosts.add(address);
                    }
                } catch (IOException e) {
                    // Do nothing as there's nothing to worry about here
                }

                if(Thread.interrupted()){
                    throw new InterruptedException();
                }
            }

            mNetworkTask.setHosts(reachableHosts);
            mNetworkTask.setCurrentState(PING_STATE_COMPLETE);
        }
        catch (InterruptedException ie){
            mNetworkTask.setCurrentState(PING_STATE_FAILED);
        }
        finally {
            // Release the thread
            mNetworkTask.setDownloadThread(null);
            // reset flag
            Thread.interrupted();
        }
    }

}


// This could be a more effective way of pinging if we need to use sockets?
/*
    public String call() throws Exception{
                try(final Socket socket = new Socket()){
            final InetSocketAddress inetSocketAddress = new InetSocketAddress(mAddress, PORT);

            socket.connect(inetSocketAddress, TIMEOUT);
            return mAddress;
        }
        catch(java.io.IOException e){
            return null;
        }
    }
 */

