<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type");

include_once '../../basedatos/BDTienda.php';
include_once '../../tablas/Producto.php';

$database = new BDTienda();
$conex = $database->dameConexion();
$producto = new Producto($conex);

$result = $producto->leer();

if ($result->num_rows > 0) {
    $productos = array();
    while ($row = $result->fetch_assoc()) {
        $producto_item = array(
            "id" => $row['id'],
            "nombre" => $row['nombre'],
            "precio_con_iva" => $row['precio_con_iva'],
            "descripcion" => $row['descripcion'],
            "imagenB64" => $row['imagenB64']
        );
        array_push($productos, $producto_item);
    }
    http_response_code(200);
    echo json_encode($productos);
} else {
    http_response_code(404);
    echo json_encode(array("info" => "No se encontraron productos."));
}
?>
