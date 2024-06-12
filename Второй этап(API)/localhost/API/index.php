<?php
require_once 'config.php';

// Получение URL-адреса запроса
$uri = $_SERVER['REQUEST_URI'];

// Обработка запросов
if ($uri == '/users') {
    require_once 'users.php'; // Обработка пользователей
} else if ($uri == '/achievements') {
    require_once 'achievements.php'; // Обработка достижений
} else if ($uri == '/achievements_stat') {
    require_once 'achievements_stat.php'; // Обработка статистики достижений
} else if ($uri == '/tasks') {
    require_once 'tasks.php'; // Обработка задач
} else if ($uri == '/math_stat') {
    require_once 'math_stat.php'; // Обработка статистики
} else if ($uri == '/login.php') {
    require_once 'achievements.php'; // Обработка достижений
} else {
    echo json_encode(['error' => 'Invalid endpoint']); 
}

?>
