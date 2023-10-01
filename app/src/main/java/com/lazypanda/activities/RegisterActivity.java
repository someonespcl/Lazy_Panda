package com.lazypanda.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.common.internal.IAccountAccessor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lazypanda.R;
import com.lazypanda.databinding.ActivityRegisterBinding;
import java.util.HashMap;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    Boolean passwordVisible = false;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        // dialog
        dialog = new Dialog(RegisterActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
        dialog.setCancelable(false);

        // password toggle
        binding.passwordInput.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        final int Right = 2;
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            if (event.getRawX()
                                    >= binding.passwordInput.getRight()
                                            - binding.passwordInput
                                                    .getCompoundDrawables()[Right]
                                                    .getBounds()
                                                    .width()) {
                                int selection = binding.passwordInput.getSelectionEnd();
                                if (passwordVisible.booleanValue()) {
                                    binding.passwordInput
                                            .setCompoundDrawablesRelativeWithIntrinsicBounds(
                                                    0, 0, R.drawable.eye_close, 0);
                                    binding.passwordInput.setTransformationMethod(
                                            PasswordTransformationMethod.getInstance());
                                    // passwordVisibile = false;
                                } else {
                                    binding.passwordInput
                                            .setCompoundDrawablesRelativeWithIntrinsicBounds(
                                                    0, 0, R.drawable.eye_open, 0);
                                    binding.passwordInput.setTransformationMethod(
                                            HideReturnsTransformationMethod.getInstance());
                                    // passwordVisibile = true;
                                }
                                passwordVisible = !passwordVisible;
                                binding.passwordInput.setSelection(selection);
                                return true;
                            }
                        }
                        return false;
                    }
                });

        // text design
        SpannableString txtSub =
                new SpannableString("Start your Journey with a simple Registration.");
        SpannableString haveAc = new SpannableString("Already Have Account ? Login Now.");

        Typeface poppinsBold = Typeface.createFromAsset(getAssets(), "poppins_bold.ttf");
        Typeface poppinsExtraB = Typeface.createFromAsset(getAssets(), "poppins_extrabold.ttf");
        TypefaceSpan poppinsB = new TypefaceSpan(poppinsBold);
        TypefaceSpan poppinsExtraBS = new TypefaceSpan(poppinsExtraB);

        ForegroundColorSpan redColor = new ForegroundColorSpan(Color.parseColor("#FF2C4B"));
        ForegroundColorSpan yellow = new ForegroundColorSpan(Color.parseColor("#FFC100"));

        RelativeSizeSpan small = new RelativeSizeSpan(1.39f);
        RelativeSizeSpan medium = new RelativeSizeSpan(1.6f);
        RelativeSizeSpan large = new RelativeSizeSpan(1.8f);

        // txtRegister.setSpan(textSize, 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // txtRegister.setSpan(yellow, 10, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // txtRegister.setSpan(small, 10, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // txtRegister.setSpan(poppinsB, 10, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtSub.setSpan(yellow, 33, 45, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // txtRegister.setSpan(large, 19, 25, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtSub.setSpan(poppinsB, 33, 45, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtSub.setSpan(new UnderlineSpan(), 33, 45, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // txtSub.setSpan(new UnderlineSpan(), 11, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan click =
                new ClickableSpan() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    }
                };

        haveAc.setSpan(new UnderlineSpan(), 23, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        haveAc.setSpan(redColor, 23, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        haveAc.setSpan(poppinsB, 23, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // haveAc.setSpan(click, 23, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.subTxt.setText(txtSub);
        binding.loginAc.setText(haveAc);
        // binding.loginAc.setMovementMethod(LinkMovementMethod.getInstance());

        binding.registerBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (binding.emailInput.getText().toString().trim().isEmpty()) {
                            binding.emailInput.setError("Cannot Be Empty");
                            binding.emailInput.setFocusable(true);
                        } else if (!Patterns.EMAIL_ADDRESS
                                .matcher(binding.emailInput.getText().toString().trim())
                                .matches()) {
                            binding.emailInput.setError("Invalid Email");
                            binding.emailInput.setFocusable(true);
                        } else if (binding.phnNInput.getText().toString().trim().isEmpty()) {
                            binding.phnNInput.setError("Cannot Be Empty");
                            binding.phnNInput.setFocusable(true);
                        } else if (binding.nameInput.getText().toString().trim().isEmpty()) {
                            binding.nameInput.setError("Cannot Be Empty");
                            binding.nameInput.setFocusable(true);
                        } else if (binding.passwordInput.getText().toString().isEmpty()) {
                            binding.passwordInput.setError("Cannot Be Empty");
                            binding.passwordInput.setFocusable(true);
                        } else if (binding.passwordInput.getText().toString().trim().length() < 6) {
                            binding.passwordInput.setError("Must Be >6");
                            binding.passwordInput.setFocusable(true);
                        } else {
                            registerUser(
                                    binding.emailInput.getText().toString().trim(),
                                    binding.passwordInput.getText().toString().trim()
                            );
                        }
                    }
                });
    }

    // register user with email password
    public void registerUser(String email, String password) {
        dialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    mAuth.getCurrentUser()
                                            .sendEmailVerification()
                                            .addOnCompleteListener(
                                                    new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(
                                                                @NonNull Task<Void> t) {
                                                            if (t.isSuccessful()) {
                                                                FirebaseUser user = mAuth.getCurrentUser();
                                                                
                                                                String email = user.getEmail();
                                                                String uId = user.getUid();
                                                                HashMap<Object, String> data = new HashMap<>();
                                                                
                                                                data.put("email", email);
                                                                data.put("uId", uId);
                                                                data.put("name", binding.nameInput.getText().toString().trim());
                                                                data.put("phoneN", binding.phnNInput.getText().toString().trim());
                                                                data.put("image", "");
                                                                data.put("onlineStatus", "online");
                                                                data.put("typingTo", "noOne");
                                                                data.put("password", password);
                                        
                                                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                                DatabaseReference databaseRefer = database.getReference("Users");
                                                                databaseRefer.child(uId).setValue(data);
                                                                
                                                                dialog.dismiss();
                                                                startActivity(
                                                                        new Intent(
                                                                                RegisterActivity
                                                                                        .this,
                                                                                EmailVerificationActivity
                                                                                        .class));
                                                                finish();
                                                            } else {
                                                                dialog.dismiss();
                                                                Toast.makeText(
                                                                                RegisterActivity
                                                                                        .this,
                                                                                t.getException()
                                                                                        .getMessage(),
                                                                                Toast.LENGTH_LONG)
                                                                        .show();
                                                            }
                                                        }
                                                    });
                                } else {
                                    if (mAuth.getCurrentUser() != null
                                            && !mAuth.getCurrentUser().isEmailVerified()) {
                                        mAuth.getCurrentUser()
                                                .sendEmailVerification()
                                                .addOnCompleteListener(
                                                        new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> t) {
                                                                if(t.isSuccessful()) {
                                                                    dialog.dismiss();
                                                                	startActivity(new Intent(RegisterActivity.this, EmailVerificationActivity.class));
                                                                    finish();
                                                                }else{
                                                                    Toast.makeText(RegisterActivity.this, t.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        });
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(
                                                        RegisterActivity.this,
                                                        task.getException().getMessage(),
                                                        Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            }
                        });
    }

    // going to previous activity
    public void goBack(View v) {
        onBackPressed();
    }

    // goto login screen
    public void goToLogin(View v) {
        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        // startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }
}
