package com.lazypanda.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lazypanda.databinding.ActivityEmailVerificationBinding;

public class EmailVerificationActivity extends AppCompatActivity {

    private ActivityEmailVerificationBinding binding;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmailVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        handler = new Handler(Looper.getMainLooper());
        runnable =
                new Runnable() {
                    @Override
                    public void run() {
                        if (user != null) {
                            user.reload()
                                    .addOnCompleteListener(
                                            new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    if (task.isSuccessful()) {
                                                        if (user.isEmailVerified()) {
                                                            handler.removeCallbacks(runnable);
                                                            startActivity(
                                                                    new Intent(
                                                                            EmailVerificationActivity
                                                                                    .this,
                                                                            DashboardActivity.class));
                                                            finish();
                                                        }
                                                    } else {
                                                        Toast.makeText(
                                                                        EmailVerificationActivity
                                                                                .this,
                                                                        task.getException()
                                                                                .getMessage(),
                                                                        Toast.LENGTH_LONG)
                                                                .show();
                                                    }
                                                }
                                            });
                        }
                        handler.postDelayed(this, 3000);
                    }
                };

        handler.post(runnable);
    }

    // resend email
    public void resendEmailLink(View v) {
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Email verification link sent successfully.
                                        // Display a message to the user.
                                        Toast.makeText(
                                                        EmailVerificationActivity.this,
                                                        "Verification email sent.",
                                                        Toast.LENGTH_SHORT)
                                                .show();
                                    } else {
                                        // Failed to send verification email. Handle the error.
                                        Toast.makeText(
                                                        EmailVerificationActivity.this,
                                                        "Failed to send verification email.",
                                                        Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {

                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(
                                                    EmailVerificationActivity.this,
                                                    e.getMessage(),
                                                    Toast.LENGTH_LONG)
                                            .show();
                                }
                            });
        }
    }

    // manually allow
    public void manuallyAllow(View view) {
        user.reload();
        if (user != null) {
            if (user.isEmailVerified()) {
                startActivity(new Intent(EmailVerificationActivity.this, DashboardActivity.class));
                finish();
            } else {
                Toast.makeText(
                                EmailVerificationActivity.this,
                                "Please Verify Email",
                                Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    // going back
    public void goBack(View v) {
        onBackPressed();
        handler.removeCallbacks(runnable);
    }
}
