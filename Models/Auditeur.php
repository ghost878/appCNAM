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
}