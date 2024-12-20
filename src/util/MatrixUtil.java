package util;

import java.text.DecimalFormat;

public abstract class MatrixUtil {

    static DecimalFormat df = new DecimalFormat("#.###");

    // Mine
    public static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            System.out.print("| ");
            for (double element : row) {
                if (element == -0) { element = 0; }
                StringBuilder out = new StringBuilder(df.format(element));
                while (out.length() < 7) { out.append(" "); }
                System.out.print(out);
            } System.out.println("|");
        }
    }

    // StackOverflow Yoink
    /**
     * Puts a matrix into reduced row echelon form
     *
     * @param matrix input matrix
     * @return 2D result matrix
     */
    public static double[][] rref(double[][] matrix) {
        int columnIndex = 0;
        int cursor;

        // number of rows and columns in matrix
        int getRowSize = matrix.length;
        int getColumnSize = matrix[0].length;

        loop:
        for (int rowIndex = 0; rowIndex < getRowSize; rowIndex++) {
            if (getColumnSize <= columnIndex) {
                break loop;
            }
            cursor = rowIndex;
            while (matrix[cursor][columnIndex] == 0) {
                cursor++;
                if (getRowSize == cursor) {
                    cursor = rowIndex;
                    columnIndex++;
                    if (getColumnSize == columnIndex) {
                        break loop;
                    }
                }

            }

            matrix = rowSwap(matrix, cursor, rowIndex);
            if (matrix[rowIndex][columnIndex] != 0) {
                matrix = rowScale(matrix, rowIndex, (1 / matrix[rowIndex][columnIndex]));
            }

            for (cursor = 0; cursor < getRowSize; cursor++) {
                if (cursor != rowIndex) {
                    matrix = rowAddScale(matrix, rowIndex, cursor, ((-1) * matrix[cursor][columnIndex]));
                }
            }
            columnIndex++;
        }
        return matrix;
    }

    /**
     * Swap positions of 2 rows
     *
     * @param matrix    matrix before row additon
     * @param rowIndex1 int index of row to swap
     * @param rowIndex2 int index of row to swap
     * @return matrix after row swap
     */
    private static double[][] rowSwap(double[][] matrix, int rowIndex1, int rowIndex2) {
        // number of columns in matrix
        int numColumns = matrix[0].length;

        // holds number to be swapped
        double hold;

        for (int k = 0; k < numColumns; k++) {
            hold = matrix[rowIndex2][k];
            matrix[rowIndex2][k] = matrix[rowIndex1][k];
            matrix[rowIndex1][k] = hold;
        }

        return matrix;
    }

    /**
     * Adds 2 rows together row2 = row2 + row1
     *
     * @param matrix    matrix before row additon
     * @param rowIndex1 int index of row to be added
     * @param rowIndex2 int index or row that row1 is added to
     * @return matrix after row addition
     */
    private static double[][] rowAdd(double[][] matrix, int rowIndex1, int rowIndex2) {
        // number of columns in matrix
        int numColumns = matrix[0].length;

        for (int k = 0; k < numColumns; k++) {
            matrix[rowIndex2][k] += matrix[rowIndex1][k];
        }

        return matrix;
    }

    /**
     * Multiplies a row by a scalar
     *
     * @param matrix   matrix before row additon
     * @param rowIndex int index of row to be scaled
     * @param scalar   double to scale row by
     * @return matrix after row scaling
     */
    private static double[][] rowScale(double[][] matrix, int rowIndex, double scalar) {
        // number of columns in matrix
        int numColumns = matrix[0].length;

        for (int k = 0; k < numColumns; k++) {
            matrix[rowIndex][k] *= scalar;
        }

        return matrix;
    }

    /**
     * Adds a row by the scalar of another row
     * row2 = row2 + (row1 * scalar)
     *
     * @param matrix    matrix before row additon
     * @param rowIndex1 int index of row to be added
     * @param rowIndex2 int index or row that row1 is added to
     * @param scalar    double to scale row by
     * @return matrix after row addition
     */
    private static double[][] rowAddScale(double[][] matrix, int rowIndex1, int rowIndex2, double scalar) {
        // number of columns in matrix
        int numColumns = matrix[0].length;

        for (int k = 0; k < numColumns; k++) {
            matrix[rowIndex2][k] += (matrix[rowIndex1][k] * scalar);
        }

        return matrix;
    }
}
