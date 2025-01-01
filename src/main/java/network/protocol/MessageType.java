package network.protocol;

public enum MessageType {
    CONNECT,      // Подключение нового клиента
    DISCONNECT,   // Отключение клиента
    PLAYER_POSITION, // Обновление позиции игрока
    PLAYER_DISCONNECT,
    BULLET_SHOT,  // Добавляем новый тип
}