package com.pawan.aadhaarcard;

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

    private TextView mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_result);

        mobileNumber = findViewById(R.id.mobileNumber);

        String apiKey = getIntent().getStringExtra("api_key");
        String accountId = getIntent().getStringExtra("account_id");

        String taskId = getIntent().getStringExtra("task_id");
        String groupId = getIntent().getStringExtra("group_id");
        String aadhaarNumber = getIntent().getStringExtra("aadhaar_number");

        apiService =  RetrofitClient.getClient(apiKey, accountId).create(AadhaarVerificationApiService.class);

        sendAadhaarNum(taskId, groupId, aadhaarNumber);
    }

    private void sendAadhaarNum(String taskId, String groupId, String aadhaarNumber) {
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
                    requestMobileNumber(requestId);
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

    private void requestMobileNumber(String requestId) {
        apiService.requestMob(requestId).enqueue(new Callback<List<MobileNumRes>>() {
            @Override
            public void onResponse(Call<List<MobileNumRes>> call, Response<List<MobileNumRes>> response) {
//                Log.d("API Call", apiService.requestMob(requestId).request().url().toString());
                if (response.isSuccessful() && response.body() != null) {
                    MobileNumRes numResponse = response.body().get(0);
                    String responseMobile = numResponse.getMobileNumber();
                    if (responseMobile != null) {
//                        Log.d("mobile_number", responseMobile);
                        mobileNumber.setText(responseMobile);
                    } else {
                        Log.e("mobile_number", "Mobile number is null", new Throwable());
                    }
                }
                else {
                    Log.e("VerificationResult", "Failed the check: ", new Throwable());
                    Toast.makeText(VerificationResultActivity.this, "Failed to load tis item", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MobileNumRes>> call, Throwable t) {
                Log.e("VerificationResult", "Error: " + t.getMessage());
                Toast.makeText(VerificationResultActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
