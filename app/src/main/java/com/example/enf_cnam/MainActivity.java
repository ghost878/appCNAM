package com.example.enf_cnam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    private Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Cette directive enlève la barre de titre
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.requestWindowFeature(Window.)
// Cette directive permet d'enlever la barre de notifications pour afficher l'application en plein écran
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//On définit le contenu de la vue APRES les instructions précédentes pour éviter un crash
        //this.setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MySQLConnection db = null;
        try {
            db = new MySQLConnection("jdbc:mysql:eu-cdbr-west-03.cleardb.net","b6b3d65e2ecfa2","8e57f74e");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.connection = db.getConnection();
        String query2 = "SELECT *  FROM auditeurs";
        try {
            PreparedStatement ps2 = this.connection.prepareStatement(query2);
            ResultSet results2 = ps2.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }



    }
}