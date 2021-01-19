<?php

require_once("../Controllers/DBController.php");

/**
 * Class Note
 */
class Note {

    /**
     * @param id Identifiant de l'auditeur
     * Fonction: getByIdAuditeur
     * Description: Retourne toute les notes d'un auditeur
     */
    public function getByIdAuditeur($id) {
        $dbcontroller = new DBController();
        $mysqli = $dbcontroller->getMySQLIObject();
        $stmt = $mysqli->prepare("
           SELECT * 
           FROM notes n, unites u
           WHERE u.ID_UNITE=n.ID_UNITE
           AND n.ID_AUDITEUR = ?     
        "
        );
        $stmt->bind_param('i',$id);
        $stmt->execute();
        $notes = $stmt->get_result();
        if ($notes->num_rows == 0) {
            return false;
        }
        else {
            $results = array();
            while($datas = $notes->fetch_assoc()) {
                array_push($results,$datas);
            }
            return ["notes" => $results];
        }
    }

    /**
     * @param id Identifiant de l'auditeur
     * @param year Année scolaire / mettre l'année la plus basse (Ex : 2020/2021 => 2020)
     * Fonction: getByYearAndAuditeur
     * Description: Retourne la liste des notes d'un auditeur pour l'année scolaire désirée.
     */
    public function getByYearAndAuditeur($id,$year) {
        $dbcontroller = new DBController();
        $mysqli = $dbcontroller->getMySQLIObject();
        $stmt = $mysqli->prepare("
                SELECT *
                FROM notes n, unites u
                WHERE u.ID_UNITE=n.ID_UNITE
                AND n.ID_AUDITEUR = ?
                AND n.ANNEESCO = ?
            "
        );
        $stmt->bind_param('ii',$id,$year);
        $stmt->execute();
        $notes = $stmt->get_result();
        if ($notes->num_rows == 0) {
            return false;
        }
        else {
            $results = array();
            while($datas = $notes->fetch_assoc()) {
                array_push($results,$datas);
            }
            return ["notes" => $results];
        }
    }
}

?>