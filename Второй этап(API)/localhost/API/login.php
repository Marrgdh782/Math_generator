<?php
// Подключение к базе данных
$conn = mysqli_connect("localhost", "root", "1111", "db_1");

// Проверка подключения
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

// Обработка запроса POST /login
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents('php://input'), true);

    $username = $data['username'];
    $password = $data['password'];

    // Запрос к базе данных
    $sql = "SELECT * FROM users WHERE login = '$username' AND password = '$password'";
    $result = mysqli_query($conn, $sql);

    if ($result && mysqli_num_rows($result) > 0) {
        // Отправляем JSON-ответ
        echo json_encode(['message' => 'Login successful']); 
    } else {
        // Отправляем JSON-ответ
        echo json_encode(['error' => 'Invalid username or password']); 
    }
}

mysqli_close($conn);
?>
