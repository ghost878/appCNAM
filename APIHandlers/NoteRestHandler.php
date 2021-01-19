<?php

require_once("SimpleRest.php");
require_once('../Models/Note.php');

/**
 * Class Note
 */
class NoteRestHandler extends SimpleRest {

    /**
     * @param id Identifiant de l'auditeur 
     * Fonction: getAllCursus
     * Description: Retourne les notes toutes année confondu d'un auditeur
     */
    public function getCursus($id) {
        $note = new Note();
        $notes = $note->getByIdAuditeur($id);
        if (empty($notes)) {
            $statusCode = 404;
            $notes = array("error" => "Les notes n'ont pas pu être chargées ou il n'y a pas de données pour l'auditeur demandé.");
        }
        else {
            $statusCode = 200;
            $requestContentType = 'application/json';
            $this->setHttpHeaders($requestContentType,$statusCode);
            if (strpos($requestContentType,'application/json') !== false) {
                $response = $this->encodeJson($notes);
                echo $response;
            }
        }
    } 

    public function getCursusByYear($id,$year) {
        $note = new Note();
        $notes = $note->getByYearAndAuditeur($id,$year);
        if (empty($notes)) {
            $statusCode = 404;
            $notes = array("error" => "Les notes n'ont pas pu être chargées ou il n'y a pas de données pour l'auditeur et l'année demandée.");
        }
        else {
            $statusCode = 200;
            $requestContentType = 'application/json';
            $this->setHttpHeaders($requestContentType,$statusCode);
            if (strpos($requestContentType,'application/json') !== false) {
                $response = $this->encodeJson($notes);
                echo $response;
            }
        }
    }
}

?>