package com.confessit;

import java.util.Date;

public class Post {
    private int tagID;
    private Date date;
    private Date time;
    private String content;
    private String picturePath;
    private int like;
    private int dislike;
    private Json comment;
    private boolean isApproved;

    /***
     *
     * @param tagID is the primary key of the post. It is unique and use for retrieval purpose.
     * @param date is the date when the post is submitted.
     * @param time is the time when the post is submitted.
     * @param content is the content in the post.
     * @param picturePath is the file path that leads to the picture.
     * @param like is the amount of like that the post received.
     * @param dislike is the amount of dislike that the post received.
     * @param comment is the comment that the post received.
     * @param isApproved is the status of the post, True if the post is approved by the administrator, False if otherwise.
     */
    public Post(int tagID, Date date, Date time, String content, String picturePath, int like, int dislike, Json comment, boolean isApproved) {
        this.tagID = tagID;
        this.date = date;
        this.time = time;
        this.content = content;
        this.picturePath = picturePath;
        this.like = like;
        this.dislike = dislike;
        this.comment = comment;
        this.isApproved = isApproved;
    }

    public Post(int tagID, Date date, Date time, String content, int like, int dislike, boolean isApproved) {
        this.tagID = tagID;
        this.date = date;
        this.time = time;
        this.content = content;
        this.like = like;
        this.dislike = dislike;
        this.isApproved = isApproved;
    }

    /***
     * get the tagID of the post
     * @return tagID
     */
    public int getTagID() {
        return tagID;
    }

    /***
     * set the tagID
     * @param tagID
     */
    public void setTagID(int tagID) {
        this.tagID = tagID;
    }

    /***
     * get the Date when the post is submitted
     * @return date
     */
    public Date getDate() {
        return date;
    }


    /***
     * set the Date
     * @param date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /***
     * get the time when the post is submitted
     * @return
     */
    public Date getTime() {
        return time;
    }

    /***
     * set the time
     * @param time
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /***
     * get the content of the post
     * @return
     */
    public String getContent() {
        return content;
    }

    /***
     * set the content
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /***
     * get the file path that leads to the picture
     * @return
     */
    public String getPicturePath() {
        return picturePath;
    }

    /***
     * set the file path
     * @param picturePath
     */
    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    /***
     * get the like amount that the post obtained
     * @return
     */
    public int getLike() {
        return like;
    }

    /***
     * set the like amount
     * @param like
     */
    public void setLike(int like) {
        this.like = like;
    }

    /***
     * get the dislike amount that the post obtained
     * @return
     */
    public int getDislike() {
        return dislike;
    }

    /***
     * set the dislike amount
     * @param dislike
     */
    public void setDislike(int dislike) {
        this.dislike = dislike;
    }

    /***
     * get the comment that the post received
     * @return
     */
    public Json getComment() {
        return comment;
    }

    /***
     * set the comment
     * @param comment
     */
    public void setComment(Json comment) {
        this.comment = comment;
    }

    /***
     * set the approval status of the post, True is the post is approved by the admin, False otherwise
     * @return
     */
    public boolean isApproved() {
        return isApproved;
    }

    /***
     * set the approval status of the post
     * @param approved
     */
    public void setApproved(boolean approved) {
        isApproved = approved;
    }
}
