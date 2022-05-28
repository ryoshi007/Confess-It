package com.confessit;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;


public class PendingQueue {
    private Queue pendingQ = new Queue();
    private static class DaemonThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        }
    }
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1,
            new DaemonThreadFactory());

    /***
     * Start the scheduler depending on the size of the queue
     */
    public void startSchedule() {
        pendingQ.clear();
        pendingQ.enqueue(obtainApprovedPost());
        int queueSize = pendingQ.getSize();
        Runnable run = this::displayPost;

        if (queueSize <= 5) {
            scheduler.schedule(run, 15, TimeUnit.MINUTES);
        } else if (queueSize <= 10) {
            scheduler.schedule(run, 10, TimeUnit.MINUTES);
        } else {
            scheduler.schedule(run, 5, TimeUnit.MINUTES);
        }
    }

    /**
     * Obtain tag id of the posts that have been approved but not yet displayed
     * @return an array list of integer consisting tag id of the approved yet displayed posts
     */
    private ArrayList<Integer> obtainApprovedPost() {
        ArrayList<Integer> pendingPost = new ArrayList<>();
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            String sql = "SELECT * FROM post WHERE approval = 1 and displayStatus = 0";
            queryResult = statement.executeQuery(sql);

            while(queryResult.next()) {
                int tagID = queryResult.getInt("tagid");
                pendingPost.add(tagID);
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            if (queryResult != null) {
                try {
                    queryResult.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connectDB != null) {
                try {
                    connectDB.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return pendingPost;
    }

    /**
     * Change the display status of the approved post to 1
     * @param tagid is the id of the approved post
     */
    private void changeDisplayStatus(int tagid) {
        Connection connectDB = null;
        Statement statement = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            String sql = "UPDATE post SET displayStatus = 1 WHERE tagid = " + tagid;
            statement = connectDB.createStatement();
            statement.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            if (connectDB != null) {
                try {
                    connectDB.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /***
     * Display the submitted post from the pending queue
     */
    public void displayPost() {
        int dequeueTagID = pendingQ.dequeue();
        changeDisplayStatus(dequeueTagID);
        startSchedule();
    }

}
