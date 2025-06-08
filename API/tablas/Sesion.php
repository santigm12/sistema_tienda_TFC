<?php
class Sesion
{
    private $tabla = "sesiones";
    public $id;
    public $usuario_id;
    public $dispositivo;
    private $conn;

    public function __construct($db)
    {
        $this->conn = $db;
    }

    function insertar()
    {
        $stmt = $this->conn->prepare("
            INSERT INTO " . $this->tabla . " 
            (usuario_id, dispositivo, fecha_inicio, fecha_ultima_actividad, activa)
            VALUES(?,?,NOW(),NOW(),1)");

        $stmt->bind_param("is", $this->usuario_id, $this->dispositivo);

        if ($stmt->execute()) {
            return true;
        }
        return false;
    }
}
?>