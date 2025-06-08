<?php
class Venta
{
    private $tabla = "ventas";
    public $id;
    public $cliente_id;
    public $empleado_id;
    public $total;
    public $metodo_pago;
    public $tipo_venta;
    public $descripcion;
    private $conn;

    public function __construct($db)
    {
        $this->conn = $db;
    }

    function insertar()
    {
        $stmt = $this->conn->prepare("
            INSERT INTO " . $this->tabla . " 
            (cliente_id, empleado_id, fecha, total, metodo_pago, tipo_venta, estado, descripcion)
            VALUES(?,?,NOW(),?,?,?,'EN PROCESO',?)");

        $stmt->bind_param("iidsss", 
            $this->cliente_id, $this->empleado_id, $this->total,
            $this->metodo_pago, $this->tipo_venta, $this->descripcion);

        if ($stmt->execute()) {
            return $this->conn->insert_id;
        }
        return false;
    }


    function leer($cliente_id)
    {
        $query = "SELECT * FROM " . $this->tabla . " WHERE cliente_id = ?";
        $stmt = $this->conn->prepare($query);
        $stmt->bind_param("i", $cliente_id);
        $stmt->execute();
        $result = $stmt->get_result();
        return $result;
    }

     public function eliminar($id) {
        // Cambiado a PDO para consistencia
        $query = "DELETE FROM " . $this->tabla . " WHERE id = :id";
        $stmt = $this->conn->prepare($query);
        $stmt->bindParam(":id", $id);
        
        try {
            return $stmt->execute();
        } catch (PDOException $e) {
            error_log("Error al eliminar venta: " . $e->getMessage());
            return false;
        }
    }

}
?>