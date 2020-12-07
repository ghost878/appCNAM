<?php 

class DBController {
    
    private $conn = "";
    private $host = "eu-cdbr-west-03.cleardb.net";
    private $user = "b6b3d65e2ecfa2";
    private $password = "8e57f74e";
    private $database = "heroku_d8cb00c3e8e4e88";

    /**
     * Constructeur
     * Description: intialise la connexion avec la base de données.
     */
    function __construct()
    {
        $conn = $this->connectDB();
        if (!empty($conn)) {
            $this->conn = $conn;
        }
    }

    /**
     * Fonction: connectDB
     * Description: Fonction en charge de la connexion avec la base de données.
     */
    function connectDB() {
        // tentative de création de la connexion avec la base de données
        $conn = new mysqli($this->host,$this->user,$this->password,$this->database);
        if ($conn->connect_errno) {
            echo "Impossible de se connecter à la base de données : " . $conn->connect_error;
            exit(); 
        }
        // Encodage en UTF-8
        $conn->set_charset("utf8");
        return $conn;
    }

    function getMySQLIObject() {
        return new mysqli($this->host,$this->user,$this->password,$this->database);
    }

    /**
     * Fonction: executeSelectQuery
     * Description: Fonction d'exécution de requête SQL
     */
    function executeSelectQuery($query) {
        $result = mysqli_query($this->conn, $query);
        while ($row=mysqli_fetch_assoc($result)) {
            $resultset [] = $row;
        }
        if (!empty($resultset)) {
            return $resultset;
        }
    }
}

?>