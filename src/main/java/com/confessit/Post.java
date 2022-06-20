package com.confessit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A class that used to store post information
 */
public class Post {

    /**
     * Index of a post
     */
    private int index;

    /**
     * tag ID of a post
     */
    private int tagID;

    /**
     * Submitted time of a post
     */
    private Date datetime;

    /**
     * Post content
     */
    private String content;

    /**
     * Picture path of a post image
     */
    private String picturePath;

    /**
     * Number of likes of a post
     */
    private int like;

    /**
     * Number of dislikes of a post
     */
    private int dislike;

    /**
     * Post's comment
     */
    private String comment;

    /**
     * True if the post is approved
     * False if the post is not approved yet
     */
    private boolean isApproved;

    /**
     * Approval time of a post
     */
    private Date approvalTime;

    /**
     * True if the post is displayed on the confession page
     * False if the post is not displayed yet
     */
    private boolean isDisplayed;

    /**
     * Consists of the tag id of the posts that are replying to this post
     */
    private String reply;

    /**
     * The postID that the post is replying to
     */
    private int replyToPostID = 0;

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
     * @param replyToPostID is the postID that the post is replying to
     */
    public Post(int index, int tagID, Date datetime, String content, String picturePath, int like, int dislike,
                String comment, boolean isApproved, Date approvalTime, boolean isDisplayed, String reply, int replyToPostID) {
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
        this.replyToPostID = replyToPostID;
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
     * @param replyToPostID is the postID that the post is replying to
     */
    public Post(int index, int tagID, Date datetime, String content, int like, int dislike, String comment,
                boolean isApproved, Date approvalTime, boolean isDisplayed, String reply, int replyToPostID) {
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
        this.replyToPostID = replyToPostID;
    }

    /**
     * To obtain the approval date and time of the post
     * @return Date object of the approval time
     */
    public Date getApprovalTime() {
        return approvalTime;
    }

    /**
     * To set the display status of the approved post
     * @return the display status of the approved post
     */
    public boolean isDisplayed() {
        return isDisplayed;
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

    /**
     * get the content of the post
     * @return the post's content
     */
    public String getContent() {
        return content;
    }

    /**
     * set the content
     * @param content is the content in the post.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * get the file path that leads to the picture
     * @return the file path of the picture
     */
    public String getPicturePath() {
        return picturePath;
    }

    /**
     * get the like amount that the post obtained
     * @return the amount of like in the form of integer
     */
    public int getLike() {
        return like;
    }

    /**
     * set the like amount
     * @param like is the amount of like that the post received.
     */
    public void setLike(int like) {
        this.like = like;
    }

    /**
     * get the dislike amount that the post obtained
     * @return the dislike amount in the form of integer
     */
    public int getDislike() {
        return dislike;
    }

    /**
     * set the dislike amount
     * @param dislike is the amount of dislike that the post received.
     */
    public void setDislike(int dislike) {
        this.dislike = dislike;
    }

    /**
     * get the comment that the post received
     * @return the comment of the post
     */
    public String getComment() {
        return comment;
    }

    /**
     * set the comment
     * @param comment is the comment that the post received.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }


    /**
     * get the replyTo value
     * @return the integer value of replyTo
     */
    public int getReplyToPostID() {
        return replyToPostID;
    }

    /**
     * get the string representation of the post
     * @return the representation of post in the form of string
     */
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
