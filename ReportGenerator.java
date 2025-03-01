import java.sql.*;
import java.util.*;

public class ReportGenerator {

    private String reportType;
    private List<Integer> data;
    private Connection connection;

    // Database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/hospital_system"; // Update as needed
    private static final String USERNAME = "root"; // Your database username
    private static final String PASSWORD = "maryam123"; // Your database password

    public ReportGenerator(String reportType) {
        this.reportType = reportType;
        this.data = new ArrayList<>();
        connectToDatabase();
    }

     //Connect to the MySQL database using JDBC

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database connected successfully.");
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
        }
    }


    // Generate a Patient Visit History Report by retrieving visit data from the database

    public void generatePatientReport() {
        System.out.println("\nGenerating Patient Visit History Report...");
        getDataFromDatabase("SELECT patientID FROM patient"); // Assuming there is a `visitRecord` table
        sortDataUsingMergeSort();
        System.out.println("Sorted Patient Data: " + data);
        System.out.println("Report: Total Patient - " + data.size());
    }


    //  Generate an Appointment Statistics Report by retrieving appointment data from the database

    public void generateAppointmentReport() {
        System.out.println("\nGenerating Appointment Statistics Report...");
        getDataFromDatabase("SELECT appointmentID FROM appointment "); // Exclude canceled appointments (assuming status column exists in the appointment table)
        sortDataUsingQuickSort(0, data.size() - 1);
        System.out.println("Sorted Appointment Data: " + data);
        System.out.println("Report: Total Appointments (including canceled) - " + data.size());

    }


     // Generate a Revenue Summary Report by retrieving billing data from the database

    public void generateRevenueReport() {
        System.out.println("\nGenerating Revenue Summary Report...");
        getDataFromDatabase("SELECT billingAmount FROM billing"); // Assuming there is a `billing` table
        sortDataUsingMergeSort();
        System.out.println("Sorted Revenue Data: " + data);
        int totalRevenue = data.stream().mapToInt(Integer::intValue).sum();
        System.out.println("Report: Total Revenue - $" + totalRevenue);
    }


    // get data from the database based on the provided SQL query
     // @param query The SQL query to run

    private void getDataFromDatabase(String query) {
        data.clear();
        try (Statement stmt = connection.createStatement();
             ResultSet resultSet = stmt.executeQuery(query)) {
            while (resultSet.next()) {
                // Assuming that the first column contains an integer value (can be customized)
                data.add(resultSet.getInt(1));
            }
            System.out.println("Data fetched successfully from the database.");
        } catch (SQLException e) {
            System.err.println("Error fetching data from the database: " + e.getMessage());
        }
    }

     // Close the database connection

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing the database connection: " + e.getMessage());
        }
    }

    private void sortDataUsingMergeSort() {
        data = mergeSort(data);
    }

    private List<Integer> mergeSort(List<Integer> data) {
        if (data.size() <= 1) {
            return data;
        }

        int mid = data.size() / 2;
        List<Integer> left = mergeSort(data.subList(0, mid));
        List<Integer> right = mergeSort(data.subList(mid, data.size()));

        return merge(left, right);
    }

    private List<Integer> merge(List<Integer> left, List<Integer> right) {
        List<Integer> sorted = new ArrayList<>();
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            if (left.get(i) <= right.get(j)) {
                sorted.add(left.get(i++));
            } else {
                sorted.add(right.get(j++));
            }
        }

        while (i < left.size()) {
            sorted.add(left.get(i++));
        }

        while (j < right.size()) {
            sorted.add(right.get(j++));
        }

        return sorted;
    }

    private void sortDataUsingQuickSort(int low, int high) {
        // Ensure indices are valid before sorting
        if (low < 0 || high >= data.size() || low >= high) {
            return; // Invalid indices, no sorting needed
        }
        if (low < high) {
            int pivotIndex = partition(low, high);
            sortDataUsingQuickSort(low, pivotIndex - 1);
            sortDataUsingQuickSort(pivotIndex + 1, high);
        }
    }

    private int partition(int low, int high) {
        int pivot = data.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (data.get(j) < pivot) {
                i++;
                Collections.swap(data, i, j);
            }
        }
        Collections.swap(data, i + 1, high);
        return i + 1;
    }

     //Main method to test the ReportGenerator

    public static void main(String[] args) {
        ReportGenerator reportGenerator = new ReportGenerator("Patient");

        // Generate Patient Report
        reportGenerator.generatePatientReport();

        // Generate Appointment Report
        reportGenerator.generateAppointmentReport();

        // Generate Revenue Report
        reportGenerator.generateRevenueReport();

        // Close the database connection
        reportGenerator.closeConnection();
    }
}
