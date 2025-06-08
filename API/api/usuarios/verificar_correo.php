<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type");
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

include_once '../../basedatos/BDTienda.php';
include_once '../../tablas/Usuario.php';

// Verificar que se recibió el parámetro 'correo'
if (!isset($_GET['correo'])) {
    echo json_encode(["valido" => false, "mensaje" => "Falta el parámetro 'correo'."]);
    exit;
}

$correo = $_GET['correo'];

// Validar formato del correo
if (!filter_var($correo, FILTER_VALIDATE_EMAIL)) {
    echo json_encode(["valido" => false, "mensaje" => "Formato de correo no válido."]);
    exit;
}

// Verificar que el dominio del correo exista
$dominio = substr(strrchr($correo, "@"), 1);
if (!checkdnsrr($dominio, "MX")) {
    echo json_encode(["valido" => false, "mensaje" => "El dominio del correo no existe o no acepta correos."]);
    exit;
}

// Conectar a la base de datos
$basedatos = new BDTienda();
$conex = $basedatos->dameConexion();

// Crear instancia del modelo Usuario
$usuario = new Usuario($conex);

// *** Solución rápida: asignar propiedad 'conexion' para el método que lo usa ***
$usuario->conexion = $conex;

// Comprobar si el correo ya está registrado
if ($usuario->existeCorreo($correo)) {
    echo json_encode(["valido" => false, "mensaje" => "El correo ya está registrado."]);
    exit;
}

// Todo correcto
echo json_encode(["valido" => true, "mensaje" => "Correo válido."]);
?>
