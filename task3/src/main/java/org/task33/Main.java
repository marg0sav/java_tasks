package org.task33;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args)
    {
        ArrayList<Integer> actions = new ArrayList<>();
        int R = 2500;
        int T = 5;
        int deltaTime = R/100;

        // ОТРИЦАТЕЛЬНОЕ число — это время нажатия для открытия
        // ПОЛОЖИТЕЛЬНОЕ число — это время нажатия для закрытия
        actions.add(15 * deltaTime);// окно и так закрыто
        actions.add(-2 * deltaTime);// кнопка вниз нажата < T секунд и окно открывается полностью
        actions.add(20 * deltaTime);// кнопка вверх нажата > T секунд, когда кнопка отпущена окно перестанет закрываться
        actions.add(20 * deltaTime);// кнопка вверх нажата > T секунд, когда кнопка отпущена окно перестанет закрываться
        actions.add(2 * deltaTime);// кнопка вверх нажата < T секунд и окно закрывается полностью
        actions.add(10 * deltaTime);// окно уже закрыто
        actions.add(-20 * deltaTime);

        Window window = new Window(actions, T, deltaTime);
        Open open = new Open(window);
        Close close = new Close(window);
        new Thread(open).start();
        new Thread(close).start();
    }
}
