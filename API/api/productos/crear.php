<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type");

include_once '../../basedatos/BDTienda.php';
include_once '../../tablas/Producto.php';

$database = new BDTienda();
$conex = $database->dameConexion();
$producto = new Producto($conex);

$datos = json_decode(file_get_contents("php://input"));

if (isset($datos->codigo_barras) && isset($datos->nombre) && 
    isset($datos->precio) && isset($datos->precio_con_iva)) {
    
    $producto->codigo_barras = $datos->codigo_barras;
    $producto->nombre = $datos->nombre;
    $producto->precio = $datos->precio;
    $producto->precio_con_iva = $datos->precio_con_iva;
    $producto->stock = $datos->stock ?? 0;
    $producto->descripcion = $datos->descripcion ?? '';
    $producto->imagenB64 = $datos->imagenB64 ?? '';
    $producto->categoria = $datos->categoria ?? '';

    if ($producto->insertar()) {
        http_response_code(201);
        echo json_encode(array("info" => "Producto creado correctamente."));
    } else {
        http_response_code(503);
        echo json_encode(array("info" => "No se pudo crear el producto."));
    }
} else {
    http_response_code(400);
    echo json_encode(array("info" => "Datos incompletos para crear producto."));
}
?>