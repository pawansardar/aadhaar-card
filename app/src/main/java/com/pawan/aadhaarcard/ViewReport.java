package com.pawan.aadhaarcard;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewReport extends AppCompatActivity {
    private TextView mobileNumber, state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);

        String responseMobile = getIntent().getStringExtra("responseMobile");
        String responseState = getIntent().getStringExtra("responseState");

        mobileNumber = findViewById(R.id.mobileNumber);
        state = findViewById(R.id.state);

        mobileNumber.setText(responseMobile);
        state.setText(responseState);
    }
}
