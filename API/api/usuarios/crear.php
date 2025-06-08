<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type");

include_once '../../basedatos/BDTienda.php';
include_once '../../tablas/Usuario.php';

$database = new BDTienda();
$conex = $database->dameConexion();
$usuario = new Usuario($conex);

$datos = json_decode(file_get_contents("php://input"));

if (isset($datos->correo) && isset($datos->password) && isset($datos->rol) && isset($datos->nombre) && isset($datos->apellido)) {
    
    $usuario->correo = $datos->correo;
    $usuario->password_hash = $datos->password;
    $usuario->rol = $datos->rol;
    $usuario->nombre = $datos->nombre;
    $usuario->apellido = $datos->apellido;
    $usuario->telefono = $datos->telefono ?? null;
    $usuario->direccion = $datos->direccion ?? null;

    if ($usuario->insertar()) {
    // Obtener el ID del usuario recién creado
        $usuario_id = $conex->insert_id;

        // Insertar sesión inicial
        $insertSesion = "INSERT INTO sesiones (usuario_id, dispositivo) VALUES (?, ?)";
        $stmtSesion = $conex->prepare($insertSesion);
        $dispositivo = 'Móvil';
        $stmtSesion->bind_param("is", $usuario_id, $dispositivo);
        $stmtSesion->execute();

        http_response_code(201);
        echo json_encode(array("info" => "Usuario creado correctamente."));
    } else {
        http_response_code(503);
        echo json_encode(array("info" => "No se pudo crear el usuario."));
    }
} else {
    http_response_code(400);
    echo json_encode(array("info" => "Datos incompletos para crear usuario."));
}
?>