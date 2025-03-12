package com.pawan.aadhaarcard;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewReport extends AppCompatActivity {
    private TextView mobileNumber, state, userName, userDob, userGender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);

        String responseMobile = getIntent().getStringExtra("responseMobile");
        String responseState = getIntent().getStringExtra("responseState");
        String gender = getIntent().getStringExtra("responseGender");
        String name = getIntent().getStringExtra("name");
        String dob = getIntent().getStringExtra("dob");

        userName = findViewById(R.id.userName);
        mobileNumber = findViewById(R.id.mobileNumber);
        state = findViewById(R.id.state);
        userDob = findViewById(R.id.userDob);
        userGender = findViewById(R.id.userGender);

        userName.setText(name);
        mobileNumber.setText(responseMobile);
        state.setText(responseState);
        userDob.setText(dob);
        userGender.setText(gender);
    }
}
