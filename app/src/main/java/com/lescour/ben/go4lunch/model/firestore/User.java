package com.lescour.ben.go4lunch.model.firestore;

import javax.annotation.Nullable;

/**
 * Created by benja on 10/04/2019.
 */
public class User {

    private String uid;
    private String userName;
    @Nullable
    private String userUrlImage;

    public User(String uid, String userName, String userUrlImage) {
        this.uid = uid;
        this.userName = userName;
        this.userUrlImage = userUrlImage;
    }

    //GETTER\\
    public String getUid() { return uid; }
    public String getUserName() { return userName; }
    public String getUserUrlImage() { return userUrlImage; }

    //SETTER\\
    public void setUid(String uid) { this.uid = uid; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setUserUrlImage(String userUrlImage) { this.userUrlImage = userUrlImage; }

}
