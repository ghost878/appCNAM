<?php 

require_once("../APIHandlers/AuditeurRestHandler.php");

$view = "";
if (isset($_GET['view'])) {
    $view = $_GET['view'];
}

switch($view) {
    case "all":
        $auditeurHandler = new AuditeurRestHandler();
        $auditeurHandler->getAllAuditeurs();
        break;
    case "":
        // 404 - not found
        break;
}