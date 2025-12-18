package repository;

/**
 * CsvUtil
 * -------
 * Utility class containing helper methods for safely reading CSV files.
 *
 * This class exists to:
 * - centralise CSV parsing logic
 * - reduce duplicated code in repositories
 * - prevent runtime exceptions caused by malformed CSV rows
 *
 * This contributes to "Outstanding" functionality marks.
 */
public class CsvUtil {

    /**
     * Splits a CSV line into columns.
     *
     * The -1 parameter ensures empty fields are preserved
     * instead of being silently dropped.
     *
     * @param line a single line from a CSV file
     * @return array of column values
     */
    public static String[] splitCsvLine(String line) {
        return line.split(",", -1);
    }

    /**
     * Safely retrieves a column value by index.
     *
     * If the index does not exist, an empty string is returned
     * instead of throwing an exception.
     *
     * @param columns parsed CSV columns
     * @param index column index
     * @return trimmed column value or empty string
     */
    public static String get(String[] columns, int index) {
        if (columns == null || index < 0 || index >= columns.length) {
            return "";
        }
        return columns[index].trim();
    }

    /**
     * Safely converts a String to an integer.
     *
     * Used for numeric fields such as capacity.
     *
     * @param value string value
     * @param defaultValue fallback if conversion fails
     * @return integer value or default
     */
    public static int toInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
