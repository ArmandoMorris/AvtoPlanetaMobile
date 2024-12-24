<?php
header("Content-Type: application/json; charset=UTF-8");

$host = "localhost"; 
$username = "root";  
$password = "";      
$database = "autoparts_store"; 

$conn = new mysqli($host, $username, $password, $database);

if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Ошибка подключения к базе данных: " . $conn->connect_error]);
    exit;
}

$data = json_decode(file_get_contents("php://input"), true);
$id = isset($data['id']) ? $data['id'] : (isset($_GET['id']) ? $_GET['id'] : null);

if ($id) {
    $id = $conn->real_escape_string($id);

    // Замените 'id' на реальное имя столбца в вашей базе данных
    $query = "DELETE FROM products WHERE id = '$id'";
    if ($conn->query($query) === TRUE) {
        echo json_encode(["status" => "success", "message" => "Продукт успешно удален"]);
    } else {
        echo json_encode(["status" => "error", "message" => "Ошибка при удалении продукта: " . $conn->error]);
    }
} else {
    echo json_encode(["status" => "error", "message" => "ID продукта не передан"]);
}

$conn->close();
?>
