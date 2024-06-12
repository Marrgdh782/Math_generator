<?php
include_once "config.php";

// Обработка запроса POST /math_stat
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['id']) && isset($_POST['username'])) {
    $id = $_POST['id'];

    $sql = "INSERT INTO math_stat (id, 0_total, 0_right, 0_wrong, 1_total, 1_right, 1_wrong, 2_total, 2_right, 2_wrong, custom_total, custom_right, custom_wrong) 
            VALUES (?, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0) ON DUPLICATE KEY UPDATE 0_total = 0, 0_right = 0, 0_wrong = 0, 1_total = 0, 1_right = 0, 1_wrong = 0, 2_total = 0, 2_right = 0, 2_wrong = 0, custom_total = 0, custom_right = 0, custom_wrong = 0"; 
    $stmt = mysqli_prepare($conn, $sql);
    mysqli_stmt_bind_param($stmt, "i", $id);
    mysqli_stmt_execute($stmt);

    if (mysqli_stmt_affected_rows($stmt) > 0) {
        echo json_encode(['message' => 'Stat created successfully']);
    } else {
        echo json_encode(['error' => mysqli_error($conn)]);
    }
}

// Обработка запроса GET /math_stat/{id}
if ($_SERVER['REQUEST_METHOD'] === 'GET' && isset($_GET['id'])) {
    $id = $_GET['id'];
    $sql = "SELECT * FROM math_stat WHERE id = ?";
    $stmt = mysqli_prepare($conn, $sql);
    mysqli_stmt_bind_param($stmt, "i", $id);
    mysqli_stmt_execute($stmt);
    $result = mysqli_stmt_get_result($stmt);

    if ($result && mysqli_num_rows($result) > 0) {
        $math_stat = mysqli_fetch_assoc($result);
        echo json_encode($math_stat);
    } else {
        echo json_encode(['error' => 'Math stat not found']);
    }
}


mysqli_close($conn);
?>
