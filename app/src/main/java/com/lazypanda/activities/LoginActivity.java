package com.lazypanda.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lazypanda.R;
import com.lazypanda.databinding.ActivityLoginBinding;
import java.util.HashMap;
import java.util.List;
import com.google.firebase.auth.SignInMethodQueryResult;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 143;
    private SharedPreferences sharedPreferences;
    private static final String LOCAL_CREDENTIALS = "localCredentials";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER_ME = "rememberMe";
    private CheckBox checkBox;
    GoogleSignInClient mGoogleSignInClient;
    Boolean passwordVisibile = false;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        mAuth = FirebaseAuth.getInstance();
        
        checkBox = findViewById(R.id.rememberMe);
        
        //sharedPreferences = getApplicationContext().getPreferences(LOCAL_CREDENTIALS, Context.MODE_PRIVATE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        
        boolean rememberMe = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);
        checkBox.setChecked(rememberMe);
        
        if(rememberMe) {
        	String savedEmail = sharedPreferences.getString(KEY_EMAIL, "");
            String savedPassword = sharedPreferences.getString(KEY_PASSWORD, "");
            binding.eInput.setText(savedEmail);
            binding.passInput.setText(savedPassword);
        }

        // dialog
        dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
        dialog.setCancelable(false);

        // Create a GoogleSignInClient
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // password toggle
        binding.passInput.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        final int Right = 2;
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            if (event.getRawX()
                                    >= binding.passInput.getRight()
                                            - binding.passInput
                                                    .getCompoundDrawables()[Right]
                                                    .getBounds()
                                                    .width()) {
                                int selection = binding.passInput.getSelectionEnd();
                                if (passwordVisibile.booleanValue()) {
                                    binding.passInput
                                            .setCompoundDrawablesRelativeWithIntrinsicBounds(
                                                    0, 0, R.drawable.eye_close, 0);
                                    binding.passInput.setTransformationMethod(
                                            PasswordTransformationMethod.getInstance());
                                    // passwordVisibile = false;
                                } else {
                                    binding.passInput
                                            .setCompoundDrawablesRelativeWithIntrinsicBounds(
                                                    0, 0, R.drawable.eye_open, 0);
                                    binding.passInput.setTransformationMethod(
                                            HideReturnsTransformationMethod.getInstance());
                                    // passwordVisibile = true;
                                }
                                passwordVisibile = !passwordVisibile;
                                binding.passInput.setSelection(selection);
                                return true;
                            }
                        }
                        return false;
                    }
                });

        // text design
        SpannableString loginTxt = new SpannableString("Login\nInto Your\nAccount.");
        SpannableString createAcTxt = new SpannableString("Dont Have Account ? Create Now.");

        Typeface poppinsBold = Typeface.createFromAsset(getAssets(), "poppins_bold.ttf");
        Typeface poppinsExtraB = Typeface.createFromAsset(getAssets(), "poppins_extrabold.ttf");
        TypefaceSpan customFontSpan = new TypefaceSpan(poppinsBold);
        TypefaceSpan poppinsExtraBS = new TypefaceSpan(poppinsExtraB);

        RelativeSizeSpan medium = new RelativeSizeSpan(1.5f);
        RelativeSizeSpan large = new RelativeSizeSpan(1.95f);

        ForegroundColorSpan redColor = new ForegroundColorSpan(Color.parseColor("#FF2C4B"));
        ForegroundColorSpan yellow = new ForegroundColorSpan(Color.parseColor("#FFC100"));

        loginTxt.setSpan(customFontSpan, 6, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginTxt.setSpan(medium, 6, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginTxt.setSpan(poppinsExtraBS, 16, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginTxt.setSpan(large, 16, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginTxt.setSpan(yellow, 16, 23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan click =
                new ClickableSpan() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                    }
                };

        createAcTxt.setSpan(new UnderlineSpan(), 20, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        createAcTxt.setSpan(customFontSpan, 20, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        createAcTxt.setSpan(redColor, 20, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // createAcTxt.setSpan(click, 20, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.LoginText.setText(loginTxt);
        binding.createAc.setText(createAcTxt);
        // binding.createAc.setMovementMethod(LinkMovementMethod.getInstance());

    }
    
    //login user
    public void loginUser(View v) {
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.eInput.getText().toString().trim()).matches()) {
            binding.eInput.setError("Invalid Email");
            binding.eInput.setFocusable(true);
        } else if (binding.eInput.getText().toString().trim().isEmpty()) {
            binding.eInput.setError("Cannot Be Empty");
            binding.eInput.setFocusable(true);
        } else if (binding.passInput.getText().toString().isEmpty()) {
            binding.passInput.setError("Cannot Be Empty");
            binding.passInput.setFocusable(true);
        }else if(binding.passInput.getText().toString().trim().length() < 6) {
        	binding.passInput.setError("Must Be >6");
            binding.passInput.setFocusable(true);
        } else {
            loginUserWithEmailPassword(
                    binding.eInput.getText().toString().trim(),
                    binding.passInput.getText().toString());
        }
    }

    // login user with email
    private void loginUserWithEmailPassword(String email, String password) {
        if(checkBox.isChecked()) {
        	SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_EMAIL, email);
            editor.putString(KEY_PASSWORD, password);
            editor.putBoolean(KEY_REMEMBER_ME, true);
            editor.apply();
        }else{
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(KEY_EMAIL);
            editor.remove(KEY_PASSWORD);
            editor.putBoolean(KEY_REMEMBER_ME, false);
            editor.apply();
        }
        dialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    if (mAuth.getCurrentUser().isEmailVerified()) {

                                        //FirebaseUser user = mAuth.getCurrentUser();
                                        
                                        dialog.dismiss();
                                        startActivity(
                                                new Intent(
                                                        LoginActivity.this,
                                                        DashboardActivity.class));
                                    } else {
                                        Toast.makeText(
                                                        LoginActivity.this,
                                                        "Please verify your email",
                                                        Toast.LENGTH_LONG)
                                                .show();
                                        dialog.dismiss();
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    dialog.dismiss();
                                    Toast.makeText(
                                                    LoginActivity.this,
                                                    "Authentication failed.",
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
                                                LoginActivity.this,
                                                "" + e.getMessage(),
                                                Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
    }

    // forget password
    public void forgetPassword(View v) {
        if (binding.eInput.getText().toString().trim().isEmpty()) {
            binding.eInput.setError("Please Enter Your Email To Recover Password ");
            binding.eInput.setFocusable(true);
        } else if (!Patterns.EMAIL_ADDRESS
                .matcher(binding.eInput.getText().toString().trim())
                .matches()) {
            binding.eInput.setError("Invalid Email");
            binding.eInput.setFocusable(true);
        } else {
            passwordRecover(binding.eInput.getText().toString().trim());
        }
    }

    // send forget password email
    private void passwordRecover(String email) {
        dialog.show();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Forget password email link sent Successfully.", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(
                                                    LoginActivity.this,
                                                    task.getException().getMessage(),
                                                    Toast.LENGTH_LONG)
                                            .show();
                                    dialog.dismiss();
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(
                                                LoginActivity.this,
                                                "" + e.getMessage(),
                                                Toast.LENGTH_LONG)
                                        .show();
                                dialog.dismiss();
                            }
                        });
    }

    // signin with google
    public void signInWithGoogleAc(View v) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign-In failed, handle the error
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        FirebaseAuth.getInstance()
                .signInWithCredential(credential)
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign-in successful, user is authenticated with Firebase
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                        String email = user.getEmail();
                                        String uId = user.getUid();
                                        String phoneNumber = user.getPhoneNumber();
                                        String name = user.getDisplayName();
                                        //String image = user.getPhotoUrl().toString();    
                                        HashMap<Object, String> data = new HashMap<>();

                                        data.put("email", email);
                                        data.put("uId", uId);
                                        data.put("name", name);
                                        data.put("image", "");
                                        data.put("phoneN", phoneNumber);
                                        data.put("onlineStatus", "online");
                                        data.put("typingTo", "noOne");

                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference databaseRefer =
                                                database.getReference("Users");
                                        databaseRefer.child(uId).setValue(data);
                                    }

                                    startActivity(
                                            new Intent(
                                                    LoginActivity.this, DashboardActivity.class));
                                } else {
                                    // Sign-in failed, handle the error
                                    Toast.makeText(
                                                    LoginActivity.this,
                                                    "Unable to Sign In",
                                                    Toast.LENGTH_LONG)
                                            .show();
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(
                                                LoginActivity.this,
                                                "" + e.getMessage(),
                                                Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
    }

    // go to previous activity
    public void goBack(View v) {
        onBackPressed();
    }

    // goto register
    public void goToRegister(View v) {
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        // startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
}
