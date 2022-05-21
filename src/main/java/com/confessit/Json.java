package com.confessit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Json {

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

                JSONObject object = new JSONObject();
                JSONArray tagIDArray = new JSONArray();

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

                JSONObject object = new JSONObject();
                JSONArray tagIDArray = new JSONArray();

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
     * Return a list of tag IDs by reading UserHistory.json
     * @param email user email address
     * @return tagID (an integer array list that stores tag IDs)
     */
    public static List<Integer> readUserHistory(String email) {
        List<Integer> tagID = new ArrayList<>();
        File historyJson = new File("JsonFiles\\UserHistory.json");
        if (historyJson.exists()) {
            JSONParser jsonParser = new JSONParser();
            try {
                Object obj = jsonParser.parse(new FileReader(historyJson));
                JSONArray history = (JSONArray) obj;

                // Iterate over history array to load all tag IDs that match with the email address
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
     * Return a list of tag IDs by reading UserArchive.json
     * @param email user email address
     * @return tagID (an integer array list that stores tag IDs)
     */
    public static List<Integer> readUserArchive(String email) {
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
     * delete a tag ID based on the user email address in UserHistory.json
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
}
