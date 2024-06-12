<?
include_once ("config.php");
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['username'])) {
    $username = $_POST['username'];

    $sql = "SELECT solves FROM math_stats WHERE user_id = '$username'";
    $result = mysqli_query($conn, $sql);

    if ($result && mysqli_num_rows($result) > 0) {
        $row = mysqli_fetch_assoc($result);
        echo json_encode(['solves' => $row['solves']]);
    } else {
        echo json_encode(['error' => 'User not found']);
    }
}

mysqli_close($conn);
?>