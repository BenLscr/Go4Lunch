package com.lescour.ben.go4lunch.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.lescour.ben.go4lunch.R;

import java.util.Arrays;

public class AuthActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_auth);
    }

    private void startSignInActivity(){
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
        Log.e("startSignIn", "dans la méthode");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.isCurrentUserLogged()){
            Log.e("checkUserIsLog", "user found");
            this.launchMainActivity();
        } else {
            Log.e("checkUserIsLog", "no user");
            this.startSignInActivity();
        }
    }

    private void launchMainActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

        IdpResponse response = IdpResponse.fromResultIntent(data);

        Log.e("handleResponseAfter", "after sign in");

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                //this.createUserInFirestore();
                this.launchMainActivity();
                Log.e("ok", "main");
            } else { // ERRORS
                if (response == null) {
                    //Toast.makeText(this, R.string.error_authentication_canceled, Toast.LENGTH_LONG).show();
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

}
