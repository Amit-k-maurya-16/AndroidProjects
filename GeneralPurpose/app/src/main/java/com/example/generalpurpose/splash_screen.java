package com.example.generalpurpose;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class splash_screen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(saveInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(splash_screen.this, MainActivity.class));
            }
        }, 2000);

        splashScreen.setKeepOnScreenCondition(() -> true );
    }
}

