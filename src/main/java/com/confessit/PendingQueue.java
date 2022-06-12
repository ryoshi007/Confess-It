package com.confessit;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;


public class PendingQueue implements Runnable{
    private Queue pendingQ = new Queue();
    private ArrayList<String> containList = new ArrayList<>();

    @Override
    public void run() {
        ArrayList<Integer> postList = obtainApprovedPost();
        for (int tagID: postList) {
            if (!containList.contains(String.valueOf(tagID))) {
                pendingQ.enqueue(tagID);
                containList.add(String.valueOf(tagID));
            }
        }
        int queueSize = pendingQ.getSize();

//        Based on the question
//        if (queueSize == 0) {
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }else if (queueSize <= 5) {
//            try {
//                Thread.sleep(60000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        } else if (queueSize <= 10) {
//            try {
//                Thread.sleep(40000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            try {
//                Thread.sleep(25000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }

        //For testing purpose
        if (queueSize == 0) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }else if (queueSize <= 5) {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else if (queueSize <= 10) {
            try {
                Thread.sleep(40000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                Thread.sleep(25000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        deployPost();
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
    public void deployPost() {
        if (!pendingQ.isEmpty()) {
            int dequeueTagID = pendingQ.dequeue();
            changeDisplayStatus(dequeueTagID);
            containList.remove(String.valueOf(dequeueTagID));
            run();
        }
    }

}
