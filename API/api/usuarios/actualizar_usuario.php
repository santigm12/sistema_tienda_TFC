<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: PUT, POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type");

include_once '../../basedatos/BDTienda.php';
include_once '../../tablas/Usuario.php';

try {
    $database = new BDTienda();
    $conex = $database->dameConexion();

    $datos = json_decode(file_get_contents("php://input"));

    if (
        isset($datos->id) &&
        isset($datos->nombre) &&
        isset($datos->apellido) &&
        isset($datos->telefono) &&
        isset($datos->direccion) &&
        isset($datos->correo)
    ) {
        if (isset($datos->password) && !empty($datos->password)) {
            // Si se envía nueva contraseña, la actualizamos
            $passwordHash = password_hash($datos->password, PASSWORD_DEFAULT);

            $query = "UPDATE usuarios SET 
                        nombre = ?, 
                        apellido = ?, 
                        telefono = ?, 
                        direccion = ?, 
                        correo = ?, 
                        password_hash = ?
                      WHERE id = ?";
            $stmt = $conex->prepare($query);
            $stmt->bind_param(
                "ssssssi",
                $datos->nombre,
                $datos->apellido,
                $datos->telefono,
                $datos->direccion,
                $datos->correo,
                $passwordHash,
                $datos->id
            );
        } else {
            // Si no hay contraseña, no se actualiza
            $query = "UPDATE usuarios SET 
                        nombre = ?, 
                        apellido = ?, 
                        telefono = ?, 
                        direccion = ?, 
                        correo = ?
                      WHERE id = ?";
            $stmt = $conex->prepare($query);
            $stmt->bind_param(
                "sssssi",
                $datos->nombre,
                $datos->apellido,
                $datos->telefono,
                $datos->direccion,
                $datos->correo,
                $datos->id
            );
        }

        if ($stmt->execute()) {
            http_response_code(200);
            echo json_encode(array("actualizado" => true, "mensaje" => "Usuario actualizado correctamente."));
        } else {
            http_response_code(500);
            echo json_encode(array("actualizado" => false, "mensaje" => "Error al actualizar el usuario."));
        }
    } else {
        http_response_code(400);
        echo json_encode(array("actualizado" => false, "mensaje" => "Datos incompletos."));
    }
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(array(
        "actualizado" => false,
        "mensaje" => "Error interno del servidor",
        "error" => $e->getMessage()
    ));
}
?>
