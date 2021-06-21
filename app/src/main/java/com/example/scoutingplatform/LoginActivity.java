package com.example.scoutingplatform;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class LoginActivity extends AppCompatActivity {

    EditText txtemail;
    EditText txtpassword;
    EditText txtdbid;
    Button btnLogin;
    ConnectivityManager cm;
    NetworkInfo activeNetwork;
    ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtemail = findViewById(R.id.username);
        txtpassword = findViewById(R.id.password);
        txtdbid = findViewById(R.id.edtDBID);
        btnLogin = findViewById(R.id.login);
        loading = findViewById(R.id.loading);
        btnLogin.setOnClickListener(v -> {
            if (CheckForConectivity()) {
                loading.setVisibility(View.VISIBLE);
                disablebutns();
                Authenticate(txtemail.getText().toString(), txtpassword.getText().toString(), txtdbid.getText().toString());
            } else {
                Toast.makeText(getApplicationContext(), "No network.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void disablebutns() {
        txtemail.setEnabled(false);
        txtpassword.setEnabled(false);
        txtdbid.setEnabled(false);
        btnLogin.setEnabled(false);
    }

    private void enablebutns() {
        txtemail.setEnabled(true);
        txtpassword.setEnabled(true);
        txtdbid.setEnabled(true);
        btnLogin.setEnabled(true);
        loading.setVisibility(View.GONE);
    }

    private void Authenticate(final String email, final String passw, final String dbid) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://appnostic.dbflex.net/secure/api/v2/" + dbid + "/Scouting%20PDDD%20setup/Default%20View/select.json")
                .header("Authorization", Credentials.basic(email, passw))
                .method("GET", null)
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.d("RESPONSES", "SyncData: FAILED");
            }

            @Override
            public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {
                if (response.isSuccessful() && (response.body() != null ? response.body().toString().length() : 0) > 2) {
                    String jsonOutput = response.body().string();
                    Type listType = new TypeToken<List<PDDDSetup>>() {
                    }.getType();
                    Gson gson = new Gson();
                    List<PDDDSetup> posts = gson.fromJson(jsonOutput, listType);

                    if (posts.size() > 0) {
                        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("email", email);
                        editor.putString("password", passw);
                        editor.putString("DBID", dbid);
                        editor.putBoolean("Authorized", true);
                        editor.commit();
                        finish();
                    } else {
                        LoginActivity.this.runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "No records found.", Toast.LENGTH_LONG).show();
                            enablebutns();
                        });
                    }
                } else {
                    new Thread() {
                        public void run() {
                            LoginActivity.this.runOnUiThread(() -> {
                                Toast.makeText(getApplicationContext(), "Code: " + response.code() + " Message: " + response.message(), Toast.LENGTH_LONG).show();
                                enablebutns();
                            });
                        }
                    }.start();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Please confirm");
        builder.setMessage("Are you want to exit the app?");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        builder.setNegativeButton("No", (dialogInterface, i) -> {
        });
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Boolean CheckForConectivity() {
        cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            return false;
        } else {
            return true;
        }
    }
}
