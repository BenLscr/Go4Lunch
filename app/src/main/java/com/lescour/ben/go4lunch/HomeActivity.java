package com.lescour.ben.go4lunch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by benja on 15/03/2019.
 */
public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    @BindView(R.id.activity_home_toolbar) Toolbar toolbar;
    @BindView(R.id.activity_home_drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.activity_home_nav_view) NavigationView navigationView;
    @BindView(R.id.navigation) BottomNavigationView navigation;

    private ProgressDialog mProgress;
    private View header;

    private static final int SIGN_OUT_TASK = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        this.configureToolbar();

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        this.configureDrawerLayout();
        this.configureNavigationView();

        this.initProgressDialog();
        this.updateMainMenuWithUserInfo();
    }

    private void configureToolbar() {
        setSupportActionBar(toolbar);
    }

    //BOTTOM TOOLBAR\\
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_map:
                    return true;
                case R.id.navigation_list_view:
                    return true;
                case R.id.navigation_workmates:
                    return true;
            }
            return false;
        }
    };

    //MENU TOOLBAR\\
    /**
     * Inflate the menu and add it to the Toolbar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /**
     * Handle actions on menu items.
     * @param item Item selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_activity_home_search:
                Toast.makeText(this, "Button not available", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //MAIN MENU\\
    private void configureDrawerLayout(){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView(){
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
    }

    /**
     * When an item is selected. Display the associate fragment in the ViewPager.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.activity_home_drawer_your_lunch:
                break;
            case R.id.activity_home_drawer_settings:
                break;
            case R.id.activity_home_drawer_logout:
                mProgress.show();
                this.signOutUserFromFirebase();
                break;
            default:
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Close the NavigationDrawer with the button back
     */
    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return aVoid -> {
            switch (origin){
                case SIGN_OUT_TASK:
                    mProgress.dismiss();
                    finish();
                    break;
                default:
                    break;
            }
        };
    }

    private void initProgressDialog(){
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("ondestroy", "destroy");
    }

    private void updateMainMenuWithUserInfo(){

        if (this.getCurrentUser() != null) {

            ImageView userImage = (ImageView) header.findViewById(R.id.user_image);
            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(userImage);
            }
            //Get email & username from Firebase
            String email = TextUtils.isEmpty(getCurrentUser().getEmail()) ? getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
            String name = TextUtils.isEmpty(getCurrentUser().getDisplayName()) ? getString(R.string.info_no_name_found) : this.getCurrentUser().getDisplayName();

            //Update views with data
            TextView userMail = (TextView) header.findViewById(R.id.user_mail);
            userMail.setText(email);

            TextView userName = (TextView) header.findViewById(R.id.user_name);
            userName.setText(name);
        }
    }
}
