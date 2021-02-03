package com.example.enf_cnam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mapView = (MapView) findViewById(R.id.mapView);


        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Thread markers = new Thread(new Runnable() {
            public void run() {
                final JSONArray etablissements = getEtablissements();
                //System.out.println(etablissements);
                //final String[] destinations = {""};
                for (int i = 0; i < etablissements.length(); i++) {
                    try {
                        final JSONObject etab = etablissements.getJSONObject(i);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LatLng cnamEtab = null;
                                try {
                                    cnamEtab = new LatLng(etab.getDouble("LATITUDE"), etab.getDouble("LONGITUDE"));
                                    mMap.addMarker(new MarkerOptions()
                                            .position(cnamEtab)
                                            .title(etab.getString("LIBELLE")));
                                    //destinations += etab.getDouble("LATITUDE") + "," + etab.getDouble("LATITUDE") + "|";

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                //mMap.moveCamera(CameraUpdateFactory.newLatLng(cnamEtab));
                            }
                        });
//
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        markers.start();


        // Afficher Sydney
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(48.72234, 5.65267), 6));

    }

        
    public void findNearestCNAM(View v) {
        Thread nearest = new Thread(new Runnable() {
            public void run() {
                final JSONArray etablissements = getEtablissements();
                final ArrayList<double[]> listDestinations = new ArrayList<>();

                String destinations = "";
                String origins = "";
                for (int i = 0; i < etablissements.length(); i++) {
                    try {
                        final JSONObject etab = etablissements.getJSONObject(i);
                        double[] coord = new double[2];
                        coord[0] = etab.getDouble("LATITUDE");
                        coord[1] = etab.getDouble("LONGITUDE");
                        listDestinations.add(coord);
                        destinations += etab.getDouble("LATITUDE") + "," + etab.getDouble("LONGITUDE") + "|";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                LocationManager lm = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String bestProvider = String.valueOf(lm.getBestProvider(criteria, true));
                try {
                    Location location = lm.getLastKnownLocation(bestProvider);
                    origins = location.getLatitude() + "," + location.getLongitude();
                    System.out.println(location);
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    System.out.println("Coucou " + latitude + "--" + longitude);
                } catch (SecurityException e) {
                    System.out.println("Erreur"); // lets the user know there is a problem with the gps
                }

                System.out.println("Origins" + origins + " Dest" + destinations );


                System.out.println(getDistances(origins,destinations));
                JSONArray distances = getDistances(origins,destinations);
                int indexMinimum = -1;
                try {
                    //int minValue = distances.getJSONObject(0).getJSONObject("distance").getInt("value");
                    int minValue = 99999999;
                    System.out.println(minValue);
                    for(int i= 0; i < distances.length();i++) {
                        if(distances.getJSONObject(i).getJSONObject("distance").getInt("value") < minValue){
                            minValue = distances.getJSONObject(i).getJSONObject("distance").getInt("value");
                            indexMinimum = i;
                        }
                    }

                    System.out.println(minValue + " " + indexMinimum);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Handler mainHandler = new Handler(getApplicationContext().getMainLooper());
                final int finalIndexMinimum = indexMinimum;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(listDestinations.get(finalIndexMinimum)[0], listDestinations.get(finalIndexMinimum)[1]), 8));

                    }});
            }
        });
        nearest.start();
    }

    public JSONArray getEtablissements() {
        JSONArray results = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://apicnam.000webhostapp.com/API/Controllers/EtablissementController.php?view=all")
                    .build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            //System.out.println("Response :  " + responseBody);
            JSONObject jsonResponse = new JSONObject(responseBody);

                results = jsonResponse.getJSONArray("etablissements");
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return results;
    }

    public JSONArray getDistances(String origins, String destinations) {
        JSONArray results = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/distancematrix/json?origins="+ origins +"&destinations="+ destinations +"&key=AIzaSyAN9k9wDxeBADhS0HyPvo4OHli7T7go1w4&mode=driving&language=en&units=metrics")
                    .build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);

            results = jsonResponse.getJSONArray("rows").getJSONObject(0).getJSONArray("elements");
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return results;
    }




    public void viewHome(View v) {
        Intent homeActivity = new Intent(MapActivity.this, HomeActivity.class);
        startActivity(homeActivity);
    }
}