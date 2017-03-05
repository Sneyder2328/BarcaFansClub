package com.twismart.barcafansclub.Pojos;

/**
 * Created by sneyd on 2/14/2017.
 **/

public class Notification {
    public static final int NEW_FOLLOWER = 1, LIKED_MY_POST = 2, LIKED_MY_COMMENT = 3, COMMENT_MY_POST = 4;

    private String notificationReceiver, notificationSender, staffId, date, name, imgProfile, imgCover;
    private int notificationType;

    public Notification(String notificationSender, String staffId, String notificationType, String date, String name, String imgProfile, String imgCover) {
        this.notificationSender = notificationSender;
        this.staffId = staffId;
        this.date = date;
        this.name = name;
        this.imgProfile = imgProfile;
        this.imgCover = imgCover;
        if(notificationType.equals("newFollower")){
            this.notificationType = 1;
        } else if(notificationType.equals("likeMyPost")){
            this.notificationType = 2;
        } else if(notificationType.equals("likeMyComment")){
            this.notificationType = 3;
        } else if(notificationType.equals("commentMyPost")){
            this.notificationType = 4;
        }
    }

    public String getImgCover() {
        return imgCover;
    }

    public void setImgCover(String imgCover) {
        this.imgCover = imgCover;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgProfile() {
        return imgProfile;
    }

    public void setImgProfile(String imgProfile) {
        this.imgProfile = imgProfile;
    }

    public String getNotificationReceiver() {
        return notificationReceiver;
    }

    public void setNotificationReceiver(String notificationReceiver) {
        this.notificationReceiver = notificationReceiver;
    }

    public String getNotificationSender() {
        return notificationSender;
    }

    public void setNotificationSender(String notificationSender) {
        this.notificationSender = notificationSender;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
