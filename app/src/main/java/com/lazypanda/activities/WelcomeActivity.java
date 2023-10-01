package com.lazypanda.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TypefaceSpan;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;
import com.lazypanda.R;
import com.lazypanda.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends AppCompatActivity {

    private ActivityWelcomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        //text design 
        SpannableString Quote =
                new SpannableString(
                        "“Pandas are proof that you can be fat, lazy, and still be Loved by everyone.”");
        SpannableString subText = new SpannableString("Dont\nBe Lazy,\nIn Doing This.");

        Typeface poppinsBold = Typeface.createFromAsset(getAssets(), "poppins_bold.ttf");
        Typeface poppinsExtraB = Typeface.createFromAsset(getAssets(), "poppins_extrabold.ttf");
        TypefaceSpan customFontSpan = new TypefaceSpan(poppinsBold);
        TypefaceSpan poppinsExtraBS = new TypefaceSpan(poppinsExtraB);

        ForegroundColorSpan redColor = new ForegroundColorSpan(Color.parseColor("#FF2C4B"));
        ForegroundColorSpan yellow = new ForegroundColorSpan(Color.parseColor("#FFC100"));

        RelativeSizeSpan textSize = new RelativeSizeSpan(1.39f);
        RelativeSizeSpan medium = new RelativeSizeSpan(1.6f);
        RelativeSizeSpan large = new RelativeSizeSpan(1.8f);

        CharacterStyle customFontColor =
                new CharacterStyle() {
                    @Override
                    public void updateDrawState(TextPaint tp) {
                        tp.setTypeface(poppinsBold);
                        tp.setColor(Color.parseColor("#FFC100"));
                    }
                };

        Quote.setSpan(customFontColor, 1, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Quote.setSpan(customFontSpan, 58, 63, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Quote.setSpan(redColor, 58, 63, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Quote.setSpan(textSize, 1, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Quote.setSpan(textSize, 58, 63, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        subText.setSpan(medium, 5, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        subText.setSpan(large, 13, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        subText.setSpan(customFontSpan, 5, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        subText.setSpan(poppinsExtraBS, 13, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        subText.setSpan(redColor, 8, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        subText.setSpan(yellow, 22, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.lazypandaQuote.setText(Quote);
        binding.subText.setText(subText);
        
        Animation anim = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.fade);
        binding.topCircleView.startAnimation(anim);
    }
    
    //going to login screen
    public void goToLoginScreen(View v) {
        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
    }
    
    //going to register activity
    public void goToRegisterA(View v) {
        startActivity(new Intent(WelcomeActivity.this, RegisterActivity.class));
    }
}
