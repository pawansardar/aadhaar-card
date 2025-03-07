package com.pawan.aadhaarcard;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK_FRONT = 1;
    private static final int REQUEST_IMAGE_PICK_BACK = 2;
    private static final int REQUEST_IMAGE_CAPTURE_FRONT = 3;
    private static final int REQUEST_IMAGE_CAPTURE_BACK = 4;
    private Uri imageUri;

    private ImageView imageViewFront;
    private ImageView imageViewBack;
    private EditText editTextName, editTextDOB, editTextAddress, editTextAadhaarNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewFront = findViewById(R.id.imageViewFront);
        imageViewBack = findViewById(R.id.imageViewBack);
        editTextName = findViewById(R.id.editTextName);
        editTextDOB = findViewById(R.id.editTextDOB);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextAadhaarNumber = findViewById(R.id.editTextAadhaarNumber);
        Button btnSelectImageFront = findViewById(R.id.btnSelectImageFront);
        Button btnSelectImageBack = findViewById(R.id.btnSelectImageBack);
        Button btnCaptureImageFront = findViewById(R.id.btnCaptureImageFront);
        Button btnCaptureImageBack = findViewById(R.id.btnCaptureImageBack);

        btnSelectImageFront.setOnClickListener(view -> selectImage(REQUEST_IMAGE_PICK_FRONT));
        btnSelectImageBack.setOnClickListener(view -> selectImage(REQUEST_IMAGE_PICK_BACK));
        btnCaptureImageFront.setOnClickListener(view -> captureImage(REQUEST_IMAGE_CAPTURE_FRONT));
        btnCaptureImageBack.setOnClickListener(view -> captureImage(REQUEST_IMAGE_CAPTURE_BACK));
    }

    private void selectImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    private void captureImage(int requestCode) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, requestCode);
        } else {
            startCameraIntent(requestCode);
        }
    }

    private void startCameraIntent(int requestCode) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Captured Image");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Log.d("MainActivity -> startCameraIntent()", "Image URI: " + imageUri);

        if (imageUri != null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, requestCode);
        }
        else {
            Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start camera
                startCameraIntent(requestCode);
            }
            else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
        }
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK_FRONT || requestCode == REQUEST_IMAGE_PICK_BACK) {
                try {
                    if (data != null && data.getData() != null) {
                        Uri imageUri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        handleImageResult(requestCode, bitmap);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (requestCode == REQUEST_IMAGE_CAPTURE_FRONT || requestCode == REQUEST_IMAGE_CAPTURE_BACK) {
                try {
                    Log.d("MainActivity -> onActivityResult()", "Image URI: " + imageUri);
                    if (imageUri != null) {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        handleImageResult(requestCode, bitmap);
                    }
                    else {
                        Toast.makeText(this, "Captured image URI is null", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void handleImageResult(int requestCode, Bitmap bitmap) {
        if (requestCode == REQUEST_IMAGE_PICK_FRONT || requestCode == REQUEST_IMAGE_CAPTURE_FRONT) {
            imageViewFront.setImageBitmap(bitmap);
            processFrontImage(bitmap);
        }
        else if (requestCode == REQUEST_IMAGE_PICK_BACK || requestCode == REQUEST_IMAGE_CAPTURE_BACK) {
            imageViewBack.setImageBitmap(bitmap);
            processBackImage(bitmap);
        }

        // Delete the captured image if it was taken from the camera
        if (requestCode == REQUEST_IMAGE_CAPTURE_FRONT || requestCode == REQUEST_IMAGE_CAPTURE_BACK) {
            deleteCapturedImage();
        }
    }

    private void deleteCapturedImage() {
        if (imageUri != null) {
            try {
                getContentResolver().delete(imageUri, null, null);
                Log.d("MainActivity", "Captured image deleted: " + imageUri);
            } catch (Exception e) {
                Log.e("MainActivity", "Failed to delete captured image", e);
            }
        }
    }

    private void processFrontImage(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener(this::extractFrontInfo)
                .addOnFailureListener(e -> Log.e("MainActivity", "Text recognition failed", e));
    }

    private void processBackImage(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener(this::extractBackInfo)
                .addOnFailureListener(e -> Log.e("MainActivity", "Text recognition failed", e));
    }

    private void extractFrontInfo(Text text) {
        String resultText = text.getText();
        String name = extractName(resultText);
        String dob = extractDOB(resultText);
        String aadhaarNumber = extractAadhaarNumber(resultText, dob);
        String address = extractFrontAddress(resultText, name);

        editTextName.setText(name);
        editTextDOB.setText(dob);
        editTextAadhaarNumber.setText(aadhaarNumber);
        editTextAddress.setText(address);
    }

    private void extractBackInfo(Text text) {
        String resultText = text.getText();
        String address = extractBackAddress(resultText);

        editTextAddress.setText(address);
    }

    private String extractName(String text) {
        String[] lines = text.split("\n");

        for (int i = 0; i < lines.length; i++) {
            String currentLine = lines[i].trim(); // Trim whitespace

            // Check if the line contains a valid DOB or YOB using extractDOB() regex logic
            if (currentLine.matches(".*(DOB|Date of Birth|D.O.B)[\\s:\\-]*?\\d{2}[\\/\\-]\\d{2}[\\/\\-]\\d{4}.*") ||
                    currentLine.matches(".*(Year of Birth)[\\s:\\-]*?\\d{4}.*")) {

                // Search for the name in the previous lines
                for (int j = i - 1; j >= 0; j--) {
                    String potentialName = lines[j].trim();

                    // Ensure the extracted name is meaningful
                    if (!potentialName.isEmpty() && !potentialName.matches(".*\\d.*") &&
                            !potentialName.toLowerCase().contains("name") &&
                            !potentialName.toLowerCase().contains("government") &&
                            potentialName.length() > 2) { // Avoid very short names
                        return potentialName;
                    }
                }
            }
        }
        return "Name not found";
    }

    private String extractDOB(String text) {
        // First, check for full Date of Birth
        Pattern dobPattern = Pattern.compile("(DOB|Date of Birth|D.O.B)[\\s:\\-]*?(\\d{2}[\\/\\-]\\d{2}[\\/\\-]\\d{4})", Pattern.CASE_INSENSITIVE);
        Matcher dobMatcher = dobPattern.matcher(text);

        if (dobMatcher.find()) {
            return dobMatcher.group(2).trim(); // Extract full date
        }

        // If full DOB is not found, check for Year of Birth
        Pattern yobPattern = Pattern.compile("(Year of Birth)[\\s:\\-]*?(\\d{4})", Pattern.CASE_INSENSITIVE);
        Matcher yobMatcher = yobPattern.matcher(text);

        if (yobMatcher.find()) {
            return yobMatcher.group(2).trim(); // Extract only year
        }

        return "DOB not found";
    }

    private String extractAadhaarNumber(String text, String dob) {
        String[] lines = text.split("\n");
        StringBuilder aadhaarNumBuilder = new StringBuilder();
        boolean dobFound = false;
        for (String line : lines) {
            if (line.contains(dob)) {
                Log.d("extractAadhaarNumber", "DOB Found");
                dobFound = true;
                continue;
            }
            if (dobFound) {
                Pattern pattern = Pattern.compile("\\b\\d{4} \\d{4} \\d{4}\\b");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String extractedAadhaarNum = matcher.group().replace(" ", "");

                    if (extractedAadhaarNum.matches("\\d{12}")) {
                        aadhaarNumBuilder.append(extractedAadhaarNum);
                        break;
                    }
                }
            }
        }
        String aadhaarNumber = aadhaarNumBuilder.toString().trim();
        return aadhaarNumber.isEmpty() ? "Aadhaar Number not found, try again!" : aadhaarNumber;
    }

    private String extractFrontAddress(String text, String name) {
        String[] lines = text.split("\n");
        StringBuilder addressBuilder = new StringBuilder();
        boolean addressStarted = false;
        boolean nameFound = false;

        for (String line : lines) {
            if (line.startsWith("To")) {
                addressStarted = true;
                continue;
            }
            if (addressStarted && line.startsWith(name)) {
                nameFound = true;
                continue;
            }
            if (addressStarted && nameFound) {
                addressBuilder.append(line.trim()).append(" ");
                if (line.matches(".*\\d{6}.*")) {
                    break;
                }
            }
        }
        String address = addressBuilder.toString().trim();
        return address.isEmpty() ? "Address not found on the front side" : address;
    }

    private String extractBackAddress(String text) {
        String[] lines = text.split("\n");
        StringBuilder addressBuilder = new StringBuilder();
        boolean addressStarted = false;

        for (String line : lines) {
            if (line.startsWith("Address:")) {
                addressStarted = true;
            }
            if (addressStarted) {
                addressBuilder.append(line.trim()).append(" ");
                if (line.matches(".*\\d{6}.*")) {
                    break;
                }
            }
        }
        addressBuilder.delete(0, 9);
        String address = addressBuilder.toString().trim();
        return address.isEmpty() ? "Address not found" : address;
    }
}
