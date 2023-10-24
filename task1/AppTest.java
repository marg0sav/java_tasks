package ru.spbstu.telematics.java;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppTest {
    @Test
    public void testTransposeMatrixFromFile() {
        String fileName = "matrixForCheck.txt";

        App app = new App();

        int[][] expectedMatrix = readMatrixFromFile(fileName);
        int[][] transposedMatrix = App.transposeMatrix(expectedMatrix);
        int[][] expectedTransposedMatrix = {{1, 4, 7}, {2, 5, 8}, {3, 6, 9}};

        assertArrayEquals(expectedTransposedMatrix, transposedMatrix);
    }

    @Test
    public void testTransposeVectorFromFile() {
        String fileName = "vector.txt";

        App app = new App();

        int[][] expectedMatrix = readMatrixFromFile(fileName);
        int[][] transposedMatrix = App.transposeMatrix(expectedMatrix);
        int[][] expectedTransposedMatrix = {{1}, {2}, {3}};

        assertArrayEquals(expectedTransposedMatrix, transposedMatrix);
    }

    @Test(expected = NumberFormatException.class)
    public void testNonNumericValueInMatrix() {
        String fileName = "nonNumericValue.txt";
        App app = new App();
        int[][] matrix = readMatrixFromFile(fileName);
       int[][] transposedMatrix = App.transposeMatrix(matrix); 
    }

    
       @Test
    public void testEmptyFile() {
        String fileName = "emptyFile.txt";

        int[][] matrix = readMatrixFromFile(fileName);

         assertTrue(matrix.length == 0); 
    }



    private int[][] readMatrixFromFile(String fileName) {
        List<int[]> rows = new ArrayList<>();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.trim().split("\\s+");
                int[] row = new int[values.length];
                for (int i = 0; i < values.length; i++) {
                    row[i] = Integer.parseInt(values[i]);
                }
                rows.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        int[][] matrix = new int[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            matrix[i] = rows.get(i);
        }

        return matrix;
    }
}

