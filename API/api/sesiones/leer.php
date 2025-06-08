<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type");

include_once '../../basedatos/BDTienda.php';
include_once '../../tablas/Sesion.php';

try {
    $database = new BDTienda();
    $conex = $database->dameConexion();
    $sesion = new Sesion($conex);

    // Verificar si se solicita una sesión específica
    if (isset($_GET['id'])) {
        $sesion->id = $_GET['id'];
        $query = "SELECT * FROM sesiones WHERE id = ?";
        $stmt = $conex->prepare($query);
        $stmt->bind_param("i", $sesion->id);
    } else {
        // Consulta para todas las sesiones
        $query = "SELECT * FROM sesiones";
        $stmt = $conex->prepare($query);
    }

    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        $sesiones = array();
        while ($row = $result->fetch_assoc()) {
            $sesion_item = array(
                "id" => $row['id'],
                "usuario_id" => $row['usuario_id'],
                "dispositivo" => $row['dispositivo'],
                "fecha_inicio" => $row['fecha_inicio'],
                "fecha_ultima_actividad" => $row['fecha_ultima_actividad'],
                "activa" => $row['activa']
            );
            array_push($sesiones, $sesion_item);
        }
        http_response_code(200);
        echo json_encode($sesiones);
    } else {
        http_response_code(404);
        echo json_encode(array("info" => "No se encontraron sesiones."));
    }
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(array(
        "info" => "Error interno del servidor",
        "error" => $e->getMessage()
    ));
}
?>