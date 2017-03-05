package com.twismart.barcafansclub.Pojos;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import com.twismart.barcafansclub.Util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sneyd on 2/1/2017.
 **/
public class Post implements Parcelable {
    private Drawable drawableImgUser = null;
    private List<Drawable> listDrawablesImgsPost = new ArrayList<>();
    private String postId, userId, text, date, dateText;
    private String imgUrl;
    private String myVote = Constants.Thumb.NEITHER.value;

    private int likes, dislikes;

    private String nameUser = "", imgUser = "", imgUserCover = "";

    public Post(){

    }
    public Post(String id, String postedById, String date, String dateText, String text, String imgUrl) {
        this.postId = id;
        this.userId = postedById;
        this.date = date;
        this.dateText = dateText;
        this.text = text;
        this.imgUrl = imgUrl;
    }

    private Post(Parcel in) {
        userId = in.readString();
        postId = in.readString();
        date = in.readString();
        dateText = in.readString();
        text = in.readString();
        imgUrl = in.readString();
        myVote = in.readString();
        likes = in.readInt();
        dislikes = in.readInt();
        nameUser = in.readString();
        imgUser = in.readString();
        imgUserCover = in.readString();
    }

    public List<Drawable> getListDrawablesImgsPost() {
        return listDrawablesImgsPost;
    }

    public void setListDrawablesImgsPost(List<Drawable> listDrawablesImgsPost) {
        this.listDrawablesImgsPost = listDrawablesImgsPost;
    }

    public Drawable getDrawableImgUser() {
        return drawableImgUser;
    }

    public void setDrawableImgUser(Drawable drawableImgUser) {
        this.drawableImgUser = drawableImgUser;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(userId);
        out.writeString(postId);
        out.writeString(date);
        out.writeString(dateText);
        out.writeString(text);
        out.writeString(imgUrl);
        out.writeString(myVote);
        out.writeInt(likes);
        out.writeInt(dislikes);
        out.writeString(nameUser);
        out.writeString(imgUser);
        out.writeString(imgUserCover);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public void setBasicInfo(String nameUser, String imgUser, String imgUserCover){
        this.nameUser = nameUser;
        this.imgUser = imgUser;
        this.imgUserCover = imgUserCover;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMyVote() {
        return myVote;
    }

    public void setMyVote(String myVote) {
        this.myVote = myVote;
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

    public String getImgUser() {
        return imgUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public void setImgUser(String imgUser) {
        this.imgUser = imgUser;
    }

    public String getImgUserCover() {
        return imgUserCover;
    }

    public void setImgUserCover(String imgUserCover) {
        this.imgUserCover = imgUserCover;
    }

    public String getNameUser() {
        return nameUser;
    }

    public boolean hasBasicInfo(){
        return !nameUser.equals("");
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getDateText() {
        return dateText;
    }

    public void setDateText(String dateText) {
        this.dateText = dateText;
    }
}
