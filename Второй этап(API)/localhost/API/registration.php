<?
$conn = mysqli_connect("localhost", "root", "1111", "db_1");

// Проверка подключения
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
} 

// Обработка запроса POST /users
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents('php://input'), true);

    $name = $data['name'];
    $password = $data['password'];
    $email = $data['email'];

    $sql = "INSERT INTO math_user (name, password, email) VALUES ('$name', '$password', '$email')";

    if (mysqli_query($conn, $sql)) {
        echo json_encode(['message' => 'User created successfully']);
    } else {
        echo json_encode(['error' => mysqli_error($conn)]);
    }
}
