package ru.spbstu.telematics.java;

/**
 * Hello world!
 *
 */
/*public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
}*/
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        String fileName = "matrix.txt"; // Имя файла с матрицей
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            List<String> lines = new ArrayList<>();

            // Чтение строк из файла
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            // Проверка на пустой файл
            if (lines.isEmpty()) {
                System.out.println("Файл со значениями исходной матрицы пустой");
                return;
            }

            // Определение размеров матрицы
            int rows = lines.size();
            int cols = lines.get(0).trim().split("\\s+").length;

            // Создание и заполнение матрицы
            int[][] matrix = new int[rows][cols];
            for (int row = 0; row < rows; row++) {
                String[] values = lines.get(row).trim().split("\\s+");
                if (values.length != cols) {
                    System.out.println("Некорректное количество элементов в строке " + (row + 1));
                    return;
                }
                for (int col = 0; col < cols; col++) {
                    try {
                        matrix[row][col] = Integer.parseInt(values[col]);
                    } catch (NumberFormatException e) {
                        System.out.println("Некорректное значение в строке " + (row + 1) + ", столбце " + (col + 1));
                        return;
                    }
                }
            }

            // Транспонирование матрицы и вывод на экран
            int[][] transposedMatrix = transposeMatrix(matrix);
            System.out.println("Исходная матрица:");
            printMatrix(matrix);
            System.out.println("Транспонированная матрица:");
            printMatrix(transposedMatrix);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Метод для транспонирования матрицы
    public static int[][] transposeMatrix(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] transposedMatrix = new int[cols][rows];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                transposedMatrix[col][row] = matrix[row][col];
            }
        }

        return transposedMatrix;
    }

    // Метод для вывода матрицы на экран
    public static void printMatrix(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        if (rows == 0 || cols == 0) {
            System.out.println("Матрица пустая");
            return;
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                System.out.print(matrix[row][col] + " ");
            }
            System.out.println();
        }
    }


}
