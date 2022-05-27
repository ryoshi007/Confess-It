package com.confessit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Post {
    private int index;
    private int tagID;
    private Date datetime;
    private String content;
    private String picturePath;
    private int like;
    private int dislike;
    private String comment;
    private boolean isApproved;
    private Date approvalTime;
    private boolean isDisplayed;
    private String reply;

    /**
     * Constructor for a submitted post with picture
     * @param index is the unique identifier for the post, used for administrative purpose
     * @param tagID is the unique key of the post. It is unique and use for retrieval purpose.
     * @param datetime is the date and time when the post is submitted.
     * @param content is the content in the post.
     * @param picturePath is the file path that leads to the picture.
     * @param like is the amount of like that the post received.
     * @param dislike is the amount of dislike that the post received.
     * @param comment is the comment that the post received.
     * @param isApproved is the status of the post, True if the post is approved by the administrator, False if otherwise.
     * @param approvalTime is the time when the post is approved
     * @param isDisplayed is the status of the approved has been displayed or not
     * @param reply is the string consists of the tag id of the posts that are replying to this post
     */
    public Post(int index, int tagID, Date datetime, String content, String picturePath, int like, int dislike,
                String comment, boolean isApproved, Date approvalTime, boolean isDisplayed, String reply) {
        this.index = index;
        this.tagID = tagID;
        this.datetime = datetime;
        this.content = content;
        this.picturePath = picturePath;
        this.like = like;
        this.dislike = dislike;
        this.comment = comment;
        this.isApproved = isApproved;
        this.approvalTime = approvalTime;
        this.isDisplayed = isDisplayed;
        this.reply = reply;
    }

    /**
     * Constructor for a submitted post without picture
     * @param index is the unique identifier for the post, used for administrative purpose
     * @param tagID is the unique key of the post. It is unique and use for retrieval purpose.
     * @param datetime is the date and time when the post is submitted.
     * @param content is the content in the post.
     * @param like is the amount of like that the post received.
     * @param dislike is the amount of dislike that the post received.
     * @param comment is the comment that the post received.
     * @param isApproved is the status of the post, True if the post is approved by the administrator, False if otherwise.
     * @param approvalTime is the time when the post is approved
     * @param isDisplayed is the status of the approved has been displayed or not
     * @param reply is the string consists of the tag id of the posts that are replying to this post
     */
    public Post(int index, int tagID, Date datetime, String content, int like, int dislike, String comment,
                boolean isApproved, Date approvalTime, boolean isDisplayed, String reply) {
        this.index = index;
        this.tagID = tagID;
        this.datetime = datetime;
        this.content = content;
        this.like = like;
        this.dislike = dislike;
        this.comment = comment;
        this.isApproved = isApproved;
        this.approvalTime = approvalTime;
        this.isDisplayed = isDisplayed;
        this.reply = reply;
    }

    /**
     * To set the date and time when the post is submitted
     * @param datetime is the submitted time
     */
    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    /**
     * To obtain the approval date and time of the post
     * @return Date object of the approval time
     */
    public Date getApprovalTime() {
        return approvalTime;
    }

    /**
     * To set the approval date and time of approved post
     * @param approvalTime the approval time of post
     */
    public void setApprovalTime(Date approvalTime) {
        this.approvalTime = approvalTime;
    }

    /**
     * To set the display status of the approved post
     * @return the display status of the approved post
     */
    public boolean isDisplayed() {
        return isDisplayed;
    }

    /**
     * Set the display status of the approved post
     * @param displayed is the boolean value, 0 if the post has not been displayed (not popped from the PendingQueue),
     * 1 otherwise
     */
    public void setDisplayed(boolean displayed) {
        isDisplayed = displayed;
    }

    /**
     * To get the reply of the post
     * @return Json object consists of tag id of the posts that reply to this post
     */
    public String getReply() {
        return reply;
    }

    /**
     * To set the reply of the post
     * @param reply is Json object containing the tag id of the posts
     */
    public void setReply(String reply) {
        this.reply = reply;
    }

    /**
     * To get the index of the post
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * get the tagID of the post
     * @return tagID
     */
    public int getTagID() {
        return tagID;
    }

    /**
     * set the tagID
     * @param tagID is the unique key of the post. It is unique and use for retrieval purpose.
     */
    public void setTagID(int tagID) {
        this.tagID = tagID;
    }

    /**
     * get the Date when the post is submitted
     * @return datetime
     */
    public Date getDatetime() {
        return datetime;
    }


    /***
     * set the Date
     * @param datetime is the date and time when the post is submitted.
     */
    public void setDate(Date datetime) {
        this.datetime = datetime;
    }

    /***
     * get the content of the post
     * @return the post's content
     */
    public String getContent() {
        return content;
    }

    /***
     * set the content
     * @param content is the content in the post.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /***
     * get the file path that leads to the picture
     * @return the file path of the picture
     */
    public String getPicturePath() {
        return picturePath;
    }

    /***
     * set the file path
     * @param picturePath is the file path that leads to the picture.
     */
    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    /***
     * get the like amount that the post obtained
     * @return the amount of like in the form of integer
     */
    public int getLike() {
        return like;
    }

    /***
     * set the like amount
     * @param like is the amount of like that the post received.
     */
    public void setLike(int like) {
        this.like = like;
    }

    /***
     * get the dislike amount that the post obtained
     * @return the dislike amount in the form of integer
     */
    public int getDislike() {
        return dislike;
    }

    /***
     * set the dislike amount
     * @param dislike is the amount of dislike that the post received.
     */
    public void setDislike(int dislike) {
        this.dislike = dislike;
    }

    /***
     * get the comment that the post received
     * @return the comment of the post
     */
    public String getComment() {
        return comment;
    }

    /***
     * set the comment
     * @param comment is the comment that the post received.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /***
     * set the approval status of the post, True is the post is approved by the admin, False otherwise
     * @return boolean value of approval status
     */
    public boolean isApproved() {
        return isApproved;
    }

    /***
     * set the approval status of the post
     * @param approved is the status of the post, True if the post is approved by the administrator, False if otherwise.
     */
    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = dateFormat.format(datetime);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String time = timeFormat.format(datetime);

        DateFormat approvalFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String approval = approvalFormat.format(approvalTime);

        return "Index: " + index + "\nTag ID: " + tagID + "\nDate: " + date + "\nTime: " + time + "\nContent: "
                + content + "\nFile Path: " + picturePath + "\nLike Amount: " + like + "\nDislike Amount: " + dislike
                + "\nComment: " + comment + "\nApproval Time: " + approval + "\nReply: " + reply;
    }
}
