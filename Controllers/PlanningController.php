<?php 

require_once("../APIHandlers/PlanningRestHandler.php");

$view = "";
if (isset($_GET['view'])) {
    $view = $_GET['view'];
}

$planningHandler = new PlanningRestHandler();

switch($view) {
    case "planning":
        $planningHandler->getPlanning();
        break;
    // route de test
    case "test": 
        $planningHandler->getNextWeek();
        break;
    case "":
        // 404 - Not Found
        break;
}