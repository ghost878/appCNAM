package com.example.enf_cnam;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpRequest {

    /**
     *  Méthode appelant une requête GET HTTP vers l'APICNAM
     * @param token Token de connexion
     * @param url   URL
     * @return JSONObject
     * @throws IOException
     * @throws JSONException
     */
    public static JSONObject requestGet(String token, String url) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("content-type", "application/json")
                .addHeader("accept-Language", "fr")
                .addHeader("authorization", token)
                .build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
//        System.out.println(responseBody);
        JSONObject jsonResponse = new JSONObject(responseBody);
        return jsonResponse;
    }

    /**
     *  Méthode appelant une requête GET HTTP vers l'APICNAM
     * @param token Token de connexion
     * @param formBody Corps de la requête
     * @param url   URL
     * @return JSONObject
     * @throws IOException
     * @throws JSONException
     */
    public static JSONObject requestPost(String token, RequestBody formBody, String url) throws IOException, JSONException {
        OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("content-type", "application/json")
                    .addHeader("accept-Language", "fr")
                    .addHeader("authorization", token)
                    .method("POST", formBody)
                    .build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
//            System.out.println("Response :  " + responseBody);
            JSONObject jsonResponse = new JSONObject(responseBody);
        return jsonResponse;
    }


}
