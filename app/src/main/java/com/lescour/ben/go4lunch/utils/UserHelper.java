package com.lescour.ben.go4lunch.utils;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.lescour.ben.go4lunch.model.firestore.User;

/**
 * Created by benja on 10/04/2019.
 */
public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, User user) {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME).document(uid).set(user);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME).document(uid).get();
    }

    public static Task<QuerySnapshot> getUsersDocuments(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME).get();
    }

    public static Task<QuerySnapshot> getUsersWhoHaveSameChoice(String userChoicePlaceId) {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME).whereEqualTo("userChoicePlaceId", userChoicePlaceId).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateChoice(String uid, String userChoicePlaceId, String userChoiceRestaurantName) {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME).document(uid)
                .update("userChoicePlaceId", userChoicePlaceId,
                        "userChoiceRestaurantName", userChoiceRestaurantName);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME).document(uid).delete();
    }

}
