package com.example.android.rsrrevalidatieservicecopy.MainActivity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.rsrrevalidatieservicecopy.R;

// Creating a nea class in order to provide the splash screen animation
public class ActivitySplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        final Intent intent = new Intent(this, MainActivity.class);

        // Creating the Thread object to prioritize the execution of specific action
        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(1000); // Temporarily cease execution
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(intent);
                    finish();
                }
            }
        };
        // lead this thread to begin execution
        timer.start();
    }
}
