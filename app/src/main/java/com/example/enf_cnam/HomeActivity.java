package com.example.enf_cnam;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


@RequiresApi(api = Build.VERSION_CODES.O)
public class HomeActivity extends AppCompatActivity {

    public static JSONObject uniteClick;
    private LinearLayout homeLayout;
    private LinearLayout enseignements;
    private TextView hello;

    /**
     * Création de de l'activité d'accueil . Cette méthode affiche les boutons redirigeant aux autres interfaces
     * de l'applicatiion Planning, Cursus etc et liste les différentes unités d'un auditeur pour accéder à leur interface Moodle
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeLayout = (LinearLayout) findViewById(R.id.homeLayout);
        enseignements = (LinearLayout) findViewById(R.id.enseignements);
        hello = (TextView) findViewById(R.id.hello);

        Locale locale = new Locale(MainActivity.lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        hello.setTextColor(Color.BLACK);
        hello.setPadding(0,30,30,0);
        try {
            String name = "<font color='#c1002a'>"+ MainActivity.auditeurInfo.getString("PRENOM") + " " + MainActivity.auditeurInfo.getString("NOM") +"</font>";
            hello.setText(Html.fromHtml(getString(R.string.hello) + name));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        hello.setTextSize(15);
        hello.setGravity(Gravity.RIGHT);
        hello.setTypeface(Typeface.DEFAULT_BOLD);

        for(int i = 0; i < MainActivity.enseignements.length(); i++) {
            LinearLayout unite = new LinearLayout(this);
            unite.setOrientation(LinearLayout.VERTICAL);
            try {
                final JSONObject uniteDetails = MainActivity.enseignements.getJSONObject(i);
                TextView libelle = new TextView(this);
                libelle.setText(uniteDetails.getString("LIBELLE"));
                libelle.setGravity(Gravity.CENTER);
                libelle.setTextColor(Color.BLACK);
                libelle.setTypeface(Typeface.DEFAULT_BOLD);
                TextView code = new TextView(this);
                code.setText(uniteDetails.getString("CODE"));
                code.setGravity(Gravity.CENTER);
                code.setTextColor(Color.BLACK);
                code.setTypeface(Typeface.DEFAULT_BOLD);
                unite.addView(code);
                unite.addView(libelle);
                unite.setGravity(Gravity.CENTER);
                unite.setLayoutParams(new LinearLayout.LayoutParams(300, 400));
                //unite.setPadding(-30,0,-30,0);
                unite.setBackgroundResource(R.drawable.unite);
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) unite.getLayoutParams();
                p.setMargins(20, 0, 20, 0);

                unite.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        uniteClick = uniteDetails;
                        Intent moodleActivity = new Intent(HomeActivity.this, MoodleActivity.class);
                        startActivity(moodleActivity);
                    }});
                enseignements.addView(unite);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Méthode du fragment redirigeant vers la page du profil utilisateur
     * @param v
     * @throws JSONException
     */
    public void viewUserInfo(View v) throws JSONException {
        Intent userActivity = new Intent(HomeActivity.this, UserActivity.class);
        startActivity(userActivity);
    }

    /**
     * Méthode de la page redirigeant vers la page du planning
     * @param v
     * @throws JSONException
     */
    public void viewPlanning(View v) throws JSONException {
        Intent planningActivity = new Intent(HomeActivity.this, PlanningActivity.class);
        startActivity(planningActivity);
    }

    /**
     * Méthode de la page redirigeant vers la page du cursus
     * @param v
     */
    public void viewCursus(View v) {
        Intent cursusActivity = new Intent(HomeActivity.this, CursusActivity.class);
        startActivity(cursusActivity);
    }

    /**
     * Méthode de la page redirigeant vers la page des examens
     * @param v
     */
    public void viewExamen(View v) {
        Intent examenActivity = new Intent(HomeActivity.this, ExamenActivity.class);
        startActivity(examenActivity);
    }

    /**
     * Méthode du fragment déconnectant l'utilisateur en écrasant le token
     * et redirigeant vers la page d'authentification
     * @param v
     */
    public void logout(View v) {
        Intent mainActivity = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(mainActivity);
        MainActivity.token = "";
    }

    /**
     * Méthode du fragment qui redirige vers Outlook
     * @param v
     */
    public void mail(View v) {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://login.live.com/login.srf?wa=wsignin1.0&rpsnv=13&ct=1610371321&rver=7.0.6737.0&wp=MBI_SSL&wreply=https%3a%2f%2foutlook.live.com%2fowa%2f%3fnlp%3d1%26RpsCsrfState%3db3d1dea9-4053-5262-434d-0b14a393acbf&id=292841&aadredir=1&CBCXT=out&lw=1&fl=dob%2cflname%2cwld&cobrandid=90015"));
        viewIntent.setPackage("com.android.chrome");
        startActivity(viewIntent);
    }

    /**
     * Méthode de la page redirigeant vers la page du plan GoogleMaps
     * @param v
     */
    public void viewMap(View v) {
        Intent mainActivity = new Intent(HomeActivity.this, MapActivity.class);
        startActivity(mainActivity);
    }

    /**
     * Méthode du fragment qui informe l'utilisateur qu'il est déjà sur cette activité
     * @param v
     */
    public void viewHome(View v) {
        Context context = getApplicationContext();
        CharSequence errorMessage = getString(R.string.knownActivity);
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, errorMessage, duration);
        toast.show();
    }

    /**
     * Méthode de rappel de page. Elle vérifie que si le token de connexion est nul, cela redirige vers la page d'authentification
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(MainActivity.token == "") {
            Intent mainActivity = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(mainActivity);
        }
    }
}