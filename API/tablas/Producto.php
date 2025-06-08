<?php
class Producto
{
    private $tabla = "productos";
    public $id;
    public $codigo_barras;
    public $nombre;
    public $precio;
    public $precio_con_iva;
    public $stock;
    public $descripcion;
    public $imagenB64;
    public $categoria;
    private $conn;

    public function __construct($db)
    {
        $this->conn = $db;
    }

    function insertar()
    {
        $stmt = $this->conn->prepare("
            INSERT INTO " . $this->tabla . " 
            (codigo_barras, nombre, precio, precio_con_iva, stock, descripcion, imagenB64, categoria, fecha_creacion)
            VALUES(?,?,?,?,?,?,?,?,NOW())");

        $stmt->bind_param("ssddssss", 
            $this->codigo_barras, $this->nombre, $this->precio, $this->precio_con_iva,
            $this->stock, $this->descripcion, $this->imagenB64, $this->categoria);

        if ($stmt->execute()) {
            return true;
        }
        return false;
    }

    function leer()
    {
        $query = "SELECT * FROM " . $this->tabla . " WHERE stock > 0";
        $result = $this->conn->query($query);
        return $result;
    }

}
?>