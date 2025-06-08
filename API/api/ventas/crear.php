<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type");

include_once '../../basedatos/BDTienda.php';
include_once '../../tablas/Venta.php';

$database = new BDTienda();
$conex = $database->dameConexion();
$venta = new Venta($conex);

$datos = json_decode(file_get_contents("php://input"));

if (isset($datos->cliente_id) && isset($datos->empleado_id) && 
    isset($datos->total) && isset($datos->metodo_pago) && 
    isset($datos->tipo_venta)) {
    
    $venta->cliente_id = $datos->cliente_id;
    $venta->empleado_id = $datos->empleado_id;
    $venta->total = $datos->total;
    $venta->metodo_pago = $datos->metodo_pago;
    $venta->tipo_venta = $datos->tipo_venta;
    $venta->descripcion = $datos->descripcion ?? '';

    $venta_id = $venta->insertar();

    if ($venta_id) {
        http_response_code(201);
        echo json_encode(array(
            "info" => "Venta creada correctamente.",
            "venta_id" => $venta_id
        ));
    } else {
        http_response_code(503);
        echo json_encode(array("info" => "No se pudo crear la venta."));
    }
} else {
    http_response_code(400);
    echo json_encode(array("info" => "Datos incompletos para crear venta."));
}
?>