<?php
header('Content-Type: application/json');

$host = 'localhost';
$dbname = 'autoparts_store'; 
$username = 'root';
$password = '';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    
    $stmt = $pdo->query("SELECT id, name, price, description, image_url FROM products");
    $products = $stmt->fetchAll(PDO::FETCH_ASSOC);

    
    $base_url = "";
    foreach ($products as &$product) {
        $product['image_url'] = $base_url . $product['image_url']; // Формируем полный URL для изображения
    }

    
    echo json_encode($products);
} catch (PDOException $e) {
    echo json_encode(['error' => $e->getMessage()]);
}
?>