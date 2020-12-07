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
    case "doLogin":
        $auditeurHandler = new AuditeurRestHandler();
        if (isset($_GET['login']) && isset($_GET['password'])) {
            $auditeurHandler->doLogin($_GET['login'],$_GET['password']);
        }
        else {
            echo false;
        }
        break;
    case "":
        // 404 - Not Found
        break;
}