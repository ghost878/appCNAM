<?php 

require_once("../Controllers/DBController.php");

/**
 * Class Auditeur
 */
class Auditeur {

    private $auditeurs = array();
    
    /**
     * Fonction: getAllAuditeurs
     * Description: Fonction qui retourne la liste complète de tous les auditeurs de la base de données
     */
    public function getAllAuditeurs() {
        $query = "SELECT * FROM auditeurs";
        $dbcontroller = new DBController();
        $this->auditeurs = $dbcontroller->executeSelectQuery($query);
        return $this->auditeurs;
    }

    /**
     * Fonction: checkUserExists
     * Description: Fonction qui vérifie que l'utilisateur dont le pseudo et le mot de passe existe en base. 
     */
    public function checkUserExists($pseudo,$password) {
        $dbcontroller = new DBController();
        $mysqli = $dbcontroller->getMySQLIObject();
        // création requête préparée
        $stmt = $mysqli->prepare("SELECT * FROM auditeurs WHERE IDENTIFIANT_ENF=? OR MEL_PRO=? AND PASSWORD=?");
        // injection des paramètres
        $stmt->bind_param('sss',$pseudo,$pseudo,$password);
        // exécution de la requête
        $stmt->execute();
        $auditeurs = $stmt->get_result();
        if ($auditeurs->num_rows == 0) {
            return false;
        } else {
            $this->auditeurs = $auditeurs->fetch_array();
            return ["exist" => true,"auditeur" => $this->auditeurs];
        }
    }
}
