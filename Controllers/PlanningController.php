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
        if (isset($_GET['date']) && isset($_GET['id'])) {
            $planningHandler->getPlanningByDate($_GET['date'],$_GET['id']); 
        } else {
            echo "Paramètre(s) manquant(s)";
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