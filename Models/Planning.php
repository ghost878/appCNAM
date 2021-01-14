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
     * Fonction: getEventsNextWeek
     * Description: Retourne les évènements de la semaine prochaine
     */
    public function getEventsNextWeek() {
        $finder = new DOMXPath($this->DOMDocument);
        $_VIEWSTATE = $finder->query("//input[@name='__VIEWSTATE']")->item(0)->getAttribute('value');
        var_dump($_VIEWSTATE);
        $postdata = http_build_query(
            array(
                '__VIEWSTATE' => $_VIEWSTATE,
                'ct100$MainContent$btnNavNext.x' => '------WebKitFormBoundary7MA4YWxkTrZu0gW',
                'ctl00$MainContent$btnNavNext.y' => '------WebKitFormBoundary7MA4YWxkTrZu0gW--'
            )
        );
        $options = array('https' =>
            array(
                'method' => 'POST',
                'header' => 'Content-type: application/x-www-form-urlencoded',
                'content' => $postdata
            ) 
        );
        $context = stream_context_create($options);
        $result = file_get_contents(self::url,false,$context);
        var_dump($result);
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