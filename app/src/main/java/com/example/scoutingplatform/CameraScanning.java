package com.example.scoutingplatform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class CameraScanning extends AppCompatActivity {

    EditText edt;
    Button btnNext, btnBackScan;
    ImageButton imageButtonCam;
    SurfaceView surfaceView;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    boolean created = false;
    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_layout);
        edt = findViewById(R.id.editTextScan);
        btnNext = findViewById(R.id.btnNextScan);
        btnBackScan = findViewById(R.id.btnBackScan);
        surfaceView = findViewById(R.id.surfaceView);
        imageButtonCam = findViewById(R.id.imageButtonCam);
        edt.requestFocus();

        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edt.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnNext.setOnClickListener(v -> {
            try {
                if (!edt.getText().toString().isEmpty()) {
                    SharedPreferences settings2 = getSharedPreferences("Scouting", 0);
                    SharedPreferences.Editor editor2 = settings2.edit();
                    editor2.putString("ScannedText", String.valueOf(edt.getText()));
                    editor2.apply();

                    Intent intentls = new Intent(getApplicationContext(), DataPointContainerActivity.class);
                    intentls.putExtra("CapturePoint", getIntent().getStringExtra("CapturePoint"));
                    intentls.putExtra("Barcode", edt.getText().toString());
                    startActivity(intentls);
                } else {
                    edt.setError("Please Scan a barcode.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        imageButtonCam.setOnClickListener(v -> {
            try {
                switch (surfaceView.getVisibility()) {
                    case View.INVISIBLE:
                        surfaceView.setVisibility(View.VISIBLE);
                        onVisible(created);
                        try {
                            if (ActivityCompat.checkSelfPermission(imageButtonCam.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                cameraSource.start(surfaceView.getHolder());
                                return;
                            }
                        } catch (Exception e) {
                            Log.d("cameraSource", e.toString());
                        }
                        break;
                    case View.VISIBLE:
                        cameraSource.stop();
                        surfaceView.setVisibility(View.INVISIBLE);
                        break;
                    case View.GONE:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        btnBackScan.setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();

            barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build();
            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {
                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    final SparseArray<Barcode> qrCodes = detections.getDetectedItems();
                    if (qrCodes.size() > 0) {
                        runOnUiThread(() -> {
                            Log.d("runonui", "run: ");
                            edt.setText(qrCodes.valueAt(0).displayValue);

                        });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onVisible(boolean create) {
        try {
            if (!create) {
                int orientation = this.getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    cameraSource = new CameraSource.Builder(this, barcodeDetector)
                            .setFacing(CameraSource.CAMERA_FACING_BACK)
                            .setRequestedPreviewSize(surfaceView.getHeight(),
                                    surfaceView.getWidth())
                            .setAutoFocusEnabled(true)
                            .build();
                } else {
                    cameraSource = new CameraSource.Builder(this, barcodeDetector)
                            .setFacing(CameraSource.CAMERA_FACING_BACK)
                            .setRequestedPreviewSize(surfaceView.getWidth(),
                                    surfaceView.getHeight())
                            .setAutoFocusEnabled(true)
                            .build();
                }

                surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                    @Override
                    public void surfaceCreated(SurfaceHolder holder) {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            Log.d("camdebug", "surfaceCreated: not allowed");
                            ActivityCompat.requestPermissions(CameraScanning.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                onBackPressed();
                            }
                        }
                    }

                    @Override
                    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                        Log.d("surf111", "surfaceChanged: ");
                    }

                    @Override
                    public void surfaceDestroyed(SurfaceHolder holder) {
                        cameraSource.stop();
                    }
                });
                created = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}