package com.example.enf_cnam;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ExamenActivity extends AppCompatActivity {

    private LinearLayout itemExamen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examen);
        this.itemExamen = (LinearLayout) findViewById(R.id.itemExamen);
        Thread exams = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                try {
                    JSONArray examens = listExamens(MainActivity.formation.getInt("ID_FORMATION"));
                    if (examens != null) {
                        for(int i = 0; i < examens.length(); i++) {
                            final LinearLayout unExamen = new LinearLayout(getApplicationContext());
                            unExamen.setOrientation(LinearLayout.VERTICAL);
                            JSONObject examen = examens.getJSONObject(i);
                            TextView dateAndUnite = new TextView(getApplicationContext());
                            // date au format français
                            SimpleDateFormat spf = new SimpleDateFormat("yyyy-mm-dd");
                            Date dateObject = spf.parse(examen.getString("DATE_EVENEMENT"));
                            spf = new SimpleDateFormat("dd/mm/yyyy");
                            String dateString = spf.format(dateObject);
                            dateAndUnite.setText(dateString + " - " + examen.getString("LIBELLE"));
                            // styles dateAndUnite
                            dateAndUnite.setPadding(40,15, 15, 5);
                            dateAndUnite.setTextColor(Color.BLACK);
                            dateAndUnite.setTypeface(Typeface.DEFAULT_BOLD);
                            TextView horairesAndSalle = new TextView(getApplicationContext());
                            horairesAndSalle.setText(examen.getString("HEURE_DEB") + " à " + examen.getString("HEURE_FIN") + " (" + examen.getString("LIB_SALLE") + ")");
                            // style horairesAndSalle
                            horairesAndSalle.setPadding(40,0,15,25);
                            unExamen.addView(dateAndUnite);
                            unExamen.addView(horairesAndSalle);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    itemExamen.addView(unExamen);
                                }
                            });
                        }
                    }
                } catch (JSONException | ParseException e) {
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

    public void viewUserInfo(View v) throws JSONException {
        Intent userActivity = new Intent(ExamenActivity.this, UserActivity.class);
        startActivity(userActivity);
    }
}