package com.example.enf_cnam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ExamenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examen);
        Thread exams = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray examens = listExamens(MainActivity.formation.getInt("ID_FORMATION"));
                    LinearLayout unExamen = new LinearLayout(getApplicationContext());
                    unExamen.setOrientation(LinearLayout.VERTICAL);
                    if (examens != null) {
                        for(int i = 0; i < examens.length(); i++) {
                            JSONObject examen = examens.getJSONObject(i);
                            TextView horaires = new TextView(getApplicationContext());
                            horaires.setText(examen.getString("HEURE_DEB") + " à " + examen.getString("HEURE_FIN"));
                            unExamen.addView(horaires);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        exams.start();
    }

    /**
     * Fonction: listExamens
     * Description: Liste les examens retournés depuis l'API
     * @param idFormation Identifiant de la formation
     * @return JSONArray
     */
    public JSONArray listExamens(int idFormation) {
        JSONArray results = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://apicnam.000webhostapp.com/API/Controllers/PlanningController.php?view=getExamens&id=" + idFormation)
                    .build();
            Response reponse = client.newCall(request).execute();
            String responseBody = reponse.body().string();
            JSONObject jsonReponse = new JSONObject(responseBody);
            if (jsonReponse.has("examens")) {
                results = jsonReponse.getJSONArray("examens");
            }
        } catch(IOException | JSONException e) {
            e.printStackTrace();
        }
        return results;
    }

    public void viewHome(View v) {
        Intent homeActivity = new Intent(ExamenActivity.this, HomeActivity.class);
        startActivity(homeActivity);
    }

    public void logout(View v) {
        Intent mainActivity = new Intent(ExamenActivity.this, MainActivity.class);
        startActivity(mainActivity);
    }

    public void mail(View v) {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://login.live.com/login.srf?wa=wsignin1.0&rpsnv=13&ct=1610371321&rver=7.0.6737.0&wp=MBI_SSL&wreply=https%3a%2f%2foutlook.live.com%2fowa%2f%3fnlp%3d1%26RpsCsrfState%3db3d1dea9-4053-5262-434d-0b14a393acbf&id=292841&aadredir=1&CBCXT=out&lw=1&fl=dob%2cflname%2cwld&cobrandid=90015"));
        viewIntent.setPackage("com.android.chrome");
        startActivity(viewIntent);
    }
}