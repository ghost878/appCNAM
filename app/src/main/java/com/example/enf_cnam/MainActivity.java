package com.example.enf_cnam;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static JSONObject auditeurInfo;
    public static JSONArray enseignements;
    public static JSONObject formation;
    public static String token;
    public static String lang = "FR";
    private Connection connection;
    private Button button;
    private EditText mailForm;
    private EditText passForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mailForm = (EditText)findViewById(R.id.editTextTextEmailAddress);
        passForm = (EditText)findViewById(R.id.editTextTextPassword2);
        final Button button = findViewById(R.id.button);
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Coucou1");
                Thread login = new Thread(new Runnable() {
                    public void run() {
                        System.out.println("Coucou2");
                        RequestBody formBody = new FormBody.Builder()
                                .add("login", mailForm.getText().toString())
                                .add("password", passForm.getText().toString())
                                .build();
                        try {
                            JSONObject jsonResponse = HttpRequest.requestPost(
                                    "",
                                    formBody,
                                    "https://apicnam.000webhostapp.com/API/Controllers/AuditeurController.php?view=doLogin"
                            );
                            boolean exist = jsonResponse.getBoolean("exist");
                            if(exist == true) {
                                auditeurInfo = jsonResponse.getJSONObject("auditeur");
                                enseignements = jsonResponse.getJSONArray("enseignements");
                                formation = jsonResponse.getJSONObject("formation");
                                token = jsonResponse.getString("token");
                                Intent homeActivity = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(homeActivity);
                            }
                            else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Context context = getApplicationContext();
                                        CharSequence errorMessage = "Woops - Identifiant ou mot de passe invalide";
                                        int duration = Toast.LENGTH_SHORT;
                                        Toast toast = Toast.makeText(context,errorMessage,duration);
                                        toast.show();
                                    }
                                });
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
                login.start();
            }
        });
    }
}

