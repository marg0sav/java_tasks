package org.task33;

import java.util.ArrayList;

/**
 * Класс Window представляет собой объект, управляющий состоянием и поведением автомобильного окна
 * в многопоточном приложении.
 */
public class Window
{
    // Флаг, указывающий, полностью ли окно открыто.
    private boolean isFullyOpen;
    // Флаг, указывающий, полностью ли окно закрыто.
    private boolean isFullyClosed;
    // Процент открытия окна (от 0 до 100).
    private int openingPercentage;
    // Время, прошедшее с начала открытия или закрытия окна.
    private int elapsedTime;
    // Продолжительность удержания кнопки в момент действия (время нажатия на кнопку).
    private int buttonPressDuration;
    // Время дискретизации для эмуляции времени.
    private final int timeInterval;
    // Порог времени для определения полного открытия или закрытия окна.
    private final int thresholdTime;
    // Список действий для окна.
    public ArrayList<Integer> actions;

    // Геттеры
    public boolean getIsFullyOpen() {
        return isFullyOpen;
    }

    public boolean getIsFullyClosed() {
        return isFullyClosed;
    }

    public int getOpeningPercentage() {
        return openingPercentage;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public int getButtonPressDuration() {
        return buttonPressDuration;
    }

    public int getTimeInterval() {
        return timeInterval;
    }

    public int getThresholdTime() {
        return thresholdTime;
    }

    public ArrayList<Integer> getActions() {
        return actions;
    }

    /**
     * Конструктор класса Window.
     *
     * @param actions       Список действий для окна.
     * @param thresholdTime Порог времени для определения полного открытия или закрытия окна.
     * @param timeInterval  Время дискретизации для эмуляции времени.
     */
    Window(ArrayList<Integer> actions, int thresholdTime, int timeInterval) {

        isFullyClosed = true;
        isFullyOpen = false;

        openingPercentage = 0;
        elapsedTime = 0;
        this.timeInterval = timeInterval;
        buttonPressDuration = 0;

        this.actions = actions;
        this.thresholdTime = thresholdTime;
    }

    /**
     * Метод для увеличения процента открытия окна.
     * Если окно не полностью открыто, увеличивает процент открытия и проверяет текущее состояние окна.
     * Если окно становится полностью открытым, выводит сообщение об этом.
     */
    void addOpenedPercent() {
        if (!isFullyOpen) {

            elapsedTime += timeInterval;
            openingPercentage += 1;
            checkWindowStatus();

            System.out.println(openingPercentage + " открывается");
        } else {
            System.out.println("Это окно уже открыто!");
        }
        if(isFullyOpen) {
            System.out.println("Окно полностью открыто");
        }
    }

    /**
     * Метод для уменьшения процента открытия окна.
     * Если окно не полностью закрыто, уменьшает процент открытия и проверяет текущее состояние окна.
     * Если окно становится полностью закрытым, выводит сообщение об этом.
     */
    void subOpenedPercent() {
        if (!isFullyClosed) {

            elapsedTime += timeInterval;
            openingPercentage -= 1;
            checkWindowStatus();

            System.out.println(openingPercentage + " закрывается");
        } else {
            System.out.println("Это окно уже закрыто!");
        }

        if(isFullyClosed) {
            System.out.println("Окно полностью закрыто");
        }
    }

    /**
     * Метод для проверки текущего состояния окна на основе процента открытия.
     * Обновляет флаги состояния окна в зависимости от процента открытия.
     */
    void checkWindowStatus() {
        isFullyClosed = (openingPercentage == 0);
        isFullyOpen = (openingPercentage == 100);
    }

    /**
     * Метод для начала закрытия окна.
     * Блокирует поток до момента закрытия окна или прерывания потока.
     * При завершении закрытия окна выводит сообщения в консоль.
     */
    synchronized void startClose()
    {
        try
        {
            // Ожидание, пока нет действий или текущее действие - открытие.
            while (!actions.isEmpty() && actions.get(0) < 0)
                wait();
            // Получение продолжительности удержания кнопки и удаление из списка.
            if (!actions.isEmpty()) {

                buttonPressDuration = actions.get(0);
                actions.remove(0);

                // Если продолжительность нажатия меньше порогового значения, закрытие окна полностью.
                if (buttonPressDuration < thresholdTime * timeInterval)
                    buttonPressDuration = 100 * timeInterval;

                // Сброс времени прошедшего с начала действия.
                elapsedTime = 0;

                System.out.println("Start close");

                // Цикл, ожидающий завершения закрытия окна в течение buttonPressDuration.
                while (elapsedTime < buttonPressDuration) {
                    if (isFullyClosed) {

                        System.out.println("Окно полностью закрыто");
                        break;
                    }
                    try {
                        // Ожидание времени, определенного timeInterval, перед проверкой статуса окна.
                        Thread.sleep(timeInterval);
                        subOpenedPercent();
                    } catch (InterruptedException e) {
                        System.out.println("Поток был прерван");
                    }
                }
                System.out.println("End close");
            }
            notify();
        } catch (InterruptedException e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Метод для начала открытия окна. Блокирует поток до момента открытия окна или прерывания потока.
     * При завершении открытия окна выводит сообщения в консоль.
     */
    synchronized void startOpen() {
        try {
            // Ожидание, пока нет действий или текущее действие - закрытие.
            while (!actions.isEmpty() && actions.get(0) > 0)
                wait();

            if (!actions.isEmpty()) {
                // Получение продолжительности удержания кнопки и удаление из списка.
                buttonPressDuration = actions.get(0) * -1;
                actions.remove(0);

                // Если продолжительность нажатия меньше порогового значения, открытие окна полностью.
                if (buttonPressDuration < thresholdTime * timeInterval)
                    buttonPressDuration = 100 * timeInterval;

                // Сброс времени прошедшего с начала действия.
                elapsedTime = 0;

                System.out.println("Start open");

                // Цикл, ожидающий завершения открытия окна в течение buttonPressDuration.
                while (elapsedTime < buttonPressDuration) {
                    if (isFullyOpen) {
                        System.out.println("Окно полностью открыто");
                        break;
                    }
                    try {
                        // Ожидание времени, определенного timeInterval, перед проверкой статуса окна.
                        Thread.sleep(timeInterval);
                        addOpenedPercent();

                    } catch (InterruptedException e) {
                        System.out.println("Поток был прерван");
                    }
                }
                System.out.println("End open");
            }
            notify();
        } catch (InterruptedException e)
        {
            System.out.println(e.getMessage());
        }
    }
}