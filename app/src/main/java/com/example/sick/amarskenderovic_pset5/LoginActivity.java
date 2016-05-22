package com.example.sick.amarskenderovic_pset5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button bLogin = (Button) findViewById(R.id.continueButton);
        assert bLogin != null;

        // On click listener to go to the next activity
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent welcomeIntent = new Intent(LoginActivity.this, WeatherActivity.class);
                LoginActivity.this.startActivity(welcomeIntent);
            }
        });
    }
}