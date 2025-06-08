<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    http_response_code(405);
    echo json_encode(array("error" => "Método no permitido"));
    exit;
}

require_once '../../basedatos/BDTienda.php';
require_once '../../tablas/DetalleVenta.php';

try {
    $database = new BDTienda();
    $conex = $database->dameConexion();

    if (!isset($_GET['venta_id'])) {
        http_response_code(400);
        echo json_encode(array("error" => "Se requiere venta_id"));
        exit;
    }

    $venta_id = (int)$_GET['venta_id'];
    $query = "SELECT * FROM detalle_venta WHERE venta_id = ?";
    $stmt = $conex->prepare($query);

    if (!$stmt) {
        throw new Exception("Error en la preparación de la consulta: " . $conex->error);
    }

    $stmt->bind_param("i", $venta_id);

    if (!$stmt->execute()) {
        throw new Exception("Error al ejecutar la consulta: " . $stmt->error);
    }

    $result = $stmt->get_result();
    $detalles = array();

    while ($row = $result->fetch_assoc()) {
        $detalles[] = array(
            "venta_id" => $row['venta_id'],
            "producto_id" => $row['producto_id'],
            "cantidad" => $row['cantidad'],
            "precio_unitario" => $row['precio_unitario'],
            "subtotal" => $row['subtotal']
        );
    }

    // Siempre devolver array, aunque esté vacío
    http_response_code(200);
    echo json_encode($detalles);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(array(
        "error" => "Error interno del servidor",
        "message" => $e->getMessage()
    ));
}
?>
