<?php
// Обработка запроса GET /achievements_stat
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $sql = "SELECT * FROM math_achievements_stat";
    $result = mysqli_query($conn, $sql);

    $achievements_stat = [];
    while ($row = mysqli_fetch_assoc($result)) {
        $achievements_stat[] = $row;
    }

    echo json_encode($achievements_stat);
}

// Обработка запроса GET /achievements_stat/{id}
if ($_SERVER['REQUEST_METHOD'] === 'GET' && isset($_GET['id'])) {
    $id = $_GET['id'];
    $sql = "SELECT * FROM math_achievements_stat WHERE id = $id";
    $result = mysqli_query($conn, $sql);

    if ($result && mysqli_num_rows($result) > 0) {
        $achievement_stat = mysqli_fetch_assoc($result);
        echo json_encode($achievement_stat);
    } else {
        echo json_encode(['error' => 'Achievement stat not found']);
    }
}

// Обработка запроса POST /achievements_stat
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $light = $_POST['light'];
    $hard = $_POST['hard'];

    $sql = "INSERT INTO math_achievements_stat (light, hard) VALUES ('$light', '$hard')";

    if (mysqli_query($conn, $sql)) {
        echo json_encode(['message' => 'Achievement stat created successfully']);
    } else {
        echo json_encode(['error' => mysqli_error($conn)]);
    }
}

// Обработка запроса PUT /achievements_stat/{id}
if ($_SERVER['REQUEST_METHOD'] === 'PUT' && isset($_GET['id'])) {
    $id = $_GET['id'];
    $light = $_PUT['light'];
    $hard = $_PUT['hard'];

    $sql = "UPDATE math_achievements_stat SET light = '$light', hard = '$hard' WHERE id = $id";

    if (mysqli_query($conn, $sql)) {
        echo json_encode(['message' => 'Achievement stat updated successfully']);
    } else {
        echo json_encode(['error' => mysqli_error($conn)]);
    }
}

// Обработка запроса DELETE /achievements_stat/{id}
if ($_SERVER['REQUEST_METHOD'] === 'DELETE' && isset($_GET['id'])) {
    $id = $_GET['id'];
    $sql = "DELETE FROM math_achievements_stat WHERE id = $id";

    if (mysqli_query($conn, $sql)) {
        echo json_encode(['message' => 'Achievement stat deleted successfully']);
    } else {
        echo json_encode(['error' => mysqli_error($conn)]);
    }
}

?>