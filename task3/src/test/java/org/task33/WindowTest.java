package org.task33;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;


class WindowTest{

    @Test
    public void testWindowInitialization() {
        ArrayList<Integer> actions = new ArrayList<>();
        Window window = new Window(actions, 10, 100);

        assertTrue(window.getIsFullyClosed());
        assertFalse(window.getIsFullyOpen());
        assertEquals(0, window.getOpeningPercentage());
        assertEquals(0, window.getElapsedTime());
        assertEquals(0, window.getButtonPressDuration());
        assertEquals(100, window.getTimeInterval());
        assertEquals(10, window.getThresholdTime());
        assertNotNull(window.getActions());
    }

    @Test
    public void testAddOpenedPercent() {
        ArrayList<Integer> actions = new ArrayList<>();
        Window window = new Window(actions, 10, 100);

        // Проверка начального состояния
        assertEquals(0, window.getOpeningPercentage());
        assertEquals(0, window.getElapsedTime());
        assertTrue(window.getIsFullyClosed());
        assertFalse(window.getIsFullyOpen());

        // Вызов метода addOpenedPercent()
        window.addOpenedPercent();

        // Проверка измененного состояния
        assertEquals(1, window.getOpeningPercentage());
        assertEquals(100, window.getElapsedTime());
        assertFalse(window.getIsFullyClosed());
        assertFalse(window.getIsFullyOpen());

        // Дополнительная проверка, что повторный вызов не вызывает ошибок
        window.addOpenedPercent();
        assertEquals(2, window.getOpeningPercentage());
        assertEquals(200, window.getElapsedTime());

        // Дополнительная проверка на открытие на 100%
        for (int i = 0; i < 98; i++) {
            window.addOpenedPercent();
        }
        assertEquals(100, window.getOpeningPercentage());
        assertFalse(window.getIsFullyClosed());
        assertTrue(window.getIsFullyOpen());

        // Попытка вызвать метод после того, как окно открыто
        window.addOpenedPercent();
    }

    @Test
    public void testSubOpenedPercent() {
        ArrayList<Integer> actions = new ArrayList<>();
        Window window = new Window(actions, 10, 100);

        // Устанавливаем открытие окна на 50%
        for (int i = 0; i < 50; i++) {
            window.addOpenedPercent();
        }

        window.subOpenedPercent();

        assertEquals(49, window.getOpeningPercentage()); // Открыто на 49% после уменьшения на 1%
        assertFalse(window.getIsFullyClosed());
        assertFalse(window.getIsFullyOpen());

        // Полностью закрываем
        for (int i = 0; i < 49; i++) {
            window.subOpenedPercent();
        }

        // Проверка на вызов метода, когда окно уже закрыто
        window.subOpenedPercent();
    }

    @Test
    void testCheckWindowStatus() {
        ArrayList<Integer> actions = new ArrayList<>();
        Window window = new Window(actions, 10, 100);

        window.checkWindowStatus();

        assertTrue(window.getIsFullyClosed());
        assertFalse(window.getIsFullyOpen());

        // Проверка промежуточного варианта( не полностью открыто/закрыто)
        window.addOpenedPercent();
        assertFalse(window.getIsFullyClosed());
        assertFalse(window.getIsFullyOpen());

        // Полностью открываем окно
        for (int i = 0; i < 99; i++) {
            window.addOpenedPercent();
        }
        assertFalse(window.getIsFullyClosed());
        assertTrue(window.getIsFullyOpen());

        // Полностью закрываем окно
        for (int i = 0; i < 100; i++) {
            window.subOpenedPercent();
        }
        assertTrue(window.getIsFullyClosed());
        assertFalse(window.getIsFullyOpen());
    }

    @Test
    public void testStartCloseAndStartOpen() throws InterruptedException {
        // Создаем окно для тестирования
        Window window = new Window(new ArrayList<>(), 10, 100);

        // Создаем поток, который будет вызывать startClose
        Thread closeThread = new Thread(window::startClose);

        // Создаем поток, который будет вызывать startOpen
        Thread openThread = new Thread(window::startOpen);

        // Запускаем оба потока
        openThread.start();
        closeThread.start();

        // Ждем, пока оба потока завершат выполнение
        openThread.join();
        closeThread.join();

        assertTrue(window.getIsFullyClosed());
    }

    @Test
    void testWindowFullyOpen() throws InterruptedException{
        ArrayList<Integer> actions = new ArrayList<>();

        int T = 5;
        int deltaTime = 25;

        actions.add(-2 * deltaTime);

        Window window = new Window(actions, T, deltaTime);

        // Создаем и запускаем поток открытия окна
        Thread openThread = new Thread(window::startOpen);
        openThread.start();

        // Ждем завершения потока
            openThread.join();

        // Проверяем, что окно открыто полностью
        assertTrue(window.getIsFullyOpen());
    }

}