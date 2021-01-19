<?php 

require_once("../Models/Planning.php");
require_once("SimpleRest.php");


class PlanningRestHandler extends SimpleRest {


    /**
     * Fonction: getPlanning
     * Description: Retourne le planning 
     */
    public function getPlanning($id) {
        $planning = new Planning();
        $events = $planning->getEventsCurrentWeek($id);
        if (empty($events)) {
            $statusCode = 404;
            $events = array('error' => "Le planning n'a pas pu être chargé");
        } else {
            $statusCode = 200;
        }
        $requestContentType = 'application/json';
        $this->setHttpHeaders($requestContentType,$statusCode);
        if (strpos($requestContentType,'application/json') !== false) {
            $response = $this->encodeJson($events);
            echo $response;
        }
    }

    /**
     * Fonction: getNextWeek
     * Description: Retourne la semaine suivante de la semaine courante
     */
    public function getNextWeek() {
        $planning = new Planning();
        $planning->getEventsNextWeek();
    }

    /**
     * Fonction: getPlanningByDate
     * Description: Retourne le planning de la date indiqué au format dd/mm/Y
     */
    public function getPlanningByDate($date,$id) {
        $planning = new Planning();
        $events = $planning->getByDate($date,$id);
        if (empty($events)) {
            $statusCode = 404;
            $events = array("error" => "Le planning n'a pas pu être chargé ou il n'y a pas de données pour la date ou la formation demandé.");
        }
        else {
            $statusCode = 200;
        }
        $requestContentType = 'application/json';
        $this->setHttpHeaders($requestContentType,$statusCode);
        if (strpos($requestContentType,'application/json') !== false) {
            $response = $this->encodeJson($events);
            echo $response;
        }
    }
}