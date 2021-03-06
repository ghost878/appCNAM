package com.example.enf_cnam;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserActivity extends AppCompatActivity {

    private LinearLayout infoLayout;
    private LinearLayout cadreInfo;
    private ArrayList<View> viewList;
    private Spinner langue;
    //public static String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        infoLayout = (LinearLayout) findViewById(R.id.infoLayout);
        cadreInfo = (LinearLayout) findViewById(R.id.cadreInfo);
        viewList = new ArrayList<>();

        try {
            listInfo(MainActivity.auditeurInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ResourceType")
    /**
     * @param jsonInfo Données json de l'utilisateur
     * Description: Création des différents champs du formulaire de modification des informations personnelles, prérempli avec les informations actuelles de la BD.
     */
    public void listInfo(JSONObject jsonInfo) throws JSONException {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cadreInfo.removeAllViews();            }
        });

        Iterator<String> keys = jsonInfo.keys();
        ArrayList<String> blackList = new ArrayList<>();
        blackList.add("ID_AUDITEUR");
        blackList.add("ID_FORMATION");
        blackList.add("IDENTIFIANT_ENF");
        ArrayList<String> disableList = new ArrayList<>();
        disableList.add("NOM");
        disableList.add("DATE_NAISSANCE");
        disableList.add("MEL_PRO");
        disableList.add("INE_CNAM");
        ArrayList<String> spinnerList = new ArrayList<>();
        spinnerList.add("CIVILITE");
        spinnerList.add("NATIONALITE");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        //params.gravity = Gravity.CENTER;
        while(keys.hasNext()) {
            final String key = keys.next();
            if (!blackList.contains(key) && key.length() > 2) {
                final LinearLayout ligne = new LinearLayout(getApplicationContext());
                ligne.setPadding(30,10,30,10);
                //params.height = 20;
                ligne.setLayoutParams(params);
                ligne.setOrientation(LinearLayout.HORIZONTAL);
                String packageName = getPackageName();
                int resId = getResources().getIdentifier(key, "string", packageName);
                TextView libelle = new TextView(getApplicationContext());
                libelle.setText(getString(resId) + " : ");
                libelle.setWidth(400);
                ligne.addView(libelle);

                if (spinnerList.contains(key)) {
                    List<String> spinner = new ArrayList<String>();
                    switch (key) {
                        case "NATIONALITE":
                            spinner.add("Français");
                            spinner.add("Anglais");
                            spinner.add("Allemand");
                            break;
                        case "CIVILITE":
                            spinner.add("Monsieur");
                            spinner.add("Madame");
                            spinner.add("Mademoiselle");
                            break;
                        default:
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            this, android.R.layout.simple_spinner_item, spinner);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    Spinner select = new Spinner(getApplicationContext());
                    select.setAdapter(adapter);
                    select.setSelection(adapter.getPosition(jsonInfo.getString(key)));
                    select.setTag(key);
                    select.setMinimumWidth(500);
                    select.setGravity(Gravity.CENTER);
                    ligne.addView(select);
                    viewList.add(select);
                } else {
                    EditText editText = new EditText(getApplicationContext());
                    editText.setText(jsonInfo.getString(key));
                    editText.setTag(key);
                    editText.setWidth(500);
                    editText.setGravity(Gravity.CENTER);
                    ligne.addView(editText);
                    viewList.add(editText);
                    if (disableList.contains(key)) {
                        editText.setEnabled(false);
                    }
                    if(key.equals("PASSWORD")) {
                        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());

                    }

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cadreInfo.addView(ligne);
                    }
                });
            }

        }
        final LinearLayout ligneLangue = new LinearLayout(getApplicationContext());
        ligneLangue.setLayoutParams(params);
        ligneLangue.setOrientation(LinearLayout.HORIZONTAL);
        ligneLangue.setPadding(30,10,30,50);
        TextView libelleLangue = new TextView(getApplicationContext());
        libelleLangue.setWidth(400);
        libelleLangue.setText("LANGUE" + " : ");
            List<String> spinner = new ArrayList<String>();
                    spinner.add("FR");
                    spinner.add("EN");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, spinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            langue = new Spinner(getApplicationContext());
            langue.setAdapter(adapter);
            System.out.println(Locale.getDefault().getLanguage());
            langue.setSelection(adapter.getPosition(MainActivity.lang));
            langue.setTag("LANGUE");
            langue.setMinimumWidth(500);
            langue.setGravity(Gravity.CENTER);
        ligneLangue.addView(libelleLangue);
        ligneLangue.addView(langue);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cadreInfo.addView(ligneLangue);
            }
        });


    }

    @SuppressLint("ResourceType")
    /**
     * @param v
     * Description: Fonction se chargeant de faire la requête de modification des informations personnelles de l'utilisateur.
     */
    public void editInfo(View v) throws JSONException {
        final JSONObject data = new JSONObject();
        JSONObject infos = new JSONObject();
        infos.put("ID_AUDITEUR", MainActivity.auditeurInfo.getString("ID_AUDITEUR"));
        for(int i=0; i < viewList.size();i++) {
            View view =  viewList.get(i);
            if (view instanceof EditText) {
                EditText edit = (EditText) view;
                infos.put((String) edit.getTag(), edit.getText());
            }
            if (view instanceof Spinner) {
                Spinner spinner = (Spinner) view;
                infos.put((String) spinner.getTag(), spinner.getSelectedItem().toString());

            }
        }
        data.put("auditeur", infos);
        System.out.println(data.toString());
        Thread edit = new Thread(new Runnable() {
            public void run() {
                System.out.println("Coucou2");
                OkHttpClient client = new OkHttpClient();

                RequestBody formBody = new FormBody.Builder()
                        .add("data",data.toString())
                        .build();
                try {
                Request request = new Request.Builder()
                        .url("https://apicnam.000webhostapp.com/API/Controllers/AuditeurController.php?view=edit&id=" + MainActivity.auditeurInfo.getString("ID_AUDITEUR"))
                        //.headers("Content-Type", "application/json", "Accept-Language", "fr", "Authorization", )
                        .addHeader("content-type", "application/json")
                        .addHeader("accept-Language", "fr")
                        .addHeader("authorization", MainActivity.token)
                        .method("POST", formBody)
                        .build();
                Response response = null;

                    response = client.newCall(request).execute();
                    String responseBody = response.body().string();
                    System.out.println("Response :  " + responseBody);
                    final JSONObject jsonResponse = new JSONObject(responseBody);
                    boolean editDone = jsonResponse.getBoolean("exist");
                    if(editDone == true) {

                      // listInfo(jsonResponse.getJSONObject("auditeur"));
                        MainActivity.auditeurInfo = jsonResponse.getJSONObject("auditeur");
                        Intent userActivity = new Intent(UserActivity.this, UserActivity.class);
                        startActivity(userActivity);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Context context = getApplicationContext();
                                CharSequence errorMessage = getString(R.string.sucessMessage);
                                int duration = Toast.LENGTH_LONG;
                                Toast toast = Toast.makeText(context, errorMessage, duration);
                                toast.show();
                            }});
                        switchLangue(langue.getSelectedItem().toString());

                    }
                    else {
                        System.out.println("TOAST");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Context context = getApplicationContext();
                                CharSequence errorMessage = null;
                                try {
                                    String packageName = getPackageName();
                                    int resId = getResources().getIdentifier(jsonResponse.getString("error"), "string", packageName);
                                    getString(resId);
                                    errorMessage = getString(resId);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                int duration = Toast.LENGTH_LONG;
                                Toast toast = Toast.makeText(context,errorMessage,duration);
                                toast.show();
                            }
                        });
                    }

                } catch (IOException | JSONException /*| JSONException*/ e) {
                    e.printStackTrace();
                }
            }
        });
        edit.start();
    }

    /**
     * Description: Fonction de changement de langue
     * @param langue Langue souhaitée
     */
    public void switchLangue(String langue) {
        System.out.println("lang" + langue);
        MainActivity.lang = langue;
//        String langue = Locale.getDefault().getLanguage();
//        System.out.println("SWITCH" + langue);// your language
        Locale locale = new Locale(langue);
        Locale.setDefault(locale);
        System.out.println("Country" + locale.getCountry());
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    /**
     * Description: Redirection vers la vue HOME
     * @param v
     */
    public void viewHome(View v) {
        Intent homeActivity = new Intent(UserActivity.this, HomeActivity.class);
        startActivity(homeActivity);
    }

    /**
     * Description: Fonction de déconnexion
     * @param v
     */
    public void logout(View v) {
        Intent mainActivity = new Intent(UserActivity.this, MainActivity.class);
        startActivity(mainActivity);
        MainActivity.token = "";
    }

    /**
     * Description: Fonction de redirection vers Outlook
     * @param v
     */
    public void mail(View v) {
        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://login.live.com/login.srf?wa=wsignin1.0&rpsnv=13&ct=1610371321&rver=7.0.6737.0&wp=MBI_SSL&wreply=https%3a%2f%2foutlook.live.com%2fowa%2f%3fnlp%3d1%26RpsCsrfState%3db3d1dea9-4053-5262-434d-0b14a393acbf&id=292841&aadredir=1&CBCXT=out&lw=1&fl=dob%2cflname%2cwld&cobrandid=90015"));
        viewIntent.setPackage("com.android.chrome");
        startActivity(viewIntent);
    }

    /**
     * Description: Fonction de redirection vers la page de visualisation des informations personnelles.
     * @param v
     */
    public void viewUserInfo(View v) {
        Context context = getApplicationContext();
        CharSequence errorMessage = getString(R.string.knownActivity);
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, errorMessage, duration);
        toast.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MainActivity.token == "") {
            Intent mainActivity = new Intent(UserActivity.this, MainActivity.class);
            startActivity(mainActivity);
        }
    }

}