package com.confessit;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/***
 * Data Structure for handling pending post
 * Requirement:
 * 1. First In First Out
 * 2. If the number of elements in the data structure is less than or equals to 5, pop the data every 15 minutes.
 * 3. If the number of elements in the data structure is less than or equals to 10, pop the data every 10 minutes.
 * 4. If the number of elements in the data structure is more than 10, pop the data every 5 minutes.
 *
 * a. Refresh button - to refresh the main application
 * b. A scheduler process, pop the post based on the requirement
 *
 */

public class PendingQueue {
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture function;

    /***
     * Start the schedule with a fixed delay
     * @param time is the delay in the form of minutes
     */
    public void startSchedule(int time) {
        Runnable run = () -> popPost();
        function = scheduler.scheduleWithFixedDelay(run, 0, time, TimeUnit.MINUTES);
    }

    /***
     * Pop the submitted post from the pending queue
     */
    public void popPost() {

    }
}
