<?php 

require_once("../APIHandlers/NoteRestHandler.php");

$view = "";
if (isset($_GET['view'])) {
    $view = $_GET['view'];
}

$noteHandler = new NoteRestHandler();

switch($view) {
    case "getByIdAuditeur":
        if (isset($_GET['id'])) {
            $noteHandler->getCursus($_GET['id']);
        }
        else {
            echo "Paramètre manquant : Identifiant de l'auditeur";
        }
        break;
    case "getByYearAndAuditeur":
        if (isset($_GET['id']) && isset($_GET['year'])) {
            $noteHandler->getCursusByYear($_GET['id'],$_GET['year']);
        }
        else {
            echo "Paramètre manquant : Identifiant de l'auditeur ou l'année scolaire.";
        }
        break;
    // route de test
    case "test": 
        break;
    case "":
        // 404 - Not Found
        break;
}