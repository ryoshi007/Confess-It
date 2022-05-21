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

    /***
     * get the tagID
     * @return tagID
     */
    public int getTagID() {
        return tagID;
    }

    /***
     * set the tagID
     * @param tagID is the primary key of the post
     */
    public void setTagID(int tagID) {
        this.tagID = tagID;
    }

    /***
     * get the Date
     * @return date
     */
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getDislike() {
        return dislike;
    }

    public void setDislike(int dislike) {
        this.dislike = dislike;
    }

    public Json getComment() {
        return comment;
    }

    public void setComment(Json comment) {
        this.comment = comment;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }
}
