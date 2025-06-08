<?php
class Usuario
{
    private $tabla = "usuarios";
    public $id;
    public $correo;
    public $password_hash;
    public $rol;
    public $nombre;
    public $apellido;
    public $telefono;
    public $direccion;
    private $conn;

    public function __construct($db)
    {
        $this->conn = $db;
    }

    function insertar()
    {
        $stmt = $this->conn->prepare("
            INSERT INTO " . $this->tabla . " 
            (correo, password_hash, rol, nombre, apellido, telefono, direccion, fecha_registro, activo)
            VALUES(?,?,?,?,?,?,?,CURDATE(),1)");

        $this->password_hash = password_hash($this->password_hash, PASSWORD_BCRYPT);

        $stmt->bind_param("sssssss", 
            $this->correo, $this->password_hash, $this->rol,
            $this->nombre, $this->apellido, $this->telefono, $this->direccion);

        if ($stmt->execute()) {
            return true;
        }
        return false;
    }

    function leer()
    {
        $query = "SELECT id, correo, rol, nombre, apellido, telefono, direccion, fecha_registro, activo 
                 FROM " . $this->tabla;
        $result = $this->conn->query($query);
        return $result;
    }

    public function existeCorreo($correo) {
        $correo = mysqli_real_escape_string($this->conexion, $correo);
        $sql = "SELECT id FROM usuarios WHERE correo = '$correo' LIMIT 1";
        $resultado = $this->conexion->query($sql);
        return $resultado && $resultado->num_rows > 0;
    }

}
?>