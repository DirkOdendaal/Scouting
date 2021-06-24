package com.example.scoutingplatform;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class CameraScanning extends AppCompatActivity {

    EditText edt;
    Button btnNext, btnBackScan;
    ImageButton imageButtonCam;
    SurfaceView surfaceView;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    boolean created = false;
    CameraSource cameraSource;
    MediaPlayer accsound = new MediaPlayer();
    BarcodeDetector barcodeDetector;

    private void initViews() {
        edt = findViewById(R.id.editTextScan);
        btnNext = findViewById(R.id.btnNextScan);
        btnBackScan = findViewById(R.id.btnBackScan);
        surfaceView = findViewById(R.id.surfaceView);
        imageButtonCam = findViewById(R.id.imageButtonCam);
        edt.requestFocus();

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
                            if (ContextCompat.checkSelfPermission(imageButtonCam.getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                cameraSource.start(surfaceView.getHolder());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_layout);
        accsound = MediaPlayer.create(this, R.raw.accplayer2);

        initViews();
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
                            edt.setText(qrCodes.valueAt(0).displayValue);
                            accsound.start();
                            cameraSource.stop();
                            surfaceView.setVisibility(View.INVISIBLE);
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
                cameraSource = new CameraSource.Builder(this, barcodeDetector)
                        .setRequestedPreviewSize(1920, 1080)
                        .setAutoFocusEnabled(true)
                        .build();

                surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                    @Override
                    public void surfaceCreated(SurfaceHolder holder) {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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