package com.lescour.ben.go4lunch.model.firestore;

import android.os.Parcel;
import android.os.Parcelable;

import javax.annotation.Nullable;

/**
 * Created by benja on 10/04/2019.
 */
public class User implements Parcelable {

    private String uid;
    private String userName;
    @Nullable
    private String userUrlImage;
    private String userChoice = "";

    public User () { }

    public User(String uid, String userName, String userUrlImage) {
        this.uid = uid;
        this.userName = userName;
        this.userUrlImage = userUrlImage;
    }

    //PARCELABLE\\
    protected User(Parcel in) {
        uid = in.readString();
        userName = in.readString();
        userUrlImage = in.readString();
        userChoice = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(userName);
        dest.writeString(userUrlImage);
        dest.writeString(userChoice);
    }

    //GETTER\\
    public String getUid() { return uid; }
    public String getUserName() { return userName; }
    public String getUserUrlImage() { return userUrlImage; }
    public String getUserChoice() { return userChoice; }

    //SETTER\\
    public void setUid(String uid) { this.uid = uid; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setUserUrlImage(String userUrlImage) { this.userUrlImage = userUrlImage; }
    public void setUserChoice(String userChoice) { this.userChoice = userChoice; }

}
