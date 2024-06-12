<?php
// Обработка запроса GET /achievements
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $sql = "SELECT * FROM math_achievements";
    $result = mysqli_query($conn, $sql);

    $achievements = [];
    while ($row = mysqli_fetch_assoc($result)) {
        $achievements[] = $row;
    }

    echo json_encode($achievements);
}

// Обработка запроса GET /achievements/{id}
if ($_SERVER['REQUEST_METHOD'] === 'GET' && isset($_GET['id'])) {
    $id = $_GET['id'];
    $sql = "SELECT * FROM math_achievements WHERE id = $id";
    $result = mysqli_query($conn, $sql);

    if ($result && mysqli_num_rows($result) > 0) {
        $achievement = mysqli_fetch_assoc($result);
        echo json_encode($achievement);
    } else {
        echo json_encode(['error' => 'Achievement not found']);
    }
}

// Обработка запроса POST /achievements
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $name = $_POST['name'];
    $size = $_POST['size'];
    $description = $_POST['description'];

    $sql = "INSERT INTO math_achievements (name, size, description) VALUES ('$name', '$size', '$description')";

    if (mysqli_query($conn, $sql)) {
        echo json_encode(['message' => 'Achievement created successfully']);
    } else {
        echo json_encode(['error' => mysqli_error($conn)]);
    }
}

// Обработка запроса PUT /achievements/{id}
if ($_SERVER['REQUEST_METHOD'] === 'PUT' && isset($_GET['id'])) {
    $id = $_GET['id'];
    $name = $_PUT['name'];
    $size = $_PUT['size'];
    $description = $_PUT['description'];

    $sql = "UPDATE math_achievements SET name = '$name', size = '$size', description = '$description' WHERE id = $id";

    if (mysqli_query($conn, $sql)) {
        echo json_encode(['message' => 'Achievement updated successfully']);
    } else {
        echo json_encode(['error' => mysqli_error($conn)]);
    }
}

// Обработка запроса DELETE /achievements/{id}
if ($_SERVER['REQUEST_METHOD'] === 'DELETE' && isset($_GET['id'])) {
    $id = $_GET['id'];
    $sql = "DELETE FROM math_achievements WHERE id = $id";

    if (mysqli_query($conn, $sql)) {
        echo json_encode(['message' => 'Achievement deleted successfully']);
    } else {
        echo json_encode(['error' => mysqli_error($conn)]);
    }
}

?>
