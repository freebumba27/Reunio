package com.altaoferta.reunio;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.altaoferta.utils.ReuseableClass;

public class LoginActivity extends AppCompatActivity {

    private static final int TIME_INTERVAL = 2000;
    EditText EditTextFirstName;
    EditText EditTextEmployeeId;
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditTextFirstName = (EditText) findViewById(R.id.EditTextFirstName);
        EditTextEmployeeId = (EditText) findViewById(R.id.EditTextEmployeeId);
    }

    public void login(View view) {
        if (EditTextFirstName.getText().toString().equalsIgnoreCase("") || EditTextEmployeeId.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, "All fields are mandatory !!", Toast.LENGTH_LONG).show();
        } else {
            if (ReuseableClass.getFromPreference("first_name", LoginActivity.this).equalsIgnoreCase(EditTextFirstName.getText().toString()) &&
                    ReuseableClass.getFromPreference("empid", LoginActivity.this).equalsIgnoreCase(EditTextEmployeeId.getText().toString())) {
                Intent i = new Intent(this, DashBoardActivity.class);
                finish();
                startActivity(i);
            } else {
                Toast.makeText(this, "Please check your credentials !!", Toast.LENGTH_LONG).show();
            }
        }


//        Intent i = new Intent(this, DashBoardActivity.class);
//        finish();
//        startActivity(i);
    }

    public void registering(View view) {
        if (ReuseableClass.getFromPreference("first_name", LoginActivity.this).length() == 0) {
            Intent i = new Intent(this, RegistrationActivity.class);
            finish();
            startActivity(i);
        } else {
            Toast.makeText(this, "For a single device single user allowed !!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getBaseContext(), R.string.double_back_pressed_error, Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }
}
