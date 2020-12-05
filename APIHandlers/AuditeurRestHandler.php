<?php 

require_once("../Models/Auditeur.php");
require_once("SimpleRest.php");

class AuditeurRestHandler extends SimpleRest {

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
        foreach($result["auditeurs"] as $auditeur) {
            foreach($auditeur as $col) {
                $col = utf8_encode($col);
            }
        }
        if (strpos($requestContentType,'application/json') !== false) {
            $response = $this->encodeJson($result);
            echo $response;
        }
    }

    /**
     * Fonction: encodeJson
     * Description: Fonction d'encodage JSON.
     */
    function encodeJson($responseData) {
        $jsonResponse = json_encode($responseData,JSON_PRETTY_PRINT );
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