<?php   
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

// Подключение к базе данных
$conn = mysqli_connect("localhost", "root", "1111", "db_1");

// Проверка подключения
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
} 

// Обработка запроса POST /users
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents('php://input'), true);

    $username = $data['username'];

    $sql = "SELECT email, id FROM math_user WHERE name = '$username'";
    $result = mysqli_query($conn, $sql);

    if ($result && mysqli_num_rows($result) > 0) {
        $row = mysqli_fetch_assoc($result);
        $email = $row['email'];
        $id = $row['id'];  
        $checkSql = "SELECT id FROM math_stat WHERE id = '$id'";
        $checkResult = mysqli_query($conn, $checkSql);
    
        if ($checkResult && mysqli_num_rows($checkResult) > 0) {
        $sql = "INSERT INTO math_stat (id, 0_total, 0_right, 0_wrong, 1_total, 1_right, 1_wrong, 2_total, 2_right, 2_wrong, custom_total, custom_right, custom_wrong) 
                VALUES ('$id', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)";
                
                mysqli_query($conn, $sql);
        }// Отправляем JSON-ответ
        echo json_encode(['email' => $email, 'id' => $id]); 
    } else {
        // Отправляем JSON-ответ
        echo json_encode(['error' => 'User not found']); 
    }
}

mysqli_close($conn);
?>