<?php
class DetalleVenta
{
    private $tabla = "detalle_venta";
    public $id;
    public $venta_id;
    public $producto_id;
    public $cantidad;
    public $precio_unitario;
    public $subtotal;
    private $conn;

    public function __construct($db)
    {
        $this->conn = $db;
    }

    function insertar()
    {
        $stmt = $this->conn->prepare("
            INSERT INTO " . $this->tabla . " 
            (venta_id, producto_id, cantidad, precio_unitario, subtotal)
            VALUES(?,?,?,?,?)");

        $stmt->bind_param("iiidd", 
            $this->venta_id, $this->producto_id, $this->cantidad,
            $this->precio_unitario, $this->subtotal);

        if ($stmt->execute()) {
            return true;
        }
        return false;
    }

    public function eliminarPorVentaId($venta_id)
    {
        $query = "DELETE FROM detalle_venta WHERE venta_id = :venta_id";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(":venta_id", $venta_id);
        
        try {
            return $stmt->execute();
        } catch (PDOException $e) {
            error_log("Error al eliminar detalles: " . $e->getMessage());
            return false;
        }
    }

}
?>