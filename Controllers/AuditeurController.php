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
        if (isset($_POST['login']) && isset($_POST['password'])) {
            $auditeurHandler->doLogin($_POST['login'],$_POST['password']);
        }
        else {
            echo false;
        }
        break;
    case "files_unite" :
        if (isset($_GET['unite'])) {
            $unite = $_GET['unite'];
        }
        $auditeurHandler = new AuditeurRestHandler();
        $auditeurHandler->getFilesUnite($unite);
        break;
    case "":
        // 404 - Not Found
        break;
}