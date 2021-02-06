package com.example.enf_cnam;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.json.JSONException;

public class CursusActivity extends AppCompatActivity {

    private LinearLayout cursusLayout;
    private LinearLayout titreLayout;
    private LinearLayout contenuLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursus);

        cursusLayout = (LinearLayout) findViewById(R.id.cursusLayout);
        titreLayout = (LinearLayout) findViewById(R.id.titreLayout);
        contenuLayout = (LinearLayout) findViewById(R.id.contenuLayout);


        Thread cursus = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void run() {
                try {
                    listCursus();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        cursus.start();


        TextView titre = new TextView(getApplicationContext());
        titre.setText("Relevé de notes");
        titreLayout.addView(titre);

    }


    public JSONArray getCursus() {
        JSONArray results = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://apicnam.000webhostapp.com/API/Controllers/NoteController.php?view=getByYearAndAuditeur&year=2020&id=" + MainActivity.auditeurInfo.getString("ID_AUDITEUR"))
                    .build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            //System.out.println("Response :  " + responseBody);
            JSONObject jsonResponse = new JSONObject(responseBody);
            if(jsonResponse.has("notes")) {
                results = jsonResponse.getJSONArray("notes");
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return results;
    }


    public void listCursus() throws JSONException {

        JSONArray cursus = getCursus();
        System.out.println(cursus);
        if(cursus != null) {
            for(int i=0; i<cursus.length(); i++) {
                System.out.println("for");
                JSONObject detailCursus = cursus.getJSONObject(i);
                final LinearLayout unCursus = new LinearLayout(getApplicationContext());
                unCursus.setOrientation(LinearLayout.VERTICAL);
                TextView ue = new TextView(getApplicationContext());
                ue.setText(detailCursus.getString("CODE") + " - " + detailCursus.getString("LIBELLE") + " (" + detailCursus.getString("ECTS") + ")");
                TextView annee = new TextView(getApplicationContext());
                annee.setText(detailCursus.getString("ANNEE_DEBUT") + " / " + detailCursus.getString("ANNEE_FIN"));
                TextView note = new TextView(getApplicationContext());
                note.setText(detailCursus.getString("NOTE"));

                unCursus.addView(ue);
                unCursus.addView(annee);
                unCursus.addView(note);
                unCursus.setPadding(10,20,10,20);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contenuLayout.addView(unCursus);
                    }
                });
            }



        }



    }














    public void viewHome(View v) {
        Intent homeActivity = new Intent(CursusActivity.this, HomeActivity.class);
        startActivity(homeActivity);
    }

    public void logout(View v) {
        Intent mainActivity = new Intent(CursusActivity.this, MainActivity.class);
        startActivity(mainActivity);
        MainActivity.token = "";
    }

    public void mail(View v) {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://login.live.com/login.srf?wa=wsignin1.0&rpsnv=13&ct=1610371321&rver=7.0.6737.0&wp=MBI_SSL&wreply=https%3a%2f%2foutlook.live.com%2fowa%2f%3fnlp%3d1%26RpsCsrfState%3db3d1dea9-4053-5262-434d-0b14a393acbf&id=292841&aadredir=1&CBCXT=out&lw=1&fl=dob%2cflname%2cwld&cobrandid=90015"));
        viewIntent.setPackage("com.android.chrome");
        startActivity(viewIntent);
    }

    public void viewUserInfo(View v) throws JSONException {
        Intent userActivity = new Intent(CursusActivity.this, UserActivity.class);
        startActivity(userActivity);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(MainActivity.token == "") {
            Intent mainActivity = new Intent(CursusActivity.this, MainActivity.class);
            startActivity(mainActivity);
        }
    }
}