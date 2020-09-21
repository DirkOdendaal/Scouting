package com.example.scoutingplatform;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

public class RecyclerAdaptercp extends RecyclerView.Adapter<Holder> {

    Context context;
    ArrayList<Model> models;
    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    boolean created = false;

    //    SurfaceView surfaceViewCam;
//
    public RecyclerAdaptercp(Context context, ArrayList<Model> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myrecycleritem, null);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {

        holder.txtCP.setText(models.get(position).getCPID());
        holder.txtDetails.setText(models.get(position).getDetails());
        holder.txtBlock.setText(models.get(position).getBlockName());
        if (models.get(position).getCount() >= models.get(position).getCountneeded()) {
            holder.CPrelativeLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.edgesgreen));
        } else if (models.get(position).getCount() < models.get(position).getCountneeded() && models.get(position).getCount() > 0) {
            holder.CPrelativeLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.edgesblue));
        }
        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (models.get(position).isForceScan() && models.get(position).getBarcode().isEmpty()) {
//                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    final AlertDialog.Builder alert = new AlertDialog.Builder(context);
//                    View dialogView = inflater.inflate(R.layout.scan_layout, null);
//                    final EditText editTextScan = (EditText) dialogView.findViewById(R.id.editTextScan);
//                    final ImageButton imageButtonCam = (ImageButton) dialogView.findViewById(R.id.imageButtonCam);
////                    final SurfaceView surfaceViewCam = (SurfaceView) dialogView.findViewById(R.id.surfaceViewCam);
////                    onVisible(created, surfaceViewCam);
////                    try {
////                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
////                            // TODO: Consider calling
////                            //    ActivityCompat#requestPermissions
////                            // here to request the missing permissions, and then overriding
////                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
////                            //                                          int[] grantResults)
////                            // to handle the case where the user grants the permission. See the documentation
////                            // for ActivityCompat#requestPermissions for more details.
////                            return;
////                        }
////                        Log.d("cameraSource", "onClick: " + surfaceViewCam.getHolder());
////
////                        cameraSource.start(surfaceViewCam.getHolder());
////
////
////                    } catch (Exception e) {
////                        Log.d("cameraSource", e.toString());
////                    }
//
//                    imageButtonCam.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
////                            switch (surfaceViewCam.getVisibility()) {
////                                case View.VISIBLE:
////                                    surfaceViewCam.setVisibility(View.GONE);
////                                    break;
////                                case View.GONE:
////                                    surfaceViewCam.setVisibility(View.VISIBLE);
////                                    break;
////                            }
//                        }
//                    });
//
//
//                    editTextScan.setShowSoftInputOnFocus(false);
//                    editTextScan.requestFocus();
//                    editTextScan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                        @Override
//                        public void onFocusChange(View v, boolean hasFocus) {
//                            if (!hasFocus) {
//                                editTextScan.requestFocus();
//                            }
//                        }
//                    });
//                    editTextScan.addTextChangedListener(new TextWatcher() {
//                        @Override
//                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                        }
//
//                        @Override
//                        public void onTextChanged(CharSequence s, int start, int before, int count) {
//                        }
//
//                        @Override
//                        public void afterTextChanged(Editable s) {
//                            if (!editTextScan.getText().toString().equals("")) {
//                                String doublescan = editTextScan.getText().toString();
//                                String half1 = doublescan.substring(0, doublescan.length() / 2);
//                                String half2 = doublescan.substring(doublescan.length() / 2);
//                                if (!half1.equals(half2)) {
//
//                                }
//                            }
//                        }
//                    });
//
//                    editTextScan.setOnKeyListener(new View.OnKeyListener() {
//                        @Override
//                        public boolean onKey(View v, int keyCode, KeyEvent event) {
//                            if (keyCode == 66) {
//                                return true;
//                            }
//                            return false;
//                        }
//                    });
//
//
//
//                    alert.setView(dialogView);
//                    alert.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(context, "keycode", Toast.LENGTH_SHORT).show();
//
//                        }
//                    });
//
//                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int whichButton) {
//                        }
//                    });
//
//                    alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                        @Override
//                        public void onDismiss(DialogInterface dialog) {
//                            if (cameraSource != null) {
//                                cameraSource.stop();
//                            }
//                        }
//                    });
//
//                    alert.create();
//                    alert.show();

                    Intent intents = new Intent(context, CameraScanning.class);
                    intents.putExtra("CapturePoint", models.get(position).getCP());
                    intents.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intents);
                } else {
                    Intent intentls = new Intent(context, DataPointContainerActivity.class);
                    intentls.putExtra("CapturePoint", models.get(position).getCP());
                    String barcode ="";
                    if(models.get(position).getBarcode() != null){
                        barcode = models.get(position).getBarcode();
                    }
                    intentls.putExtra("Barcode", barcode);
                    intentls.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intentls);
                }
            }
        });

    }

    protected void onVisible(boolean create, SurfaceView surf) {
        if (!create) {
            barcodeDetector = new BarcodeDetector.Builder(context).setBarcodeFormats(Barcode.ALL_FORMATS).build();
            barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {
                    SparseArray<Barcode> qrCodes = detections.getDetectedItems();
                    if (qrCodes.size() > 0) {
                        //editTextMasterScan.setText(qrCodes.valueAt(0).displayValue);
                    }


                }
            });
            int orientation = context.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                cameraSource = new CameraSource.Builder(context, barcodeDetector).setFacing(CameraSource.CAMERA_FACING_BACK).setRequestedPreviewSize(200, 200).setAutoFocusEnabled(true).build();//.setRequestedPreviewSize(640,480)
            } else {
                cameraSource = new CameraSource.Builder(context, barcodeDetector).setFacing(CameraSource.CAMERA_FACING_BACK).setRequestedPreviewSize(200, 200).setAutoFocusEnabled(true).build();//.setRequestedPreviewSize(640,480)
            }

            surf.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
//                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                        Log.d("camdebug", "surfaceCreated: not allowed");
//                        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
//                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                            onBackPressed();
//                        }
//
//                    }

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

    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}
