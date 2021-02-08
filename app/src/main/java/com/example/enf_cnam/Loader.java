package com.example.enf_cnam;

import android.app.ProgressDialog;
import android.os.AsyncTask;

class Loader extends AsyncTask<Void, Void, Void> {
    ProgressDialog pd;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("loading");
        pd.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        // Do your request
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (pd != null)
        {
            pd.dismiss();
        }
    }
}

//public class Loader extends AsyncTask<Void, Void, String> {
//
//    private ProgressDialog dialog;
//    public Loader(Activity activity) {
//        super();
//        this.dialog = new ProgressDialog(activity.getApplicationContext());
//    }
//
//    @Override
//    protected void onPreExecute() {
//        dialog.setMessage("Chargement...");
//        dialog.setIndeterminate(true);
//        dialog.show();
//    }
//
//    @Override
//    protected String doInBackground(Void... params) {
//        //TODO
//        return "";
//    }
//
//
//
//    @Override
//    protected void onPostExecute(String result) {
//
//    }
//}
