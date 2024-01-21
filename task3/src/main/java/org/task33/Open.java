package org.task33;
/**
 * Класс Open представляет собой объект, выполняющий операции открытия окна в многопоточном приложении.
 * Реализует интерфейс Runnable для использования в потоках.
 */
public class Open implements Runnable {
    private final Window window;

    /**
     * Конструктор класса Open.
     *
     * @param window Объект окна, которым будет управлять этот поток открытия.
     */

    Open(Window window) {
        this.window = window;
    }

    /**
     * Метод run() представляет собой основную логику выполнения потока открытия окна.
     * В цикле выполняет операцию открытия окна, пока есть доступные действия в списке.
     * По завершении выводит сообщение о завершении потока.
     */
    public void run() {
        while (!window.actions.isEmpty()) {
            window.startOpen();
        }
        System.out.println("Поток Open завершен");
    }
}