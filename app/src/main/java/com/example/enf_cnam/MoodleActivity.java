package com.example.enf_cnam;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Iterator;

public class MoodleActivity extends AppCompatActivity {

    private LinearLayout cadreFichiers;
    private LinearLayout moodleLayout;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moodle);
        cadreFichiers = (LinearLayout) findViewById(R.id.cadreFichiers);
        moodleLayout = (LinearLayout) findViewById(R.id.moodleLayout);

        cadreFichiers.removeAllViews();
        cadreFichiers.setPadding(10, 10 , 10, 10);
        TextView moodleInfo1 = (TextView) findViewById(R.id.moodleInfo1);
        TextView moodleInfo2 = (TextView) findViewById(R.id.moodleInfo2);
        TextView moodleInfo3 = (TextView) findViewById(R.id.moodleInfo3);
        try {
            moodleInfo1.setText(HomeActivity.uniteClick.getString("CODE"));
            moodleInfo2.setText(HomeActivity.uniteClick.getString("LIBELLE"));
            moodleInfo2.setTextColor(Color.BLACK);
            moodleInfo2.setTextSize(15);
            moodleInfo2.setTypeface(Typeface.DEFAULT_BOLD);
            moodleInfo3.setText(HomeActivity.uniteClick.getString("ANNEE_DEBUT") + " - " + HomeActivity.uniteClick.getString("ANNEE_FIN"));
            moodleInfo3.setTextColor(Color.BLACK);
            moodleInfo3.setTextSize(15);
        } catch (JSONException e) {
            e.printStackTrace();
        }
// APPROCHE PAR HTTP (plus securisé)
        Thread docs = new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject fichiersMoodle = HttpRequest.requestGet(
                            MainActivity.token,
                            "https://apicnam.000webhostapp.com/API/Controllers/AuditeurController.php?view=files_unite&unite=" + HomeActivity.uniteClick.getString("CODE")
                    );
                    Iterator<String> keys = fichiersMoodle.keys();
                    while(keys.hasNext()) {
                        final String key = keys.next();
                        final TextView dirName = new TextView(getApplicationContext());
                        dirName.setText(key);
                        dirName.setTextColor(Color.BLACK);
                        dirName.setTypeface(Typeface.DEFAULT_BOLD);
                        dirName.setPadding(5, 20, 5, 20);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cadreFichiers.addView(dirName);
                            }
                        });
                        final JSONObject files = fichiersMoodle.getJSONObject(key);
                        Iterator<String> listFiles = files.keys();
                        while(listFiles.hasNext()) {
                            final String cpt = listFiles.next();
                            final TextView filename = new TextView(getApplicationContext());
                            filename.setText(files.getString(cpt));
                            filename.setTextColor(Color.rgb(51, 102, 187));
                            filename.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                            filename.setPadding(15, 5, 5, 5);
                            filename.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    Intent viewIntent = null;
                                    try {
                                        viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://apicnam.000webhostapp.com/Moodle/" + HomeActivity.uniteClick.getString("CODE") + "/" + key + "/" + files.getString(cpt)));
                                        viewIntent.setPackage("com.android.chrome");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(viewIntent);
                                }
                            });
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    cadreFichiers.addView(filename);
                                }
                            });
                        }
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        docs.start();
    }

    public void viewHome(View v) {
        Intent homeActivity = new Intent(MoodleActivity.this, HomeActivity.class);
        startActivity(homeActivity);
    }

    public void logout(View v) {
        Intent mainActivity = new Intent(MoodleActivity.this, MainActivity.class);
        startActivity(mainActivity);
        MainActivity.token = "";
    }

    public void mail(View v) {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://login.live.com/login.srf?wa=wsignin1.0&rpsnv=13&ct=1610371321&rver=7.0.6737.0&wp=MBI_SSL&wreply=https%3a%2f%2foutlook.live.com%2fowa%2f%3fnlp%3d1%26RpsCsrfState%3db3d1dea9-4053-5262-434d-0b14a393acbf&id=292841&aadredir=1&CBCXT=out&lw=1&fl=dob%2cflname%2cwld&cobrandid=90015"));
        viewIntent.setPackage("com.android.chrome");
        startActivity(viewIntent);
    }

    public void viewUserInfo(View v) throws JSONException {
        Intent userActivity = new Intent(MoodleActivity.this, UserActivity.class);
        startActivity(userActivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MainActivity.token == "") {
            Intent mainActivity = new Intent(MoodleActivity.this, MainActivity.class);
            startActivity(mainActivity);
        }
    }
}