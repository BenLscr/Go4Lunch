package com.lescour.ben.go4lunch.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.model.firestore.User;
import com.lescour.ben.go4lunch.utils.UserHelper;

import java.util.Arrays;

public class AuthActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_auth);
        if (this.isCurrentUserLogged()){
            this.retrievesIsData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!this.isCurrentUserLogged()){
            this.startSignInActivity();
        }
    }

    private void startSignInActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.FacebookBuilder().build(),
                                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                                        new AuthUI.IdpConfig.TwitterBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.go4lunch_ic_sign)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                this.retrievesIsData();
            } else { // ERRORS
                if (response == null) {
                    //Toast.makeText(this, R.string.error_authentication_canceled, Toast.LENGTH_LONG).show();
                    //TODO ESSAYER DE REMOVE LE CODE ERROR
                    Log.e("fail", "annulé");
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    //Toast.makeText(this, R.string.error_authentication_canceled, Toast.LENGTH_LONG).show();
                    Log.e("fail", "no network");
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    //Toast.makeText(this, R.string.error_authentication_canceled, Toast.LENGTH_LONG).show();
                    Log.e("fail", "error inco");
                }
            }
        }
    }

    private void retrievesIsData() {
        UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    user = documentSnapshot.toObject(User.class);
                    launchHomeActivity();
                } else {
                    createUserInFirestore();
                }
            }
        });
    }

    private void createUserInFirestore(){
        user = new User(getCurrentUser().getUid(),
                getCurrentUser().getDisplayName(),
                (getCurrentUser().getPhotoUrl() != null) ? getCurrentUser().getPhotoUrl().toString() : null);
        UserHelper.createUser(user.getUid(), user).addOnFailureListener(onFailureListener());
        launchHomeActivity();
    }

    private void launchHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

}
