package com.facturasff.sff;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;


public class SplashActivity extends MainActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new android.os.Handler().postDelayed(new Runnable() {
                    public void run() {
            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
            mainActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            //animationView.cancelAnimation();
            //startActivity(mainActivity);

            startActivityForResult(mainActivity, 0);
            overridePendingTransition(0,0);
        }
        },10);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
