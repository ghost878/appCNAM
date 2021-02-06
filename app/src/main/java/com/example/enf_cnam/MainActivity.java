package com.example.enf_cnam;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private Connection connection;
    private Button button;
    private EditText mailForm;
    private EditText passForm;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static JSONObject auditeurInfo;
    public static JSONArray enseignements;
    public static JSONObject formation;
    public static String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Cette directive enlève la barre de titre
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.requestWindowFeature(Window.)
// Cette directive permet d'enlever la barre de notifications pour afficher l'application en plein écran
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//On définit le contenu de la vue APRES les instructions précédentes pour éviter un crash
        //this.setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Token token = token.create();

        mailForm = (EditText)findViewById(R.id.editTextTextEmailAddress);
        passForm = (EditText)findViewById(R.id.editTextTextPassword2);
        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Coucou1");
                Thread login = new Thread(new Runnable() {
                    public void run() {
                        System.out.println("Coucou2");
                        OkHttpClient client = new OkHttpClient();

                        RequestBody formBody = new FormBody.Builder()
                                .add("login", mailForm.getText().toString())
                                .add("password", passForm.getText().toString())
                                .build();

                        Request request = new Request.Builder()
                                .url("https://apicnam.000webhostapp.com/API/Controllers/AuditeurController.php?view=doLogin")
                                //.headers("Content-Type", "application/json", "Accept-Language", "fr", "Authorization", )
                                .method("POST", formBody)
                                .build();
                        Response response = null;
                        try {
                            response = client.newCall(request).execute();
                            String responseBody = response.body().string();
                            System.out.println("Response :  " + responseBody);
                            JSONObject jsonResponse = new JSONObject(responseBody);
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


                     /*       JSONObject jsonResponse = new JSONObject(response.body().string());
                            System.out.println("Coucou");
                            System.out.println(jsonResponse);

                            JSONArray jsonArray = jsonResponse.getJSONArray("auditeurs");
                            for(int i =0; i < jsonArray.length(); i++) {
                                JSONObject contact = jsonArray.getJSONObject(i);
                                System.out.println(contact.getString("MEL_PRO"));
                                System.out.println(contact.getString("PASSWORD"));

                                if(contact.getString("MEL_PRO").equals(mailForm.getText().toString()) && contact.getString("PASSWORD").equals(passForm.getText().toString())) {
                                    System.out.println("YOUPI");
                                    Intent homeActivity = new Intent(MainActivity.this, HomeActivity.class);
                                    startActivity(homeActivity);
                                }
                            }*/
                        } catch (IOException | JSONException /*| JSONException*/ e) {
                            e.printStackTrace();
                        }




                    }
                });
                login.start();
            }
        });
    }
}