package com.twismart.barcafansclub.Pojos;

import android.graphics.drawable.Drawable;

import com.twismart.barcafansclub.Util.Constants;

/**
 * Created by sneyd on 2/1/2017.
 **/
public class Comment {
    private Drawable drawableImgUser = null;
    private String postId, commentId, userId, imgUrl;
    private String text, date;
    private String myVote = Constants.Thumb.NEITHER.value;

    private int likes, dislikes;
    private String nameUser = "", imgUser;

    public Comment(String userId, String commentId, String postId, String date, String text, String imgUrl) {
        this.userId = userId;
        this.commentId = commentId;
        this.postId = postId;
        this.date = date;
        this.text = text;
        this.imgUrl = imgUrl;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Drawable getDrawableImgUser() {
        return drawableImgUser;
    }

    public void setDrawableImgUser(Drawable drawableImgUser) {
        this.drawableImgUser = drawableImgUser;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setBasicInfo(String nameUser, String imgUser){
        this.nameUser = nameUser;
        this.imgUser = imgUser;
    }

    public boolean hasBasicInfo(){
        return !nameUser.equals("");
    }

    public String getMyVote() {
        return myVote;
    }

    public void setMyVote(String myVote) {
        this.myVote = myVote;
    }

    public String getImgUser() {
        return imgUser;
    }

    public void setImgUser(String imgUser) {
        this.imgUser = imgUser;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }
}
