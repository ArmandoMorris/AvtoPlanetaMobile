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
if (!isset($data['username'], $data['password'])) {
    echo json_encode(["status" => "failure", "message" => "Некорректные данные"]);
    exit();
}

$username = $data['username'];
$password = $data['password'];

// Проверка данных в базе
$sql = "SELECT id, username, email FROM users WHERE username = ? AND password = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ss", $username, $password);
$stmt->execute();
$result = $stmt->get_result();

// Если пользователь найден
if ($result->num_rows > 0) {
    $user = $result->fetch_assoc();
    echo json_encode([
        "status" => "success",
        "user_id" => $user['id'],
        "username" => $user['username'],
        "email" => $user['email']
    ]);
} else {
    // Если пользователь не найден
    echo json_encode(["status" => "failure", "message" => "Неверный логин или пароль"]);
}

// Закрытие соединения
$stmt->close();
$conn->close();
?>