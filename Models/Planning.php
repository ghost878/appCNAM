<?php

require_once("../Controllers/DBController.php");

/**
 * Class Planning
 */
class Planning {

    private const url = "http://qrc.gescicca.net/Planning.aspx?id=mmw7MgOktQ%2bcNzdLFCBR0g%3d%3d&annsco=2020&typepersonne=AUDITEUR";
    private $DOMDocument;

    public function __construct()
    {
        $this->DOMDocument = new DOMDocument();
        $this->DOMDocument->validateOnParse = true;
        $this->DOMDocument->loadHTMLFile(self::url);
    }
    
    /**
     * Fonction: getDaysCurrentWeek
     * Description: Retourne les jours de la semaine actuel sous forme de tableau de chaîne de caractère.
     */
    public function getDaysCurrentWeek() {
        $finder = new DOMXPath($this->DOMDocument);
        $days = $finder->query("//*[contains(@class,'PlanningDay')]");
        $daysString = array();
        foreach($days as $day) {
            array_push($daysString,array("jour" => $day->nodeValue, "number_ue" => $day->childNodes));
        }
        return $daysString;
    }

    /**
     * Fonction: getEventsCurrentWeek
     * Description: Retourne les évènements de la semaine actuel
     */
    public function getEventsCurrentWeek() {
        $finder = new DOMXPath($this->DOMDocument);
        $horaires = $finder->query("//*[contains(@class,'PlanningEvt')]//*[contains(@class,'lblEvtRange')]");
        $unites = $finder->query("//*[contains(@class,'PlanningEvt')]//*[contains(@class,'lblEvtUE')]");
        $events = array();
        $compteur= 0;
        $currentDayOfWeek ="";
        foreach($horaires as $horaire) {
            $codeUE = $unites[$compteur]->nodeValue;
            $fullday = trim($horaire->parentNode->parentNode->parentNode->parentNode->parentNode->childNodes[1]->childNodes[1]->nodeValue);
            $dayOfWeek = explode(" ",$fullday)[0];
            if ($this->isDayOfWeek($dayOfWeek)) {
                $currentDayOfWeek = $dayOfWeek;
            }
            $libelleUE = $this->decodeCodeUe($codeUE);
            array_push($events, array("horaire" => $horaire->nodeValue, "unite" => $codeUE . " " . $libelleUE, "dayOfWeek" => $currentDayOfWeek));
            $compteur++;
        }
        return $events;
    }

    
    /**
     * Fonction: isDayOfWeek
     * Description: Retourne true si la chaine de caractère passé en paramètre correspond à un jour de la semaine (lundi,mardi,mercredi,jeudi,vendredi,samedi,dimanche) - false sinon
     */
    public function isDayOfWeek($str) {
        $upperStr = strtoupper($str);
        switch($upperStr) {
            case "LUNDI":
                return true;
                break; 
            case "MARDI":
                return true;
                break; 
            case "MERCREDI":
                return true;
                break;
            case "JEUDI":
                return true;
                break;
            case "VENDREDI":
                return true;
                break;
            case "SAMEDI":
                return true;
                break;
            case "DIMANCHE":
                return true;
                break;
            default: 
                return false;
                break;
        }
    }


    /**
     * Fonction: encodeJson
     * Description: Fonction d'encodage JSON.
     */
    public function encodeJson($responseData) {
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

    /**
     * Fonction: decodeCodeUe
     * Description: Fonction qui retourne le libellé complet du code de l'UE passé en paramètre.
     */
    public function decodeCodeUe($str) {
        switch($str) {
            case "SLOFIP":
                return "Enseignements en Slovaquie";
            case "UASI02":
                return "Expérience d'apprentissage";
            case "USSI0P":
                return "Administration de bases de données";
            case "USSI0Q":
                return "Business Intelligence";
            case "USSI0R":
                return "Apprentissage et Intelligence artificielle";
            case "USSI0S":
                return "Systèmes d'informations Web";
            case "USSI0T":
                return "Génie logiciel";
            case "USSI0U":
                return "Gestion de projet : méthodes et outils";
            case "USSI0V":
                return "Méthodologie avancée";
            case "USSI0W":
                return "Systèmes d'exploitation avancés et Virtualisation";
            case "USSI0X":
                return "Sécurité et réseaux";
            case "USSI0Y":
                return "Algorithmes pour le Cloud Computing";
            case "USSI10":
                return "Test et validation";
            case "USSI11":
                return "Recherche opérationnelle";
            case "USSI12":
                return "Structures et Organisation de l'entreprise";
            case "USSI13":
                return "Droit commercial";
            case "USSI14":
                return "Finance d'entreprise et comptabilité de gestion";
            case "USSI15":
                return "Situation de communication internationale";
            case "USSI16":
                return "Communication en situation professionnelle";
            case "USSI17":
                return "Gestion d'un service informatique";
            case "USSI18":
                return "Conduite du changement";
            case "USSI19":
                return "Logistique et supply chain";
            case "USSI1A":
                return "Création d'entreprise";
            case "USSI1T":
                return "Développement mobile";
            case "USSI1U":
                return "Management";
            default:
                return "erreur";
        }
    }
    
}


?>