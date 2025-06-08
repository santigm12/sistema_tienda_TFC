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

if (isset($datos->correo) && isset($datos->password)) {
    $query = "SELECT id, correo, password_hash, rol, nombre, apellido, direccion, telefono FROM usuarios WHERE correo = ? AND activo = 1";
    $stmt = $conex->prepare($query);
    $stmt->bind_param("s", $datos->correo);
    $stmt->execute();
    $result = $stmt->get_result();
    
    if ($result->num_rows == 1) {
        $row = $result->fetch_assoc();
        if (password_verify($datos->password, $row['password_hash'])) {
            // Actualizar la sesión existente con nueva fecha_ultima_actividad
            $actualizarSesion = "UPDATE sesiones 
                                SET fecha_ultima_actividad = CURRENT_TIMESTAMP 
                                WHERE usuario_id = ? AND activa = 1";
            $stmtActualizar = $conex->prepare($actualizarSesion);
            $stmtActualizar->bind_param("i", $row['id']);
            $stmtActualizar->execute();
            // Credenciales correctas
            http_response_code(200);
            echo json_encode(array(
                "autenticado" => true,
                "mensaje" => "Login exitoso",
                "usuario" => array(
                    "id" => $row['id'],
                    "correo" => $row['correo'],
                    "password_hash" => $row['password_hash'],
                    "rol" => $row['rol'],
                    "nombre" => $row['nombre'],
                    "apellido" => $row['apellido'],
                    "direccion" => $row['direccion'],
                    "telefono" => $row['telefono']
                )
            ));
        } else {
            // Contraseña incorrecta
            http_response_code(401);
            echo json_encode(array("autenticado" => false, "mensaje" => "Credenciales incorrectas"));
        }
    } else {
        // Usuario no encontrado
        http_response_code(404);
        echo json_encode(array("autenticado" => false, "mensaje" => "Usuario no encontrado"));
    }
} else {
    http_response_code(400);
    echo json_encode(array("autenticado" => false, "mensaje" => "Datos incompletos"));
}
?>