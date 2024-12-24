<?php
header('Content-Type: application/json');

// Настройки подключения к базе данных
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "autoparts_store";

// Создание соединения с базой данных
$conn = new mysqli($servername, $username, $password, $dbname);

// Проверка соединения
if ($conn->connect_error) {
    die(json_encode(["status" => "failure", "message" => "Connection failed: " . $conn->connect_error]));
}

// Получение данных из POST-запроса
$data = json_decode(file_get_contents("php://input"), true);

// Проверка на наличие необходимых параметров
if (!isset($data['user_id'], $data['email'])) {
    echo json_encode(["status" => "failure", "message" => "Некорректные данные"]);
    exit();
}

$user_id = $data['user_id'];
$email = $data['email'];

// Обновление email пользователя в базе данных
$sql = "UPDATE users SET email = ? WHERE id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("si", $email, $user_id);
$stmt->execute();

// Проверка на успешное обновление
if ($stmt->affected_rows > 0) {
    echo json_encode(["status" => "success", "message" => "Email обновлен успешно"]);
} else {
    echo json_encode(["status" => "failure", "message" => "Не удалось обновить email"]);
}

// Закрытие соединения
$stmt->close();
$conn->close();
?>