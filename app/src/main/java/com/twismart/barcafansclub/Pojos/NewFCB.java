package com.twismart.barcafansclub.Pojos;

import android.graphics.drawable.Drawable;

/**
 * Created by sneyd on 2/1/2017.
 **/
public class NewFCB {
    private Drawable drawableImgNew = null;
    private String title, link, description, imgUrl;

    public NewFCB(String title, String link, String description, String imgUrl) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.imgUrl = imgUrl;
    }

    public Drawable getDrawableImgNew() {
        return drawableImgNew;
    }

    public void setDrawableImgNew(Drawable drawableImgNew) {
        this.drawableImgNew = drawableImgNew;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "NewFCB{" +
                "drawableImgNew=" + drawableImgNew +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
