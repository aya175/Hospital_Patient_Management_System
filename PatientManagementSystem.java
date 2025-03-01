import java.sql.*;  // Import for database operations
import java.util.Scanner;

public class PatientManagementSystem {

    private Connection connection; // Database connection object
    private Scanner sc; // Scanner for input

    // Constructor
    public PatientManagementSystem() {
        try {
            // Connect to MySQL database
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db", "username", "maryam123");
            System.out.println("Connected to the database successfully!");
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database.");
            e.printStackTrace();
            System.exit(1);
        }

        sc = new Scanner(System.in);
    }

    public void addPatient() {
        System.out.print("Enter Patient ID: ");
        String id = sc.next();
        System.out.print("Enter Patient Name: ");
        String name = sc.next();
        System.out.print("Enter Priority: ");
        int priority = sc.nextInt();

        try {
            String query = "INSERT INTO patient (PatientID, Name, Priority) VALUES (?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, id);
            stmt.setString(2, name);
            stmt.setInt(3, priority);
            stmt.executeUpdate();
            System.out.println("Patient added successfully: " + name);
        } catch (SQLException e) {
            System.err.println("Failed to add patient.");
            e.printStackTrace();
        }
    }

    public void findPatientCase() {
        System.out.print("Enter Patient ID to search: ");
        String patientID = sc.next();

        try {
            String query = "SELECT * FROM patient WHERE PatientID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, patientID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("Patient found: " + rs.getString("Name") + " , ID: " + rs.getString("PatientID"));
            } else {
                System.out.println("Patient not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error finding patient.");
            e.printStackTrace();
        }
    }

    public void scheduleAppointment() {
        System.out.print("Enter Patient ID to schedule an appointment: ");
        String patientID = sc.next();
        System.out.print("Enter Appointment Date (YYYY-MM-DD): ");
        String date = sc.next();

        try {
            String query = "INSERT INTO Appointments (PatientID, Date) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, patientID);
            stmt.setString(2, date);
            stmt.executeUpdate();
            System.out.println("Appointment scheduled for Patient ID " + patientID + " on " + date);
        } catch (SQLException e) {
            System.err.println("Failed to schedule appointment.");
            e.printStackTrace();
        }
    }

    public void cancelAppointment() {
        System.out.print("Enter Patient ID to cancel appointment: ");
        String patientID = sc.next();

        try {
            String query = "DELETE FROM Appointments WHERE PatientID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, patientID);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Appointment canceled for Patient ID: " + patientID);
            } else {
                System.out.println("No appointment found for Patient ID: " + patientID);
            }
        } catch (SQLException e) {
            System.err.println("Failed to cancel appointment.");
            e.printStackTrace();
        }
    }

    public void manageBilling() {
        System.out.print("Enter Patient ID for billing: ");
        String patientID = sc.next();
        System.out.print("Enter Billing Amount: ");
        double amount = sc.nextDouble();

        try {
            String query = "INSERT INTO Billing (PatientID, Amount) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, patientID);
            stmt.setDouble(2, amount);
            stmt.executeUpdate();
            System.out.println("Billing record created for Patient ID " + patientID + ": $" + amount);
        } catch (SQLException e) {
            System.err.println("Failed to manage billing.");
            e.printStackTrace();
        }
    }

    public void generateReport() {
        try {
            String query = "SELECT * FROM Patients";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("\n<< Patient Report >>");
            while (rs.next()) {
                System.out.println("Patient: " + rs.getString("Name") + " , ID: " + rs.getString("PatientID"));
            }
        } catch (SQLException e) {
            System.err.println("Failed to generate report.");
            e.printStackTrace();
        }
    }

    public void menu() {
        int choice;
        do {
            System.out.println("\nPatient Management System Menu:");
            System.out.println("1. Add Patient");
            System.out.println("2. Schedule Appointment");
            System.out.println("3. Cancel Appointment");
            System.out.println("4. Manage Billing");
            System.out.println("5. Generate Report");
            System.out.println("6. Find Patient");
            System.out.println("7. Exit");

            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1: addPatient(); break;
                case 2: scheduleAppointment(); break;
                case 3: cancelAppointment(); break;
                case 4: manageBilling(); break;
                case 5: generateReport(); break;
                case 6: findPatientCase(); break;
                case 7: System.out.println("Exiting... Goodbye!"); break;
                default: System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 7);

        try {
            connection.close();
            System.out.println("Database connection closed.");
        } catch (SQLException e) {
            System.err.println("Failed to close database connection.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PatientManagementSystem system = new PatientManagementSystem();
        system.menu();
    }
}
