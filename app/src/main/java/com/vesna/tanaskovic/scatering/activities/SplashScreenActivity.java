package com.vesna.tanaskovic.scatering.activities;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;

import com.vesna.tanaskovic.scatering.R;


/**
 * Created by Tanaskovic on 6/17/2017.
 */

public class SplashScreenActivity extends Activity {


    private static final int SPLASH_TIME_OUT = 1000; // splash to be visibility in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        // Initialize in background thread
        new InitTask().execute();
    }

    private class InitTask extends AsyncTask<Void, Void, Void>
    {
        private long startTime;

        @Override
        protected void onPreExecute()
        {
            startTime = System.currentTimeMillis();
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            continueLogin();
            return null;
        }

        private void continueLogin()
        {
            // wait so the splash splash to be visible at least SPLASH_TIME_OUT milliseconds
            long timeLeft = SPLASH_TIME_OUT - (System.currentTimeMillis() - startTime);
            if(timeLeft < 0) timeLeft = 0;
            SystemClock.sleep(timeLeft);

            login();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.splash);
    }


    private void login()
    {
        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        finish(); // to disable going back  to  splash
    }

}



