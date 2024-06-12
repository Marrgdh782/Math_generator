<?
include_once("config.php");
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Получаем случайный уровень сложности
    $hardLevels = ['0', '1', '2'];
    $randomHard = $hardLevels[array_rand($hardLevels)];

    $sql = "SELECT id, task, solution FROM math_tasks WHERE hard = '$randomHard' ORDER BY RAND() LIMIT 1";

    $result = mysqli_query($conn, $sql);

    if ($result && mysqli_num_rows($result) > 0) {
        $row = mysqli_fetch_assoc($result);
        $task = [
            'id' => $row['id'],
            'task' => $row['task'],
            'solution' => $row['solution'],
            'hard' => $randomHard // Отправляем уровень сложности в ответе
        ];
        echo json_encode($task);
    } else {
        echo json_encode(['error' => 'Task not found']);
    }
}
?>