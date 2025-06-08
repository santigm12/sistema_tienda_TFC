<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type");

include_once '../../basedatos/BDTienda.php';
include_once '../../tablas/Venta.php';

try {
    $database = new BDTienda();
    $conex = $database->dameConexion();
    $venta = new Venta($conex);

    if (isset($_GET['id'])) {
        $venta->id = $_GET['id'];
        $query = "SELECT * FROM ventas WHERE id = ?";
        $stmt = $conex->prepare($query);
        $stmt->bind_param("i", $venta->id);
        $stmt->execute();
        $result = $stmt->get_result();
    }

    else if (isset($_GET['cliente_id'])) {
        $cliente_id = intval($_GET['cliente_id']);
        $result = $venta->leer($cliente_id);
    }

    else {
        http_response_code(400);
        echo json_encode(["info" => "Falta el parámetro cliente_id"]);
        exit;
    }

    if ($result && $result->num_rows > 0) {
        $ventas = array();
        while ($row = $result->fetch_assoc()) {
            $venta_item = array(
                "id" => $row['id'],
                //"cliente_id" => $row['cliente_id'],
                //"empleado_id" => $row['empleado_id'],
                "fecha" => $row['fecha'],
                "total" => $row['total'],
                //"metodo_pago" => $row['metodo_pago'],
                //"tipo_venta" => $row['tipo_venta'],
                "estado" => $row['estado'],
                //"descripcion" => $row['descripcion']
            );
            $ventas[] = $venta_item;
        }
        http_response_code(200);
        echo json_encode($ventas);
    } else {
        http_response_code(404);
        echo json_encode(["info" => "No se encontraron ventas."]);
    }
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(["info" => "Error interno del servidor", "error" => $e->getMessage()]);
}
