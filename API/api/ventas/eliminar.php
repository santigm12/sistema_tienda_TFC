<?php
// eliminar.php - Versión con MySQLi

// Configuración de headers
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: DELETE");
header("Access-Control-Max-Age: 3600");

// Incluir la clase de conexión
require_once '../../basedatos/BDTienda.php';

// Obtener el ID de la venta de la URL
$venta_id = isset($_GET['id']) ? (int)$_GET['id'] : null;

// Validar el ID
if (!$venta_id || $venta_id <= 0) {
    http_response_code(400); // Bad Request
    echo json_encode(["success" => false, "error" => "ID de venta inválido"]);
    exit;
}

// Crear instancia de la base de datos
$database = new BDTienda();
$conn = $database->dameConexion();

// Verificar conexión
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(["success" => false, "error" => "Error de conexión a la base de datos"]);
    exit;
}

try {
    // Iniciar transacción
    $conn->autocommit(false);
    
    // 1. Primero eliminar los detalles de venta
    $query_detalles = "DELETE FROM detalle_venta WHERE venta_id = ?";
    $stmt_detalles = $conn->prepare($query_detalles);
    $stmt_detalles->bind_param("i", $venta_id);
    
    if (!$stmt_detalles->execute()) {
        throw new Exception("Error al eliminar detalles de venta: " . $conn->error);
    }
    
    // 2. Luego eliminar la venta principal
    $query_venta = "DELETE FROM ventas WHERE id = ?";
    $stmt_venta = $conn->prepare($query_venta);
    $stmt_venta->bind_param("i", $venta_id);
    
    if (!$stmt_venta->execute()) {
        throw new Exception("Error al eliminar la venta: " . $conn->error);
    }
    
    // Verificar si se afectó alguna fila
    if ($stmt_venta->affected_rows === 0) {
        throw new Exception("No se encontró la venta con ID: $venta_id");
    }
    
    // Confirmar transacción
    $conn->commit();
    
    // Respuesta exitosa
    http_response_code(200);
    echo json_encode([
        "success" => true,
        "message" => "Venta eliminada correctamente",
        "venta_id" => $venta_id
    ]);
    
} catch (Exception $e) {
    // Revertir transacción en caso de error
    $conn->rollback();
    
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "error" => $e->getMessage()
    ]);
    
} finally {
    // Cerrar conexiones
    if (isset($stmt_detalles)) $stmt_detalles->close();
    if (isset($stmt_venta)) $stmt_venta->close();
    $conn->close();
}