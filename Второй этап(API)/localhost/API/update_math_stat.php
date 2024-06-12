<?
include_once("config.php");
// Обработка запроса POST /update_math_stat
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['id']) && isset($_POST['field']) && isset($_POST['value'])) {
    $id = $_POST['id'];
    $field = $_POST['field'];
    $value = $_POST['value'];

    // Проверяем, существует ли запись для этого пользователя
    $checkSql = "SELECT id FROM math_stat WHERE id = '$id'";
    $checkResult = mysqli_query($conn, $checkSql);

    if ($checkResult && mysqli_num_rows($checkResult) > 0) {
        // Запись уже существует, обновляем ее
        $sql = "UPDATE math_stat SET $field = $field + $value WHERE id = '$id'";

        if (mysqli_query($conn, $sql)) {
            echo json_encode(['message' => 'Stat updated successfully']);
        } else {
            echo json_encode(['error' => mysqli_error($conn)]);
        }
    } else {
        // Запись не существует, создаем новую
        $sql = "INSERT INTO math_stat (id, $field) VALUES ('$id', $value)";

        if (mysqli_query($conn, $sql)) {
            echo json_encode(['message' => 'Stat created successfully']);
        } else {
            echo json_encode(['error' => mysqli_error($conn)]);
        }
    }
}

mysqli_close($conn);
?>