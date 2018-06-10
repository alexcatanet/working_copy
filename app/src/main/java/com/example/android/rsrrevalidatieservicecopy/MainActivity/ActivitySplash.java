package com.example.android.rsrrevalidatieservicecopy.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.android.rsrrevalidatieservicecopy.R;

// Creating a nea class in order to provide the splash screen animation
public class ActivitySplash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        // Find the ImageView in the splash_activity.xml layout with the ID placeholder
        ImageView imageView = findViewById(R.id.splash_logo);
        ImageView imageView1 = findViewById(R.id.splash_logo_rsr);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.my_transition);
        imageView.startAnimation(animation);
        imageView1.startAnimation(animation);

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
