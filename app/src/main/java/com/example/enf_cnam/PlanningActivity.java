package com.example.enf_cnam;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@RequiresApi(api = Build.VERSION_CODES.O)
public class PlanningActivity extends AppCompatActivity {
    private LinearLayout planingLayout;
    private LinearLayout planningJour;
    private LinearLayout planningSemaine;
    private LinearLayout cadrePlanningDay;
    private LinearLayout cadrePlanningWeek;
    private Button changeViewPlanning;
    private TextView libelleDay;
    private TextView libelleWeek;
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
        libelleWeek = (TextView) findViewById(R.id.libelleWeek);
        cadrePlanningDay = (LinearLayout) findViewById(R.id.cadrePlanningDay);
        cadrePlanningWeek = (LinearLayout) findViewById(R.id.cadrePlanningWeek);
        planningSemaine.setVisibility(View.GONE);

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

        Thread semaine = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void run() {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                try {
                    listPlanningWeek(date);
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        semaine.start();
    }

    public JSONArray getPlanningJour(final String date) {
        JSONArray results = null;
        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://apicnam.000webhostapp.com/API/Controllers/PlanningController.php?view=getByDate&date=" + date + "&id=" + MainActivity.formation.getString("ID_FORMATION"))
                    .addHeader("content-type", "application/json")
                    .addHeader("accept-Language", "fr")
                    .addHeader("authorization", MainActivity.token)
//                    .method("POST", formBody)
                    .build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            System.out.println("Response :  " + responseBody);
            JSONObject jsonResponse = new JSONObject(responseBody);
            if(jsonResponse.has("events")) {
                results = jsonResponse.getJSONArray("events");
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return results;
    }


    public JSONArray getPlanningSemaine(final String date) {
        JSONArray results = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://apicnam.000webhostapp.com/API/Controllers/PlanningController.php?view=weekByDate&date=" + date + "&id=" + MainActivity.formation.getString("ID_CNAM"))
                    .addHeader("content-type", "application/json")
                    .addHeader("accept-Language", "fr")
                    .addHeader("authorization", MainActivity.token)
                    .build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            System.out.println("Response :  " + responseBody);
            if(!responseBody.contains("error")) {
                results = new JSONArray(responseBody);
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return results;
    }



    public void listPlanningDay(final LocalDate date) throws JSONException {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cadrePlanningDay.removeAllViews();
            }
        });
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
                libelleDay.setTypeface(Typeface.DEFAULT_BOLD);
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
                        cadrePlanningDay.addView(unCours);
                    }
                });

            }
        } else {
            final TextView info = new TextView(getApplicationContext());
            info.setText("Aucun cours cette journée");
            info.setTypeface(Typeface.DEFAULT_BOLD);
            info.setGravity(Gravity.CENTER);
            info.setPadding(0,100,0,0);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cadrePlanningDay.addView(info);
                }
            });
        }
    }

    public void listPlanningWeek(final LocalDate date) throws JSONException, ParseException {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cadrePlanningWeek.removeAllViews();
            }
        });
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        JSONArray cours = getPlanningSemaine(dtf.format(date));
        System.out.println(cours);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date date1 = df.parse(dtf.format(date));
        //Date date1 = df.parse("01/01/2021");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        final int week = cal.get(Calendar.WEEK_OF_YEAR)-1;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                libelleWeek.setText("Semaine " + week);
                libelleWeek.setGravity(Gravity.CENTER);
                libelleWeek.setTextColor(Color.BLACK);
                libelleWeek.setTypeface(Typeface.DEFAULT_BOLD);
            }
        });
        ArrayList<String> joursSemaine = new ArrayList<>();
        if(cours != null) {
            for (int i = 0; i < cours.length(); i++) {
                JSONObject detailCours = cours.getJSONObject(i);
                System.out.println(detailCours.getString("unite"));
                final LinearLayout unCours = new LinearLayout(getApplicationContext());
                unCours.setOrientation(LinearLayout.VERTICAL);
                if(!joursSemaine.contains(detailCours.getString("dayOfWeek"))){
                            final TextView jour = new TextView(getApplicationContext());
                            jour.setText(detailCours.getString("dayOfWeek"));
                            jour.setGravity(Gravity.CENTER);
                            jour.setPadding(0,20,0, 20);
                            jour.setTextColor(Color.rgb(196,4,44));
                            jour.setTypeface(Typeface.DEFAULT_BOLD);
                            jour.setTextSize(16);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cadrePlanningWeek.addView(jour);
                                }
                            });
                            joursSemaine.add(detailCours.getString("dayOfWeek"));
                        }
                TextView horaires = new TextView(getApplicationContext());
                horaires.setText(detailCours.getString("horaire"));
                TextView libelle = new TextView(getApplicationContext());
                libelle.setText(detailCours.getString("unite"));
                horaires.setPadding(5, 10, 5, 10);
                libelle.setPadding(5, 5, 5, 10);
                libelle.setPadding(5, 5, 5, 10);
                horaires.setTextColor(Color.rgb(196,4,44));
                libelle.setTextColor(Color.BLACK);
                horaires.setTypeface(Typeface.DEFAULT_BOLD);
                libelle.setTypeface(Typeface.DEFAULT_BOLD);
                horaires.setGravity(Gravity.CENTER);
                libelle.setGravity(Gravity.CENTER);
                unCours.addView(horaires);
                unCours.addView(libelle);
                unCours.setPadding(10,20,10,20);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cadrePlanningWeek.addView(unCours);
                    }
                });

            }
        } else {
            final TextView info = new TextView(getApplicationContext());
            info.setText("Aucun cours cette semaine");
            info.setTypeface(Typeface.DEFAULT_BOLD);
            info.setGravity(Gravity.CENTER);
            info.setPadding(0,100,0,0);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cadrePlanningWeek.addView(info);
                }
            });
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

    public void nextWeekPlanning(View v) {
        date = LocalDate.parse(date.toString()).plusDays(7);
        Thread nextDay = new Thread(new Runnable() {
            public void run() {
                //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                try {
                    // System.out.println(dtf.format(date));
                    listPlanningWeek(date);
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        nextDay.start();
    }

    public void prevWeekPlanning(View v) {
        date = LocalDate.parse(date.toString()).minusDays(7);
        Thread nextDay = new Thread(new Runnable() {
            public void run() {
                //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                try {
                    // System.out.println(dtf.format(date));
                    listPlanningWeek(date);
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        nextDay.start();
    }




    public void changeView(View v) {

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

    }

    public void viewHome(View v) {
        Intent homeActivity = new Intent(PlanningActivity.this, HomeActivity.class);
        startActivity(homeActivity);
    }
    public void viewUserInfo(View v) throws JSONException {
        Intent userActivity = new Intent(PlanningActivity.this, UserActivity.class);
        startActivity(userActivity);
    }

    public void logout(View v) {
        Intent mainActivity = new Intent(PlanningActivity.this, MainActivity.class);
        startActivity(mainActivity);
        MainActivity.token = "";
    }

    public void mail(View v) {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://login.live.com/login.srf?wa=wsignin1.0&rpsnv=13&ct=1610371321&rver=7.0.6737.0&wp=MBI_SSL&wreply=https%3a%2f%2foutlook.live.com%2fowa%2f%3fnlp%3d1%26RpsCsrfState%3db3d1dea9-4053-5262-434d-0b14a393acbf&id=292841&aadredir=1&CBCXT=out&lw=1&fl=dob%2cflname%2cwld&cobrandid=90015"));
        viewIntent.setPackage("com.android.chrome");
        startActivity(viewIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MainActivity.token == "") {
            Intent mainActivity = new Intent(PlanningActivity.this, MainActivity.class);
            startActivity(mainActivity);
        }
    }
}