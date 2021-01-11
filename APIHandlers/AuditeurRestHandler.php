<?php 

require_once("../Models/Auditeur.php");
require_once("SimpleRest.php");

class AuditeurRestHandler extends SimpleRest {

    /**
     * Fonction: getAllAuditeurs
     * Description: Fonction qui récupère toutes la listes des auditeurs enregistré en base et l'encode en format JSON.
     */
    function getAllAuditeurs() {
        $auditeur = new Auditeur();
        $rawData = $auditeur->getAllAuditeurs();
        if (empty($rawData)) {
            $statusCode = 404;
            $rawData = array('error' => 'Aucun auditeur trouve'); 
        } else {
            $statusCode = 200;
        }
        $requestContentType = 'application/json';
        $this->setHttpHeaders($requestContentType,$statusCode);
        $result["auditeurs"] = $rawData;
        if (strpos($requestContentType,'application/json') !== false) {
            $response = $this->encodeJson($result);
            echo $response;
        }
    }


    /**
     * Fonction: doLogin
     * Description: Retourne true si la combinaison identifiant / mot de passe est correct 
     * Si true, retourne également les informations de l'auditeur authentifié.
     */
    function doLogin($username,$password) {
        $auditeur = new Auditeur();
        $exists = $auditeur->checkUserExists($username,$password);
        $enseignements = $auditeur->getEnseignements($exists["auditeur"]['ID_AUDITEUR']);
        $datas = array_merge($exists,$enseignements);
        $requestContentType = 'application/json';
        $statusCode = 200;
        $this->setHttpHeaders($requestContentType,$statusCode);
        if (strpos($requestContentType,'application/json') !== false) {
            $response = $this->encodeJson($datas);
            echo $response;
        }
    }

    function getFilesUnite($unite) {
        $datas = array();
        $dossiers = scandir("../../Moodle/" . $unite );
        foreach ($dossiers as $dos) {
            if($dos != "." &&  $dos != "..") {
                $fichiers = scandir("../../Moodle/" . $unite . "/" . $dos);
                unset($fichiers[array_search(".", $fichiers)]);
                unset($fichiers[array_search("..", $fichiers)]);
                $datas[$dos] = $fichiers;
            }
        }
        $requestContentType = 'application/json';
        //$statusCode = 200;
        //$this->setHttpHeaders($requestContentType,$statusCode);
        if (strpos($requestContentType,'application/json') !== false) {
            $response = $this->encodeJson($datas);
            echo $response;
        }
    }


    /**
     * Fonction: encodeJson
     * Description: Fonction d'encodage JSON.
     */
    function encodeJson($responseData) {
        $jsonResponse = json_encode($responseData,JSON_PRETTY_PRINT);
        switch (json_last_error()) {
            case JSON_ERROR_DEPTH:
                echo ' - Profondeur maximale atteinte' . "\n";
            break;
            case JSON_ERROR_STATE_MISMATCH:
                echo ' - Inadéquation des modes ou underflow' . "\n";
            break;
            case JSON_ERROR_CTRL_CHAR:
                echo ' - Erreur lors du contrôle des caractères' . "\n";
            break;
            case JSON_ERROR_SYNTAX:
                echo ' - Erreur de syntaxe ; JSON malformé' . "\n";
            break;
            case JSON_ERROR_UTF8:
                echo ' - Caractères UTF-8 malformés, probablement une erreur d\'encodage' . "\n";
            break;
        }
        return $jsonResponse;
    }
}

?>