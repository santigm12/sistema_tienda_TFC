<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type");

include_once '../../basedatos/BDTienda.php';
include_once '../../tablas/DetalleVenta.php';

$database = new BDTienda();
$conex = $database->dameConexion();
$detalle = new DetalleVenta($conex);

$datos = json_decode(file_get_contents("php://input"));

if (isset($datos->venta_id) && isset($datos->producto_id) && 
    isset($datos->cantidad) && isset($datos->precio_unitario) && 
    isset($datos->subtotal)) {
    
    $detalle->venta_id = $datos->venta_id;
    $detalle->producto_id = $datos->producto_id;
    $detalle->cantidad = $datos->cantidad;
    $detalle->precio_unitario = $datos->precio_unitario;
    $detalle->subtotal = $datos->subtotal;

    if ($detalle->insertar()) {
        http_response_code(201);
        echo json_encode(array("info" => "Detalle de venta creado correctamente."));
    } else {
        http_response_code(503);
        echo json_encode(array("info" => "No se pudo crear el detalle de venta."));
    }
} else {
    http_response_code(400);
    echo json_encode(array("info" => "Datos incompletos para crear detalle de venta."));
}
?>