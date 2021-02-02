package com.example.enf_cnam;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@RequiresApi(api = Build.VERSION_CODES.O)
public class PlanningActivity extends AppCompatActivity {
    private LinearLayout planingLayout;
    private LinearLayout planningJour;
    private LinearLayout planningSemaine;
    private LinearLayout cadreCours;
    private Button changeViewPlanning;
    private TextView libelleDay;
    public static boolean semaineActive = false;
    public static LocalDate date = LocalDate.now();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning);

        planningJour = (LinearLayout) findViewById(R.id.planningJour);
        planningSemaine = (LinearLayout) findViewById(R.id.planningSemaine);
        planingLayout = (LinearLayout) findViewById(R.id.planningLayout);
        changeViewPlanning = (Button) findViewById(R.id.changeViewPlanning);
        libelleDay = (TextView) findViewById(R.id.libelleDay);
        cadreCours = (LinearLayout) findViewById(R.id.cadrePlanning);
        Thread jour = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void run() {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                try {
                    listPlanningDay(date);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        jour.start();

        //PLANNING SEMAINE
//        Thread semaine = new Thread(new Runnable() {
//            public void run() {
//                try {
//                    OkHttpClient client = new OkHttpClient();
//                    Request request = new Request.Builder()
//                            .url("https://apicnam.000webhostapp.com/API/Controllers/PlanningController.php?view=planning&id=" + MainActivity.formation.getString("ID_CNAM"))
//                            .build();
//                    Response response = client.newCall(request).execute();
//                    String responseBody = response.body().string();
//                    System.out.println("Response :  " + responseBody);
//                    JSONArray coursSemaine = new JSONArray(responseBody);
//                    ArrayList<String> joursSemaine = new ArrayList<>();
//
//                    for(int i = 0; i < coursSemaine.length();i++) {
//                        JSONObject detailCours = coursSemaine.getJSONObject(i);
//                        if(!joursSemaine.contains(detailCours.getString("dayOfWeek"))){
//                            final TextView jour = new TextView(getApplicationContext());
//                            jour.setText(detailCours.getString("dayOfWeek"));
//                            jour.setGravity(Gravity.CENTER);
//                            jour.setPadding(0,20,0, 20);
//                            jour.setTextColor(Color.rgb(196,4,44));
//                            jour.setTypeface(Typeface.DEFAULT_BOLD);
//                            jour.setTextSize(16);
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    planningSemaine.addView(jour);
//                                }
//                            });
//                            joursSemaine.add(detailCours.getString("dayOfWeek"));
//                        }
//                        final TextView horaire = new TextView(getApplicationContext());
//                        final TextView unite = new TextView(getApplicationContext());
//                        horaire.setText(detailCours.getString("horaire"));
//                        unite.setText(detailCours.getString("unite"));
//                        unite.setPadding(5,5, 5, 10);
//                        horaire.setPadding(5,5, 5, 5);
//                        unite.setTextColor(Color.BLACK);
//                        horaire.setTextColor(Color.BLACK);
//                        unite.setTypeface(Typeface.DEFAULT_BOLD);
//                        horaire.setTypeface(Typeface.DEFAULT_BOLD);
//
//
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                planningSemaine.addView(horaire);
//                                planningSemaine.addView(unite);
//                            }
//                        });
//                    }
//                } catch (IOException | JSONException e) {
//                    e.printStackTrace();
//                }
//            }});
//        semaine.start();
    }

    public JSONArray getPlanningJour(final String date) {
        JSONArray results = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://apicnam.000webhostapp.com/API/Controllers/PlanningController.php?view=getByDate&date=" + date + "&id=" + MainActivity.formation.getString("ID_FORMATION"))
                    .build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            //System.out.println("Response :  " + responseBody);
            JSONObject jsonResponse = new JSONObject(responseBody);
            if(jsonResponse.has("events")) {
                results = jsonResponse.getJSONArray("events");
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return results;
    }

    public void listPlanningDay(final LocalDate date) throws JSONException {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                cadreCours.removeAllViews();
//            }
//        });
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        JSONArray cours = getPlanningJour(dtf.format(date));
        //final DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("EE, dd MMMM yyyy");
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Date date1 = Date.from(date.atStartOfDay(defaultZoneId).toInstant());
        final String timeStamp = new SimpleDateFormat("EEEE d MMMM", Locale.FRANCE).format(date1);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                libelleDay.setText(timeStamp);
                libelleDay.setGravity(Gravity.CENTER);
                libelleDay.setTextColor(Color.BLACK);
            }
        });
        if(cours != null) {
            for (int i = 0; i < cours.length(); i++) {
                JSONObject detailCours = cours.getJSONObject(i);
                final LinearLayout unCours = new LinearLayout(getApplicationContext());
                unCours.setOrientation(LinearLayout.VERTICAL);
                TextView horaires = new TextView(getApplicationContext());
                horaires.setText(detailCours.getString("HEURE_DEB") + " à " + detailCours.getString("HEURE_FIN"));
                TextView libelle = new TextView(getApplicationContext());
                libelle.setText(detailCours.getString("CODE") + " : " + detailCours.getString("LIBELLE"));
                TextView profEtSalle = new TextView(getApplicationContext());
                profEtSalle.setText(detailCours.getString("PRENOM") + " " + detailCours.getString("NOM") + "  -  " + detailCours.getString("LIB_SALLE"));
                horaires.setPadding(5, 10, 5, 10);
                libelle.setPadding(5, 5, 5, 10);
                libelle.setPadding(5, 5, 5, 10);
                horaires.setTextColor(Color.rgb(196,4,44));
                libelle.setTextColor(Color.BLACK);
                profEtSalle.setTextColor(Color.DKGRAY);
                horaires.setTypeface(Typeface.DEFAULT_BOLD);
                libelle.setTypeface(Typeface.DEFAULT_BOLD);
                profEtSalle.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC), Typeface.ITALIC);
                horaires.setGravity(Gravity.CENTER);
                libelle.setGravity(Gravity.CENTER);
                profEtSalle.setGravity(Gravity.CENTER);
                unCours.addView(horaires);
                unCours.addView(libelle);
                unCours.addView(profEtSalle);
                unCours.setPadding(10,20,10,20);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cadreCours.addView(unCours);
                    }
                });

            }
        }
    }

    public void nextDayPlanning(View v) {
        date = LocalDate.parse(date.toString()).plusDays(1);
        Thread nextDay = new Thread(new Runnable() {
            public void run() {
                //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                try {
                    // System.out.println(dtf.format(date));
                    listPlanningDay(date);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        nextDay.start();
    }

    public void prevDayPlanning(View v) {
        date = LocalDate.parse(date.toString()).minusDays(1);
        Thread prevDay = new Thread(new Runnable() {
            public void run() {
                try {
                    listPlanningDay(date);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        prevDay.start();
    }


    public void changeView(View v) {
//        System.out.println("Visible : " + View.VISIBLE);
//        System.out.println("Gone : " + View.GONE);
//        System.out.println(planningJour.getVisibility() + "--" + planningSemaine.getVisibility());
//        planingLayout.removeView(planningJour);
//        planingLayout.removeView(planningSemaine);

        if(semaineActive == false) {
            semaineActive = true;
            planningSemaine.setVisibility(View.VISIBLE);
            planningJour.setVisibility(View.GONE);
            changeViewPlanning.setText("Aujourdhui");
        } else {
            semaineActive = false;
            planningJour.setVisibility(View.VISIBLE);
            planningSemaine.setVisibility(View.GONE);
            changeViewPlanning.setText("Cette semaine");
        }

//        if(planningJour.getVisibility() != View.GONE) {
//            System.out.println("Coucou1");
///*            planningSemaine.setVisibility(View.VISIBLE);
//            planningJour.setVisibility(View.GONE);
//            System.out.println(planningJour.getVisibility());
//            changeViewPlanning.setText("Aujourdhui");*/
//            planingLayout.addView(planningSemaine);
//        }
//        if(planningSemaine.getVisibility() != View.GONE) {
///*            planningJour.setVisibility(View.VISIBLE);
//            planningSemaine.setVisibility(View.GONE);
//            changeViewPlanning.setText("Cette semaine");*/
//            planingLayout.addView(planningJour);
//        }
    }

    public void viewHome(View v) {
        Intent homeActivity = new Intent(PlanningActivity.this, HomeActivity.class);
        startActivity(homeActivity);
    }
}