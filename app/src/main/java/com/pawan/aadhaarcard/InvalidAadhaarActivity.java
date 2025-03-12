package com.pawan.aadhaarcard;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InvalidAadhaarActivity extends AppCompatActivity {
    private TextView aadhaarNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invalid_aadhaar);
        String userAadhaarNumber = getIntent().getStringExtra("aadhaar_number");
        aadhaarNumber = findViewById(R.id.aadhaarNumber);
        aadhaarNumber.setText(userAadhaarNumber);
    }
}
