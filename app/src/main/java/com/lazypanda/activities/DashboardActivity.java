package com.lazypanda.activities;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigationrail.NavigationRailView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.content.Intent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.lazypanda.databinding.ActivityDashboardBinding;
import com.lazypanda.R;
//import com.lazypanda.notifications.FirebaseMessaging;
import com.lazypanda.notifications.Token;

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;
    private FirebaseAuth fAuth;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTrans;
    Fragment fragment = null;
    private DrawerLayout drawerLayout;
    private RelativeLayout navRail;
    private GestureDetectorCompat gestureDetector;
    ImageView header_Profile_Pic;
    String mUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fAuth = FirebaseAuth.getInstance();
        
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
        drawerLayout.setDrawerElevation(0);
        navRail = findViewById(R.id.nav_rail);
        
        // Create a gesture detector
        gestureDetector = new GestureDetectorCompat(this, new MyGestureListener());
        
        // Attach a touch listener to your main content layout
        FrameLayout mainContent = findViewById(R.id.content);
        mainContent.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true; // Consume the event
        });

        if (fragment == null) {
            fragmentManager = getSupportFragmentManager();
            fragmentTrans = fragmentManager.beginTransaction();
            fragmentTrans.replace(R.id.content, new HomeFragment());
            fragmentTrans.commit();
        }

        NavigationRailView navigationRailView = findViewById(R.id.navigation_rail);
        navigationRailView.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (item.getItemId() == R.id.home) {
                            fragment = new HomeFragment();
                        } else if (item.getItemId() == R.id.profile) {
                            fragment = new ProfileFragment();
                        } else if (item.getItemId() == R.id.users) {
                            fragment = new UsersFragment();
                        } else if (item.getItemId() == R.id.nav_delete) {
                        }
                        fragmentManager = getSupportFragmentManager();
                        fragmentTrans = fragmentManager.beginTransaction();
                        fragmentTrans.replace(R.id.content, fragment);
                        fragmentTrans.commit();
                        return true;
                    }
                });
        checkUserStatus();
        //updateToken(FirebaseMessaging.getInstance().getToken());
        Task<String> refreshedToken = FirebaseMessaging.getInstance().getToken();
        refreshedToken.addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()){
                    String token = task.getResult();
                    updateToken(token);
                }else {}
            }
        });
    }
    
    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
        // TODO: Implement this method
    }
    
    
    public void updateToken(String token) {
    	DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mToken = new Token(token);
        ref.child(mUID).setValue(mToken);
    }
    
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // Define your threshold for swipe detection
            int swipeThreshold = 100;

            // Calculate the difference in X coordinates
            float deltaX = e2.getX() - e1.getX();

            if (deltaX > swipeThreshold) {
                // Swipe from left to right, show navigation rail
                drawerLayout.openDrawer(GravityCompat.START);
            } else if (deltaX < -swipeThreshold) {
                // Swipe from right to left, hide navigation rail
                drawerLayout.closeDrawer(GravityCompat.START);
            }

            return true;
        }
    }

    // check user
    private void checkUserStatus() {
        FirebaseUser user = fAuth.getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            mUID = user.getUid();
            
            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", mUID);
            editor.apply();
        } else {
            startActivity(new Intent(DashboardActivity.this, WelcomeActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }
}
