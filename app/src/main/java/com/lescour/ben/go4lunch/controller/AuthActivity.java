package com.lescour.ben.go4lunch.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.lescour.ben.go4lunch.R;
import com.lescour.ben.go4lunch.utils.UserHelper;

import java.util.Arrays;

import androidx.annotation.NonNull;

public class AuthActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_auth);
        if (this.isCurrentUserLogged()){
            this.launchHomeActivity();
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
                                        new AuthUI.IdpConfig.GoogleBuilder().build()))
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
                this.createUserInFirestore();
                this.launchHomeActivity();
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

    private void createUserInFirestore(){
        if (this.getCurrentUser() != null){
            String uid = this.getCurrentUser().getUid();
            String username = this.getCurrentUser().getDisplayName();
            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;

            UserHelper.createUser(uid, username, urlPicture).addOnFailureListener(this.onFailureListener());
        }
    }

    private void launchHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

}
