package com.example.enf_cnam;

import java.sql.*;

public class MySQLConnection {

    private Connection connection;

    public MySQLConnection(String url, String user, String pass) throws SQLException, ClassNotFoundException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {
            System.out.println("Impossible de charger le driver com.mysql.jdbc.Driver");
            e.printStackTrace();
        }
        try {
            this.connection = DriverManager.getConnection(url,user,pass);
            System.out.println("Connexion établie avec la base de données");
        } catch (SQLException e) {
            System.out.println("Impossible d'accéder à la base de données");
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    public static void main(String[]args) throws SQLException, ClassNotFoundException {
        MySQLConnection db = new MySQLConnection("jdbc:mysql:eu-cdbr-west-03.cleardb.net","b6b3d65e2ecfa2","8e57f74e");
        Connection conn = db.getConnection();
        Statement stmt = conn.createStatement();
        /**
         * Exemple de requête avec BD
         */
        ResultSet rs = stmt.executeQuery("SELECT * FROM adherents");
        rs.next();
        String pseudo = rs.getString(1);
        System.out.println(pseudo);
    }
}
