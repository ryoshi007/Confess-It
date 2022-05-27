package com.confessit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Json {

    /**
     * Add a reply post tag ID of a post to the database
     * @param tagID tag ID of a post
     * @param replyPostTagID tag ID of a reply post
     */
    public void insertReplyPostTagID(int tagID, int replyPostTagID) {
        Connection connectDB = null;
        Statement statement = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            statement.executeUpdate("UPDATE post SET replyPosts = JSON_ARRAY_APPEND(replyPosts, '$', '" + replyPostTagID + "') WHERE tagid = '" + tagID + "'");
        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
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
    }

    /**
     * Retrieve reply post tag IDs of a post
     * @param tagID tag ID of a post
     * @return A list containing tag ID of reply posts
     */
    public static List<Integer> retrieveReplyPostTagID(int tagID) {
        ArrayList<Integer> storeTagID = new ArrayList<>();
        Connection connectDB = null;
        Statement statement = null;
        ResultSet queryResult = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            queryResult = statement.executeQuery("SELECT replyPosts AS replyPostTagID FROM post WHERE tagid ='" + tagID + "'");
            while(queryResult.next()) {
                String temp = queryResult.getString("replyPostTagID");
                temp = temp.replace("[","").replace("]","").replace(",","").replace("\"","");
                String[] temp1 = temp.split(" ");
                for (String temp2 : temp1) {
                    storeTagID.add(Integer.valueOf(temp2));
                }
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
        return storeTagID;
    }

    /**
     * Delete a reply post tag ID of a post
     * @param tagID tag ID of a post
     * @param tagToDelete tag ID of a reply post
     */
    public void deleteReplyPostTagID(int tagID, int tagToDelete) {
        Connection connectDB = null;
        Statement statement = null;

        try {
            DatabaseConnection connection = new DatabaseConnection();
            connectDB = connection.getConnection();
            statement = connectDB.createStatement();
            statement.executeUpdate("UPDATE post SET replyPosts = JSON_REMOVE(replyPosts, replace(JSON_SEARCH(replyPosts, 'one','" + tagToDelete + "'), '\"', '')) WHERE tagid = '" + tagID + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
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
    }

    /***
     * Add the post tag ID into UserHistory.json
     * @param email user email address
     * @param tagID tag ID of a post
     */
    @SuppressWarnings("unchecked")
    public void writeUserHistory(String email, int tagID) {
        File historyJson = new File("JsonFiles\\UserHistory.json");

        if (historyJson.exists()) {
            // if UserHistory.json file exists,
            // append the newly added post at the end of the UserHistory.json file
            JSONParser jsonParser = new JSONParser();

            try {

                Object obj = jsonParser.parse(new FileReader(historyJson));
                JSONArray history = (JSONArray) obj;

                JSONObject object;
                JSONArray tagIDArray;

                // if user email address exists in the json array
                // add the tag ID into the tag array
                int check = -1;
                for (int i = 0; i < history.size(); i++) {
                    object = (JSONObject) history.get(i);
                    if (object.get("email").equals(email)) {
                        tagIDArray = (JSONArray) object.get("tagID");
                        tagIDArray.add(tagID);
                        check = i;
                        break;
                    }
                }

                // if user emails is not exist in the json array
                // create a new object containing user email address and tag ID in json array
                if (check == -1) {
                    object = new JSONObject();
                    tagIDArray = new JSONArray();

                    tagIDArray.add(tagID);
                    object.put("email",email);
                    object.put("tagID",tagIDArray);
                    history.add(object);
                }


                FileWriter file = new FileWriter(historyJson);
                file.write(history.toJSONString());
                file.flush();
                file.close();


            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }

        } else {

            // if UserHistory.json file is not exist,
            // write the user email address and the post tag ID into a new UserHistory.json file
            JSONObject object = new JSONObject();
            JSONArray tagIDArray = new JSONArray();

            object.put("email",email);
            tagIDArray.add(tagID);
            object.put("tagID",tagIDArray);

            JSONArray history = new JSONArray();
            history.add(object);

            try {
                FileWriter file = new FileWriter(historyJson);
                file.write(history.toJSONString());
                file.flush();
                file.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Return a list of tag IDs by reading UserHistory.json
     * @param email user email address
     * @return tagID (an integer array list that stores tag IDs)
     */
    public List<Integer> readUserHistory(String email) {
        List<Integer> tagID = new ArrayList<>();
        File historyJson = new File("JsonFiles\\UserHistory.json");
        if (historyJson.exists()) {
            JSONParser jsonParser = new JSONParser();
            try {
                Object obj = jsonParser.parse(new FileReader(historyJson));
                JSONArray history = (JSONArray) obj;

                // Iterate over history array to load all tag IDs that match with the email address and store in lists
                for (Object object : history) {
                    if (object instanceof JSONObject) {
                        if (((JSONObject) object).get("email").equals(email)) {
                            JSONArray tagIDArray = (JSONArray) ((JSONObject) object).get("tagID");
                            for (int i = 0; i < tagIDArray.size(); i++) {
                                tagID.add(Math.toIntExact((Long) tagIDArray.get(i)));
                            }
                            break;
                        }
                    }
                }

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
        return tagID;
    }

    /**
     * delete a tag ID based on the user email address and tag ID in UserHistory.json
     * @param email user email address
     * @param tagID tag ID of a post
     */
    public void deleteUserHistory(String email, int tagID) {
        File historyJson = new File("JsonFiles\\UserHistory.json");

        if (historyJson.exists()) {
            // if UserHistory.json file exists,
            // delete the tag id that matches with tagID based on email address
            JSONParser jsonParser = new JSONParser();

            try {

                Object obj = jsonParser.parse(new FileReader(historyJson));
                JSONArray history = (JSONArray) obj;

                // if user email address exists in the history array
                // remove the tag id that matches with the tagID from the tagID array
                for (int i = 0; i < history.size(); i++) {
                    JSONObject object = (JSONObject) history.get(i);

                    if (object.get("email").equals(email)) {
                        JSONArray tagIDArray = (JSONArray) object.get("tagID");

                        for (int j = 0; j < tagIDArray.size(); j++) {
                            if (tagIDArray.get(j).equals((long) tagID)) {
                                tagIDArray.remove(j);
                                break;
                            }
                        }
                        break;
                    }
                }


                FileWriter file = new FileWriter(historyJson);
                file.write(history.toJSONString());
                file.flush();
                file.close();


            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Add the post tag ID into UserArchive.json
     * @param email user email address
     * @param tagID tag ID of a post
     */
    @SuppressWarnings("unchecked")
    public void writeUserArchive(String email, int tagID) {
        File archiveJson = new File("JsonFiles\\UserArchive.json");

        if (archiveJson.exists()) {
            // if UserHistory.json file exists,
            // append the newly added post at the end of the UserHistory.json file
            JSONParser jsonParser = new JSONParser();

            try {

                Object obj = jsonParser.parse(new FileReader(archiveJson));
                JSONArray archive = (JSONArray) obj;

                JSONObject object;
                JSONArray tagIDArray;

                // if user email address exists in the json array
                // add the tag ID into the tag array
                int check = -1;
                for (int i = 0; i < archive.size(); i++) {
                    object = (JSONObject) archive.get(i);
                    if (object.get("email").equals(email)) {
                        tagIDArray = (JSONArray) object.get("tagID");
                        tagIDArray.add(tagID);
                        check = i;
                        break;
                    }
                }

                // if user emails is not exist in the json array
                // create a new object containing user email address and tag ID in json array
                if (check == -1) {
                    object = new JSONObject();
                    tagIDArray = new JSONArray();

                    tagIDArray.add(tagID);
                    object.put("email",email);
                    object.put("tagID",tagIDArray);
                    archive.add(object);
                }


                FileWriter file = new FileWriter(archiveJson);
                file.write(archive.toJSONString());
                file.flush();
                file.close();


            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }

        } else {
            // if UserHistory.json file is not exist,
            // write the user email address and the post tag ID into a new UserHistory.json file
            JSONObject object = new JSONObject();
            JSONArray tagIDArray = new JSONArray();

            object.put("email",email);
            tagIDArray.add(tagID);
            object.put("tagID",tagIDArray);

            JSONArray archive = new JSONArray();
            archive.add(object);

            try {
                FileWriter file = new FileWriter(archiveJson);
                file.write(archive.toJSONString());
                file.flush();
                file.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Return a list of tag IDs by reading UserArchive.json
     * @param email user email address
     * @return tagID (an integer array list that stores tag IDs)
     */
    public List<Integer> readUserArchive(String email) {
        //An integer array list to store the tag ID
        List<Integer> tagID = new ArrayList<>();
        File historyJson = new File("JsonFiles\\UserArchive.json");
        if (historyJson.exists()) {
            JSONParser jsonParser = new JSONParser();
            try {
                Object obj = jsonParser.parse(new FileReader(historyJson));
                JSONArray archive = (JSONArray) obj;

                // Iterate over archive array to load all IDs that match with the email address
                for (Object object : archive) {
                    if (object instanceof JSONObject) {
                        if (((JSONObject) object).get("email").equals(email)) {
                            JSONArray tagIDArray = (JSONArray) ((JSONObject) object).get("tagID");
                            for (int i = 0; i < tagIDArray.size(); i++) {
                                tagID.add(Math.toIntExact((Long) tagIDArray.get(i)));
                            }
                            break;
                        }
                    }
                }

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
        return tagID;
    }

    /**
     * delete a tag ID based on the user email address in UserArchive.json
     * @param email user email address
     * @param tagID tag ID of a post
     */
    public void deleteUserArchive(String email, int tagID) {
        File archiveJson = new File("JsonFiles\\UserArchive.json");

        if (archiveJson.exists()) {
            // if UserHistory.json file exists,
            // delete the tag id that matches with tagID based on email address
            JSONParser jsonParser = new JSONParser();

            try {

                Object obj = jsonParser.parse(new FileReader(archiveJson));
                JSONArray archive = (JSONArray) obj;

                // if user email address exists in the archive array
                // remove the tag id that matches with the tagID from the tagID array
                for (int i = 0; i < archive.size(); i++) {
                    JSONObject object = (JSONObject) archive.get(i);

                    if (object.get("email").equals(email)) {
                        JSONArray tagIDArray = (JSONArray) object.get("tagID");

                        for (int j = 0; j < tagIDArray.size(); j++) {
                            if (tagIDArray.get(j).equals((long) tagID)) {
                                tagIDArray.remove(j);
                                break;
                            }
                        }
                        break;
                    }
                }


                FileWriter file = new FileWriter(archiveJson);
                file.write(archive.toJSONString());
                file.flush();
                file.close();


            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Add user comment into postComment.json
     * @param tagID tag ID of a post
     * @param username username
     * @param dateTime user comment date
     * @param comment user comment
     */
    @SuppressWarnings("unchecked")
    public static void writePostComment(int tagID, String username, String dateTime, String comment) { //int numOfLike, int numOfDislike) {
        File postCommentJson = new File("JsonFiles\\PostComment.json");

        if (postCommentJson.exists()) {
            // if postCommentJson.json file exists,
            JSONParser jsonParser = new JSONParser();

            try {

                Object obj = jsonParser.parse(new FileReader(postCommentJson));
                JSONArray postArray = (JSONArray) obj;

                JSONObject object;
                JSONArray usernameArray = new JSONArray();
                JSONArray commentArray = new JSONArray();
                JSONArray dateArray = new JSONArray();
//                JSONArray numOfLikeArray = new JSONArray();
//                JSONArray numOfDislikeArray = new JSONArray();

                // if user tag ID exists in the json array
                // add the username, user comment and comment date into their respective json arrays
                int check = -1;
                for (int i = 0; i < postArray.size(); i++) {
                    object = (JSONObject) postArray.get(i);
                    if ((Long) object.get("tagID") == tagID) {
                        usernameArray = (JSONArray) object.get("username");
                        usernameArray.add(username);
                        commentArray = (JSONArray) object.get("comment");
                        commentArray.add(comment);
                        dateArray = (JSONArray) object.get("date");
                        dateArray.add(dateTime);
//                        numOfLikeArray = (JSONArray) object.get("like");
//                        numOfLikeArray.add(dateTime);
//                        numOfDislikeArray = (JSONArray) object.get("dislike");
//                        numOfDislikeArray.add(dateTime);

                        check = i;
                        break;
                    }
                }

                // if tag ID is not exit in the json array
                // create a new object containing tag ID, username, user comment and comment date in json array
                if (check == -1) {
                    object = new JSONObject();
                    usernameArray.add(username);
                    commentArray.add(comment);
                    dateArray.add(dateTime);
//                    numOfLikeArray.add(numOfLike);
//                    numOfDislikeArray.add(numOfDislike);
                    object.put("tagID",tagID);
                    object.put("username",usernameArray);
                    object.put("comment",commentArray);
                    object.put("date",dateArray);
//                    object.put("like",numOfLikeArray);
//                    object.put("dislike",numOfDislikeArray);
                    postArray.add(object);
                }


                FileWriter file = new FileWriter(postCommentJson);
                file.write(postArray.toJSONString());
                file.flush();
                file.close();


            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }

        } else {

            // if postComment.json file is not exist,
            // write the tag ID, username, user comment, comment and comment date into a new postComment.json file
            JSONArray postArray = new JSONArray();

            JSONObject object = new JSONObject();
            JSONArray usernameArray = new JSONArray();
            JSONArray commentArray = new JSONArray();
            JSONArray dateArray = new JSONArray();
//            JSONArray numOfLikeArray = new JSONArray();
//            JSONArray numOfDislikeArray = new JSONArray();

            usernameArray.add(username);
            commentArray.add(comment);
            dateArray.add(dateTime);
            object.put("tagID",tagID);
            object.put("username",usernameArray);
            object.put("comment",commentArray);
            object.put("date",dateArray);
//            object.put("like",numOfLikeArray);
//            object.put("dislike",numOfDislikeArray);

            postArray.add(object);

            try {
                FileWriter file = new FileWriter(postCommentJson);
                file.write(postArray.toJSONString());
                file.flush();
                file.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Return a list containing other lists (username list, comment list and dateTime list)
     * @param tagID tag ID of a post
     * @return list
     */
    public static List<List<String>> readPostComment(int tagID) {
        List<List<String>> list = new ArrayList<>();
        List<String> usernameList = new ArrayList<>();
        List<String> commentList = new ArrayList<>();
        List<String> dateTimeList = new ArrayList<>();
        //List<Integer> numOfLikeList = new ArrayList<>();
        //List<String> numOfDislikeList = new ArrayList<>();

        File postCommentJson = new File("JsonFiles\\PostComment.json");
        if (postCommentJson.exists()) {
            JSONParser jsonParser = new JSONParser();
            try {
                Object obj = jsonParser.parse(new FileReader(postCommentJson));
                JSONArray postArray = (JSONArray) obj;

                // Iterate over jsonArray to load all the username, comment and date in their respective json arrays that match with the tag ID and store in lists
                for (Object object : postArray) {
                    if (object instanceof JSONObject) {
                        if ((Long) ((JSONObject) object).get("tagID") == tagID) {
                            JSONArray usernameArray = (JSONArray) ((JSONObject) object).get("username");
                            JSONArray commentArray = (JSONArray) ((JSONObject) object).get("comment");
                            JSONArray dateArray = (JSONArray) ((JSONObject) object).get("date");
//                            JSONArray numOfLikeArray = (JSONArray) ((JSONObject) object).get("like");
//                            JSONArray numOfDislikeArray = (JSONArray) ((JSONObject) object).get("dislike");

                            for (int i = 0; i < usernameArray.size(); i++) {
                                usernameList.add((String) usernameArray.get(i));
                                commentList.add((String) commentArray.get(i));
                                dateTimeList.add((String) dateArray.get(i));
//                                numOfLikeList.add((String) numOfLikeArray.get(i));
//                                numOfDislikeList.add((String) numOfDislikeArray.get(i));
                            }

                            break;
                        }
                    }
                }

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
        list.add(usernameList);
        list.add(commentList);
        list.add(dateTimeList);
//        list.add(numOfLikeList);
//        list.add(numOfDislikeList);

        return list;
    }

    /**
     * Delete a user comment based on the tag ID, username, user comment and user comment date in postComment.json
     * @param tagID tag ID of a post
     * @param username username
     * @param comment user comment
     * @param date user comment date
     */
    public static void deletePostComment(int tagID, String username, String comment, String date) { //int numOfLike, int numOfDislike) {
        File postCommentJson = new File("JsonFiles\\PostComment.json");

        if (postCommentJson.exists()) {
            // if postComment.json file exits
            JSONParser jsonParser = new JSONParser();

            try {

                Object obj = jsonParser.parse(new FileReader(postCommentJson));
                JSONArray postArray = (JSONArray) obj;

                // if tag ID exists in the json array
                // remove the username, user comment and user comment date that match with the parameters passed it
                for (int i = 0; i < postArray.size(); i++) {
                    JSONObject object = (JSONObject) postArray.get(i);

                    if ((Long)object.get("tagID") == tagID) {
                        JSONArray usernameArray = (JSONArray) object.get("username");
                        JSONArray commentArray = (JSONArray) object.get("comment");
                        JSONArray dateArray = (JSONArray) object.get("date");
//                        JSONArray numOfLikeArray = (JSONArray) ((JSONObject) object).get("like");
//                        JSONArray numOfDislikeArray = (JSONArray) ((JSONObject) object).get("dislike");

                        for (int j = 0; j < usernameArray.size(); j++) {
                            if (usernameArray.get(j).equals(username)) {
                                if (commentArray.get(j).equals(comment)) {
                                    if (dateArray.get(j).equals(date)) {
//                                        if ((Long) numOfLikeArray.get(j) == numOfLike) {
//                                            if ((Long) numOfDislikeArray.get(j) == numOfDislike) {
                                        usernameArray.remove(j);
                                        commentArray.remove(j);
                                        dateArray.remove(j);
//                                                numOfLikeArray.remove(j);
//                                                numOfDislikeArray.remove(j);
                                        break;
//                                            }
//                                        }
                                    }
                                }
                            }
                        }
                        break;
                    }
                }

                FileWriter file = new FileWriter(postCommentJson);
                file.write(postArray.toJSONString());
                file.flush();
                file.close();


            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
