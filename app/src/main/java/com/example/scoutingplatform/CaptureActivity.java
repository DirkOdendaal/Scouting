package com.example.scoutingplatform;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CaptureActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    DatabaseHelper mDatabaseHelper;
    private boolean askfgender;
    private boolean askfquantity;
    private boolean askfTrap;
    private static final String BROADCAST_ACTION = "BROADCAST_ACTION";
    ImageButton btnCamera;
    ImageView imageView6;
    String dir;
    String guid;
    Uri outputFileUri;
    File newFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            mDatabaseHelper = new DatabaseHelper(this);
            final Context mycontext = this;
            setContentView(R.layout.test1);
            encodedString = "";
            UUID idd = UUID.randomUUID();
            guid = idd.toString().replace("-", "");
            final Spinner spPest = findViewById(R.id.spPest);
            final Spinner spPhases = findViewById(R.id.spPhases);
            final Spinner spPosition = findViewById(R.id.spPosition);
            final RadioGroup rgGender = findViewById(R.id.rgGender);
            final RadioButton rbMale = findViewById(R.id.rbMale);
            final RadioButton rbFemale = findViewById(R.id.rbFemale);
            final RadioButton rbSterile = findViewById(R.id.rbSterile);
            final TextView txtGender = findViewById(R.id.txtGender);
            final TextView txtPhase = findViewById(R.id.txtPhase);
            final TextView txtPos = findViewById(R.id.txtPos);
            final TextView txtSeverity = findViewById(R.id.txtSeverity);
            final Spinner spSev = findViewById(R.id.spSeverity);
            final EditText edtQuantity = findViewById(R.id.edtQuantity);
            final EditText edtTrap = findViewById(R.id.edtTrap);
            SharedPreferences settings = getSharedPreferences("Scouting", 0);
            final List<String> PestDescriptions = mDatabaseHelper.getPDDDDescriptions(settings.getString("ScoutingMethod", ""));
            final Button btnConfirm = findViewById(R.id.btnConfirm);
            final Button btnCancel = findViewById(R.id.btnCancel);
            btnCamera = findViewById(R.id.btnCamera);
            imageView6 = findViewById(R.id.imageView6);
            final ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, PestDescriptions);

            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spPest.setAdapter(dataAdapter);


            dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Scouting/";
            File newdir = new File(dir);
            newdir.mkdirs();

            spPest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        if (!spPest.getSelectedItem().toString().equals("None")) {

                            spPhases.setVisibility(View.VISIBLE);
                            txtPhase.setVisibility(View.VISIBLE);
                            spPosition.setVisibility(View.VISIBLE);
                            txtPos.setVisibility(View.VISIBLE);

                            final List<String> Phases = mDatabaseHelper.getPDDDPhases(spPest.getSelectedItem().toString());
                            ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<>(mycontext, android.R.layout.simple_spinner_item, Phases);
                            dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPhases.setAdapter(dataAdapter2);

                            final List<String> Pos = mDatabaseHelper.getPDDDPos(spPest.getSelectedItem().toString());
                            ArrayAdapter<String> dataAdapter3 = new ArrayAdapter<>(mycontext, android.R.layout.simple_spinner_item, Pos);
                            dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPosition.setAdapter(dataAdapter3);

                            if (mDatabaseHelper.getPDDDAskGender(spPest.getSelectedItem().toString())) {
                                rgGender.setVisibility(View.VISIBLE);
                                txtGender.setVisibility(View.VISIBLE);
                                askfgender = true;
                            } else {
                                rgGender.setVisibility(View.GONE);
                                txtGender.setVisibility(View.GONE);
                                askfgender = false;
                            }

                            if (mDatabaseHelper.getPDDDAskTrap(spPest.getSelectedItem().toString())) {
                                edtTrap.setVisibility(View.VISIBLE);
                                askfTrap = true;
                            } else {
                                edtTrap.setVisibility(View.VISIBLE);
                                askfTrap = false;
                            }

                            if (mDatabaseHelper.getPDDDMesurementType(spPest.getSelectedItem().toString())) {
                                edtQuantity.setVisibility(View.VISIBLE);
                                askfquantity = true;
                                txtSeverity.setVisibility(View.GONE);
                                spSev.setVisibility(View.GONE);

                            } else {
                                String[] qwe = new String[]{"High", "Medium", "Low"};
                                ArrayAdapter<String> dataAdapterSev = new ArrayAdapter<>(mycontext, android.R.layout.simple_spinner_item, qwe);
                                dataAdapterSev.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spSev.setAdapter(dataAdapterSev);

                                edtQuantity.setVisibility(View.GONE);
                                askfquantity = false;
                                txtSeverity.setVisibility(View.VISIBLE);
                                spSev.setVisibility(View.VISIBLE);
                            }
                        } else {
                            spPhases.setVisibility(View.INVISIBLE);
                            spPosition.setVisibility(View.INVISIBLE);
                            rgGender.setVisibility(View.INVISIBLE);
                            txtGender.setVisibility(View.INVISIBLE);
                            txtSeverity.setVisibility(View.INVISIBLE);
                            spSev.setVisibility(View.INVISIBLE);
                            edtQuantity.setVisibility(View.INVISIBLE);
                            edtTrap.setVisibility(View.INVISIBLE);
                            txtPhase.setVisibility(View.INVISIBLE);
                            txtPos.setVisibility(View.INVISIBLE);
                        }

                        btnConfirm.setOnClickListener(v -> {
                            if (askfgender && rgGender.getCheckedRadioButtonId() == -1) {
                                rbFemale.setError("Please specify a gender.");
                                rbMale.setError("Please specify a gender.");
                                rbSterile.setError("Please specify a gender.");
                                return;
                            }
                            if (TextUtils.isEmpty(edtQuantity.getText()) && askfquantity) {
                                edtQuantity.setError("Please enter a quantity.");
                                return;
                            }

                            String gender = "";
                            if (rbFemale.isChecked()) {
                                gender = "Female";
                            } else if (rbMale.isChecked()) {
                                gender = "Male";
                            } else if (rbSterile.isChecked()){
                                gender = "Sterile";
                            }

                            String Phase = "";
                            String Pos = "";
                            float Quan = 0f;
                            String Sev = "";
                            String Description = "";
                            String ImagePath = "";
                            String trap = "";


                            if (!spPest.getSelectedItem().toString().equals("None")) {

                                if ((spPhases.getSelectedItemId() != -1 || !TextUtils.isEmpty(spPhases.getSelectedItem().toString()) && spPhases.getSelectedItem() != null)) {
                                    Phase = spPhases.getSelectedItem().toString();
                                }
                                if ((spPosition.getSelectedItemId() != -1 || !TextUtils.isEmpty(spPosition.getSelectedItem().toString()) && spPosition.getSelectedItem() != null)) {
                                    Pos = spPosition.getSelectedItem().toString();
                                }
                                if (!TextUtils.isEmpty(edtQuantity.getText().toString())) {
                                    Quan = Float.parseFloat(edtQuantity.getText().toString());
                                }
                                if (!TextUtils.isEmpty(edtTrap.getText().toString())) {
                                    trap = edtTrap.getText().toString();
                                }
                                if ((spSev.getSelectedItemId() != -1 || !TextUtils.isEmpty(spSev.getSelectedItem().toString())) && spSev.getSelectedItem() != null) {
                                    Sev = spSev.getSelectedItem().toString();
                                }
                                if ((spPest.getSelectedItemId() != -1 || !TextUtils.isEmpty(spPest.getSelectedItem().toString()) && spPest.getSelectedItem() != null)) {
                                    Description = spPest.getSelectedItem().toString();
                                }
                            } else {
                                Description = "None";
                            }
                            if (outputFileUri != null) {
                                ImagePath = outputFileUri.getPath();
                            }

                            SharedPreferences settings1 = getSharedPreferences("Scouting", 0);
                            String Block = settings1.getString("BlockName", "");
                            Cursor data = null;
                            try {
                                data = mDatabaseHelper.getBlockID(Block);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            data.moveToFirst();
                            String Blockid = data.getString(0);
                            data.close();
                            String ProductionUnit = null;
                            try {
                                ProductionUnit = mDatabaseHelper.getProductionUnit(Block);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String Subblock = mDatabaseHelper.getsubblock(Block);
                            String ScoutingMethod = settings1.getString("ScoutingMethod", "");
                            String Location = settings1.getString("location", "");
                            String CapturePoint = getIntent().getStringExtra("CapturePoint");
                            String Datapoint = getIntent().getStringExtra("DataPoint");
                            String barcode = getIntent().getStringExtra("Barcode");

                            boolean saved = mDatabaseHelper.addCapData(
                                    CapturePoint,
                                    gender,
                                    ScoutingMethod,
                                    Phase,
                                    Pos,
                                    Location,
                                    getCurrentTimeStamp(),
                                    ProductionUnit,
                                    Block,
                                    Subblock,
                                    Quan,
                                    Sev,
                                    Datapoint,
                                    Description,
                                    guid,
                                    ImagePath,
                                    Blockid,
                                    barcode,
                                    trap
                            );
                            if (saved) {
                                Intent intent = new Intent(BROADCAST_ACTION);
                                sendBroadcast(intent);
                                onBackPressed();
                                Log.d("cap", "onClick: " +
                                        CapturePoint +
                                        gender +
                                        ScoutingMethod +
                                        Phase +
                                        Pos +
                                        Location +
                                        getCurrentTimeStamp() +
                                        ProductionUnit +
                                        Block +
                                        Subblock +
                                        Quan +
                                        Sev +
                                        Datapoint +
                                        Description +
                                        guid +
                                        ImagePath +
                                        trap
                                );
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                            }
                        });

                        btnCancel.setOnClickListener(v -> onBackPressed());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            btnCamera.setOnClickListener(v -> {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());

                    String file = dir + guid + ".jpg";
                    newFile = new File(file);
                    try {
                        newFile.createNewFile();
                    } catch (IOException e) {
                        Log.d("PHOTO", "Could not create file");
                    }

                    outputFileUri = Uri.fromFile(newFile);
                    Log.d("PHOTO", "onClick: outp" + outputFileUri);
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                    startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentTimeStamp() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.format(new Date());
        } catch (Exception ep) {
            Log.d("exeptions", ep.toString());
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            Log.d("loadPic", "onActivityResult: ");
            imageView6.setImageURI(outputFileUri);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String encodedString;
}
