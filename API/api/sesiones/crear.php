<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type");

include_once '../../basedatos/BDTienda.php';
include_once '../../tablas/Sesion.php';

$database = new BDTienda();
$conex = $database->dameConexion();
$sesion = new Sesion($conex);

$datos = json_decode(file_get_contents("php://input"));

if (isset($datos->usuario_id) && isset($datos->dispositivo)) {
    $sesion->usuario_id = $datos->usuario_id;
    $sesion->dispositivo = $datos->dispositivo;

    if ($sesion->insertar()) {
        http_response_code(201);
        echo json_encode(array("info" => "Sesión creada correctamente."));
    } else {
        http_response_code(503);
        echo json_encode(array("info" => "No se pudo crear la sesión."));
    }
} else {
    http_response_code(400);
    echo json_encode(array("info" => "Datos incompletos para crear sesión."));
}
?>