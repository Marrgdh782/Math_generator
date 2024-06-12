<?php
$servername = "localhost";
$username = "root";
$password = "1111";
$dbname = "db_1";

// Создание подключения
$conn = mysqli_connect($servername, $username, $password, $dbname);

// Проверка подключения
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

