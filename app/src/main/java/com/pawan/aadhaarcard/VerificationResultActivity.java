package com.pawan.aadhaarcard;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
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

    private TextView mobileNumber, state, verificationResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_result);

        mobileNumber = findViewById(R.id.mobileNumber);
        state = findViewById(R.id.state);
        verificationResult = findViewById(R.id.verificationResult);

        String apiKey = getIntent().getStringExtra("api_key");
        String accountId = getIntent().getStringExtra("account_id");

        String taskId = getIntent().getStringExtra("task_id");
        String groupId = getIntent().getStringExtra("group_id");
        String aadhaarNumber = getIntent().getStringExtra("aadhaar_number");
        String userMobileNumber = getIntent().getStringExtra("mobile_number");

        apiService =  RetrofitClient.getClient(apiKey, accountId).create(AadhaarVerificationApiService.class);

        sendAadhaarNum(taskId, groupId, aadhaarNumber, userMobileNumber);
    }

    private void sendAadhaarNum(String taskId, String groupId, String aadhaarNumber, String userMobileNumber) {
        AadhaarNumReq aadhaarNumReq = new AadhaarNumReq(taskId, groupId, aadhaarNumber);
        apiService.aadhaar_lite(aadhaarNumReq).enqueue(new Callback<AadhaarNumRes>() {
            @Override
            public void onResponse(Call<AadhaarNumRes> call, Response<AadhaarNumRes> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AadhaarNumRes numResponse = response.body();
                    String requestId = numResponse.getRequestId();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    requestMobileNumber(requestId, userMobileNumber);
                }
                else {
                    Toast.makeText(VerificationResultActivity.this, "Failed to load tis item", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AadhaarNumRes> call, Throwable t) {
                Toast.makeText(VerificationResultActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestMobileNumber(String requestId, String userMobileNumber) {
        apiService.requestMob(requestId).enqueue(new Callback<List<MobileNumRes>>() {
            @Override
            public void onResponse(Call<List<MobileNumRes>> call, Response<List<MobileNumRes>> response) {
//                Log.d("API Call", apiService.requestMob(requestId).request().url().toString());
                if (response.isSuccessful() && response.body() != null) {
                    MobileNumRes numResponse = response.body().get(0);
                    String responseMobile = numResponse.getMobileNumber();
                    String responseState = numResponse.getState();
                    String responseStatus = numResponse.getStatus();
                    if (responseMobile != null) {
//                        Log.d("mobile_number", responseMobile);
                        mobileNumber.setText(responseMobile);
                    } else {
                        Log.e("mobile_number", "Mobile number is null", new Throwable());
                    }
                    if (responseState != null) {
//                        Log.d("state", responseStat);
                        state.setText(responseState);
                    } else {
                        Log.e("state", "State is null", new Throwable());
                    }

                    setResult(userMobileNumber, responseMobile, responseStatus);
                }
                else {
                    Log.e("VerificationResult", "Failed the check. Response failed or Response body is empty: ", new Throwable());
                    Toast.makeText(VerificationResultActivity.this, "Response failed or Response body is empty.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MobileNumRes>> call, Throwable t) {
                Log.e("VerificationResult", "Error: " + t.getMessage());
                Toast.makeText(VerificationResultActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setResult(String userMobileNumber, String  responseMobile, String responseStatus) {
        if (checkMobileNumbers(userMobileNumber, responseMobile) && responseStatus != "failed") {
            verificationResult.setText("Verification Successful.");
            verificationResult.setTextColor(Color.parseColor("#008000"));
        }
        else {
            verificationResult.setText("Verification Failed.");
            verificationResult.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    private boolean checkMobileNumbers(String userMobileNumber, String responseMobile) {
        for (int i=7; i<10; i++) {
            if (userMobileNumber.charAt(i) != responseMobile.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}
