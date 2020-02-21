package com.example.nearest_dept;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;


public class SplashScreen extends AppCompatActivity {

    private static int SCREEN_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }catch(Exception ae){
            Toast.makeText(this, "Somethig went wrong please try again", Toast.LENGTH_SHORT).show();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(SplashScreen.this,LoginActivity.class);
                startActivity(intent);

                finish();
            }
        },SCREEN_TIME_OUT);
//

    }



}

