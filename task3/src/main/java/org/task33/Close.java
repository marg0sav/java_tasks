package org.task33;
/**
 * Класс Close представляет собой объект, выполняющий операции закрытия окна в многопоточном приложении.
 * Реализует интерфейс Runnable для использования в потоках.
 */
public class Close implements Runnable {
    private final Window window;

    /**
     * Конструктор класса Close.
     *
     * @param window Объект окна, которым будет управлять этот поток закрытия.
     */
    Close(Window window) {
        this.window = window;
    }

    /**
     * Метод run() представляет собой основную логику выполнения потока закрытия окна.
     * В цикле выполняет операцию закрытия окна, пока есть доступные действия в списке.
     * По завершении выводит сообщение о завершении потока.
     */
    public void run() {
        while (!window.actions.isEmpty()) {
            window.startClose();
        }
        System.out.println("Поток Close завершен");
    }
}