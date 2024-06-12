<? include_once("config.php");
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents('php://input'), true);
    $taskId = $data['taskId'];
    $answer = $data['answer'];
    $hard = $data['hard']; // Получаем уровень сложности из тела запроса

    $sql = "SELECT solution FROM math_tasks WHERE id = $taskId";
    $result = mysqli_query($conn, $sql);

    if ($result && mysqli_num_rows($result) > 0) {
        $row = mysqli_fetch_assoc($result);
        $correctSolution = $row['solution'];

        if ($answer === $correctSolution) {
            echo json_encode(['result' => 'correct']);

            // Обновляем статистику в таблице math_stat
            $sql = "INSERT INTO math_stat (user_id, hard, correct) VALUES (?, ?, 1) 
                    ON DUPLICATE KEY UPDATE correct = correct + 1"; 
            $stmt = mysqli_prepare($conn, $sql);
            mysqli_stmt_bind_param($stmt, "isi", $userId, $hard, 1); // Замените $userId на идентификатор пользователя
            mysqli_stmt_execute($stmt);
        } else {
            echo json_encode(['result' => 'incorrect']);

            // Обновляем статистику в таблице math_stat
            $sql = "INSERT INTO math_stat (user_id, hard, incorrect) VALUES (?, ?, 1) 
                    ON DUPLICATE KEY UPDATE incorrect = incorrect + 1";
            $stmt = mysqli_prepare($conn, $sql);
            mysqli_stmt_bind_param($stmt, "isi", $userId, $hard, 1); // Замените $userId на идентификатор пользователя
            mysqli_stmt_execute($stmt);
        }
    } else {
        echo json_encode(['error' => 'Task not found']);
    }
}
?>