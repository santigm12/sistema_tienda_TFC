<?php
class BDTienda
{
    private $host = '54.173.46.205';
    private $user = 'admin';
    private $password = "WXzU4jdi9wWy";
    private $database = "sistema_tienda";

    public function dameConexion()
    {
        $conn = new mysqli($this->host, $this->user, $this->password, $this->database);
        $conn->set_charset("utf8");
        if ($conn->connect_error) {
            die("Error al conectar con MySQL: " . $conn->connect_error);
        } else {
            return $conn;
        }
    }
}
?>