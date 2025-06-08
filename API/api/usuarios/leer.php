<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type");

include_once '../../basedatos/BDTienda.php';
include_once '../../tablas/Usuario.php';

$database = new BDTienda();
$conex = $database->dameConexion();
$usuario = new Usuario($conex);

$result = $usuario->leer();

if ($result->num_rows > 0) {
    $usuarios = array();
    while ($row = $result->fetch_assoc()) {
        $usuario_item = array(
            "id" => $row['id'],
            "correo" => $row['correo'],
            "rol" => $row['rol'],
            "nombre" => $row['nombre'],
            "apellido" => $row['apellido'],
            "telefono" => $row['telefono'],
            "direccion" => $row['direccion'],
            "fecha_registro" => $row['fecha_registro'],
            "activo" => $row['activo']
        );
        array_push($usuarios, $usuario_item);
    }
    http_response_code(200);
    echo json_encode($usuarios);
} else {
    http_response_code(404);
    echo json_encode(array("info" => "No se encontraron usuarios."));
}
?>