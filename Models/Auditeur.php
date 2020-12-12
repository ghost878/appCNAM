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
     * @param pseudo pseudo de l'utilisateur
     * @param password mot de passe de l'utilisateur
     * Fonction: checkUserExists
     * Description: Fonction qui vérifie que l'utilisateur dont le pseudo et le mot de passe existe en base.
     * Si l'utilisateur est trouvé, retourne ses informations.
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

    /**
     * @param idAuditeur identifiant de l'auditeur
     * Fonction: getEnseignements
     * Description: Fonction qui retourne la liste des enseignements et les données de l'auditeur dont l'identifiant est passé en paramètre
     */
    public function getEnseignements($idAuditeur) {
        $dbcontroller = new DBController();
        $mysqli = $dbcontroller->getMySQLIObject();
        $stmt = $mysqli->prepare("SELECT u.* FROM unites u, formations f, auditeurs a WHERE u.ID_FORMATION=f.ID_FORMATION AND a.ID_FORMATION=f.ID_FORMATION AND a.ID_AUDITEUR=?");
        $stmt->bind_param('i',$idAuditeur);
        $stmt->execute();
        $enseignements = $stmt->get_result();
        if ($enseignements->num_rows == 0) {
            return false;
        } else {
            return ["enseignements" => $enseignements->fetch_array()];
        }
    }
}
