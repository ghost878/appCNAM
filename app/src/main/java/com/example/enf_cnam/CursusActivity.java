package com.example.enf_cnam;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

public class CursusActivity extends AppCompatActivity {

    public static int year = Calendar.getInstance().get(Calendar.YEAR)-1;
    private LinearLayout cursusLayout;
    private LinearLayout contenuLayout;
    private LinearLayout titleLayout;
    private Spinner selectYear;

    /**
     * Fonction d'appel de l'activité cursus
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursus);

        cursusLayout = (LinearLayout) findViewById(R.id.cursusLayout);
        contenuLayout = (LinearLayout) findViewById(R.id.contenuLayout);
        titleLayout = (LinearLayout) findViewById(R.id.titleLayout);
        selectYear = (Spinner) findViewById(R.id.selectYear);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Thread cursus = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void run() {
                try {
                    listCursus(year);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        cursus.start();

    }

    /** Méthode de la requête HTTP qui récupère les notes d'un auditeur pour une année donnée
     * @param year
     * @return
     */
    public JSONArray getCursus(int year) {
        JSONArray results = null;
        try {
            JSONObject jsonResponse = HttpRequest.requestGet(
                    MainActivity.token,
                    "http://apicnam.000webhostapp.com/API/Controllers/NoteController.php?view=getByYearAndAuditeur&year="+ year +"&id=" + MainActivity.auditeurInfo.getString("ID_AUDITEUR")
            );
            if(jsonResponse.has("notes")) {
                results = jsonResponse.getJSONArray("notes");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Méthode remplissant le contenuLayout des informations récupéré getCursus()
     * @param year
     * @throws JSONException
     */
    public void listCursus(int year) throws JSONException {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                contenuLayout.removeAllViews();
            }
        });

        String yearScol = year + "-" + (year+1) ;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.years, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectYear.setAdapter(adapter);
        selectYear.setSelection(adapter.getPosition(yearScol));


        JSONArray cursus = getCursus(year);
        if(cursus != null) {
            for(int i=0; i<cursus.length(); i++) {
                JSONObject detailCursus = cursus.getJSONObject(i);
                final LinearLayout unCursus = new LinearLayout(getApplicationContext());
                unCursus.setOrientation(LinearLayout.HORIZONTAL);
                TextView ue = new TextView(getApplicationContext());
                ue.setText(detailCursus.getString("CODE") + " - " + detailCursus.getString("LIBELLE") + " (" + detailCursus.getString("ECTS") + ")" + "\n" + detailCursus.getString("ANNEE_DEBUT") + " / " + detailCursus.getString("ANNEE_FIN"));
                TextView note = new TextView(getApplicationContext());
                note.setText(detailCursus.getString("NOTE") + " / 20");
                ue.setWidth(800);
                ue.setHeight(200);
                note.setGravity(Gravity.CENTER);
                ue.setTextColor(Color.BLACK);
                note.setTextColor(Color.BLACK);
                note.setTypeface(Typeface.DEFAULT_BOLD);
                note.setBackgroundResource(R.drawable.cadre_note);

                unCursus.addView(ue);
                unCursus.addView(note);
                unCursus.setPadding(40,20,40,30);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contenuLayout.addView(unCursus);
                    }
                });
            }
        }
    }

    public void changeYear(View v) {
        int year;
        switch (selectYear.getSelectedItem().toString()) {
            case "2020-2021" :
                year = 2020;
                break;
            case "2019-2020" :
                year = 2019;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + selectYear.getSelectedItem().toString());
        }
        try {
            listCursus(year);
        } catch (JSONException e) {
            e.printStackTrace();
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