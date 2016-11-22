package com.estsoft.mblockchain.kow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by joeylee on 2016-11-16.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        Intent i = null;

        switch (v.getId()) {

            case R.id.loginButton:

                i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);

                break;
        }
    }
}

