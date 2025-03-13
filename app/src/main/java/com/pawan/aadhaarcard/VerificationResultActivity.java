package com.pawan.aadhaarcard;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.pawan.aadhaarcard.api_service.AadhaarVerificationApiService;
import com.pawan.aadhaarcard.network.RetrofitClient;
import com.pawan.aadhaarcard.request.AadhaarNumReq;
import com.pawan.aadhaarcard.response.AadhaarNumRes;
import com.pawan.aadhaarcard.response.MobileNumRes;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationResultActivity extends AppCompatActivity {
    AadhaarVerificationApiService apiService;
    private TextView verificationResult;
    private RelativeLayout contentScreen;
    private LinearLayout loadingScreen;
    private ImageView resultIcon;
    private Button btnViewReport;
    private String responseMobile = "";
    private String responseState = "";
    private String responseGender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_result);

        contentScreen = findViewById(R.id.contentScreen);
        loadingScreen = findViewById(R.id.loadingScreen);

        resultIcon = findViewById(R.id.resultIcon);
        verificationResult = findViewById(R.id.verificationResult);
        btnViewReport = findViewById(R.id.btnViewReport);

        String apiKey = getIntent().getStringExtra("api_key");
        String accountId = getIntent().getStringExtra("account_id");

        String taskId = getIntent().getStringExtra("task_id");
        String groupId = getIntent().getStringExtra("group_id");
        String aadhaarNumber = getIntent().getStringExtra("aadhaar_number");
        String mobileNumber = getIntent().getStringExtra("mobile_number");
        String name = getIntent().getStringExtra("name");
        String dob = getIntent().getStringExtra("dob");
        String gender = getIntent().getStringExtra("gender");

        apiService =  RetrofitClient.getClient(apiKey, accountId).create(AadhaarVerificationApiService.class);

        sendAadhaarNum(taskId, groupId, aadhaarNumber, mobileNumber, gender);

        btnViewReport.setOnClickListener(view -> {
            Intent intent = new Intent(VerificationResultActivity.this, ViewReport.class);
            intent.putExtra("responseMobile", responseMobile);
            intent.putExtra("responseState", responseState);
            intent.putExtra("responseGender", responseGender);
            intent.putExtra("name", name);
            intent.putExtra("dob", dob);
            startActivity(intent);
        });
    }

    private void sendAadhaarNum(String taskId, String groupId, String aadhaarNumber, String mobileNumber, String gender) {
        AadhaarNumReq aadhaarNumReq = new AadhaarNumReq(taskId, groupId, aadhaarNumber);
        apiService.aadhaar_lite(aadhaarNumReq).enqueue(new Callback<AadhaarNumRes>() {
            @Override
            public void onResponse(Call<AadhaarNumRes> call, Response<AadhaarNumRes> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AadhaarNumRes numResponse = response.body();
                    String requestId = numResponse.getRequestId();

                    // Hide Loading Screen & Show Main Content
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        requestMobileNumber(requestId, mobileNumber, gender);
                    }, 3000);
                }
                else {
                    Toast.makeText(VerificationResultActivity.this, "Failed to load tis item", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AadhaarNumRes> call, Throwable t) {
                Log.e("Network or API Error: ", "Sending Aadhaar Number API block error.", new Throwable());
                Intent intent = new Intent(VerificationResultActivity.this, ErrorActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void requestMobileNumber(String requestId, String userMobileNumber, String gender) {
        apiService.requestMob(requestId).enqueue(new Callback<List<MobileNumRes>>() {
            @Override
            public void onResponse(Call<List<MobileNumRes>> call, Response<List<MobileNumRes>> response) {
                Log.d("API Call", apiService.requestMob(requestId).request().url().toString());
                if (response.isSuccessful() && response.body() != null) {
                    MobileNumRes numResponse = response.body().get(0);
                    responseGender = numResponse.getGender();
                    responseMobile = numResponse.getMobileNumber();
                    responseState = numResponse.getState();
                    String responseStatus = numResponse.getStatus();
                    if (responseGender != null && responseMobile != null && responseState != null) {
                        boolean mobResult = checkMobileNumbers(userMobileNumber, responseMobile);
                        boolean genderResult = checkGenders(gender, responseGender.toLowerCase());
                        setResult(responseStatus, mobResult, genderResult);

                        loadingScreen.setVisibility(View.GONE);
                        contentScreen.setVisibility(View.VISIBLE);
                    }
                    else if (responseStatus.equals("failed")) {
                        String aadhaarNumber = getIntent().getStringExtra("aadhaar_number");
                        Intent intent = new Intent(VerificationResultActivity.this, InvalidAadhaarActivity.class);
                        intent.putExtra("aadhaar_number", aadhaarNumber);
                        startActivity(intent);
                        finish();
                    }
                }
                else {
                    Log.e("VerificationResult", "Response failed or Response body is empty: ", new Throwable());
                    Toast.makeText(VerificationResultActivity.this, "Response failed or Response body is empty.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MobileNumRes>> call, Throwable t) {
                Log.e("Network or API Error: ", "Request details API block error.", new Throwable());
                Intent intent = new Intent(VerificationResultActivity.this, ErrorActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setResult(String responseStatus, boolean mobResult, boolean genderResult) {
        if (!responseStatus.equals("failed") && mobResult && genderResult) {
            verificationResult.setText("Verification Successful.");
            verificationResult.setTextColor(ContextCompat.getColor(this, R.color.green));
            resultIcon.setImageResource(R.drawable.ic_success); // Green tick
            btnViewReport.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.green));
        }
        else {
            verificationResult.setText("Verification Failed.");
            verificationResult.setTextColor(ContextCompat.getColor(this, R.color.red));
            resultIcon.setImageResource(R.drawable.ic_failed); //Red cross
            btnViewReport.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red));
        }
    }

    private boolean checkMobileNumbers(String mobileNumber, String responseMobile) {
        for (int i=7; i<10; i++) {
            if (mobileNumber.charAt(i) != responseMobile.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkGenders(String gender, String responseGender) {
        if (gender.equals(responseGender)) {
            return true;
        }
        return false;
    }
}
