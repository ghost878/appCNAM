<?php 

require_once("../APIHandlers/PlanningRestHandler.php");

$view = "";
if (isset($_GET['view'])) {
    $view = $_GET['view'];
}

$planningHandler = new PlanningRestHandler();

switch($view) {
    case "planning":
        if (isset($_GET['id'])) {
            $planningHandler->getPlanning($_GET['id']);
        }
        break;
    case "getByDate":
        if (isset($_GET['date'])) {
            $planningHandler->getPlanningByDate($_GET['date']); 
        } else {
            echo "Vous devez rentrer une date";
        }
        break;
    // route de test
    case "test": 
        $planningHandler->getNextWeek();
        break;
    case "":
        // 404 - Not Found
        break;
}