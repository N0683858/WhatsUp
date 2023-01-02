package com.hamidraza.whatsup;

public class UsersModel {

    public String name;
    public String image;
    public String status;

    public UsersModel() {
    }

    public UsersModel(String name, String image, String status, String thumbnail_img) {
        this.name = name;
        this.image = image;
        this.status = status;
        this.thumbnail_img = thumbnail_img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumbnail_img() {
        return thumbnail_img;
    }

    public void setThumbnail_img(String thumbnail_img) {
        this.thumbnail_img = thumbnail_img;
    }

    public String thumbnail_img;

}
