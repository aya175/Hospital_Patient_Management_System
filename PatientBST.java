import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PatientBST {
    private PatientNode root;
    private Connection connection;

    // Constructor: Initialize the BST and database connection
    public PatientBST() {
        root = null;
        connectToDatabase();
        loadPatientsFromDatabase();
    }

    //  Connect to MySQL database
    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_system", "root", "maryam123"); // Update username and password
            System.out.println("Connected to the database successfully.");
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database.");
            e.printStackTrace();
        }
    }

    // Load existing patients from the database and insert them into the BST
    private void loadPatientsFromDatabase() {
        try {
            String query = "SELECT * FROM patient"; // Ensure table name is correct
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int patientID = rs.getInt("patientID");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String contactInfo = rs.getString("contactInfo");
                String medicalHistory = rs.getString("medicalHistory");
                String visitRecords = rs.getString("visitRecords");
                String diseaseType = rs.getString("diseaseType");
                String condition1 = rs.getString("condition1");
                String arrivalTimeStr = rs.getString("arrivalTime");
                LocalDateTime arrivalTime = LocalDateTime.parse(arrivalTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                // Instead of inserting into the database again, only insert into the BST
                Patient patient = new Patient(patientID, name, diseaseType, condition1);
                insert(patient); // Insert patient into BST only
            }

            System.out.println("Loaded all patients into the BST.");
        } catch (SQLException e) {
            System.out.println("Failed to load patients from the database.");
            e.printStackTrace();
        }
    }

    // Insert a new patient into the BST and the database
    public void insert(Patient patient) {
        root = insertRec(root, patient);
        insertPatientIntoDatabase(patient); // Insert into database too
    }

    // Recursively insert into the BST
    private PatientNode insertRec(PatientNode root, Patient patient) {
        if (root == null) {
            return new PatientNode(patient);
        }

        if (patient.getPatientID() < root.getPatient().getPatientID()) {
            root.setLeft(insertRec(root.getLeft(), patient));
        } else if (patient.getPatientID() > root.getPatient().getPatientID()) {
            root.setRight(insertRec(root.getRight(), patient));
        }

        return root;
    }

    //  Insert a patient into the MySQL Patient table
    private void insertPatientIntoDatabase(Patient patient) {
        try {
            // Check if the patient already exists in the database
            String checkQuery = "SELECT COUNT(*) FROM patient WHERE patientID = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setInt(1, patient.getPatientID());
            ResultSet rs = checkStmt.executeQuery();
            rs.next();

            if (rs.getInt(1) == 0) {  // If no such patient exists, insert new record
                String query = "INSERT INTO patient (patientID, name, age, contactInfo, medicalHistory, visitRecords, diseaseType, condition1, arrivalTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setInt(1, patient.getPatientID());
                stmt.setString(2, patient.getName());
                stmt.setInt(3, patient.getAge());
                stmt.setString(4, patient.getContactInfo());
                stmt.setString(5, patient.getMedicalHistory());
                stmt.setString(6, patient.getVisitRecords());
                stmt.setString(7, patient.getDiseaseType());
                stmt.setString(8, patient.getCondition1());
                stmt.setString(9, String.valueOf(patient.getArrivalTime()));
                stmt.executeUpdate();
                System.out.println("Patient inserted into the database successfully.");
            } else {
                System.out.println("Patient with ID " + patient.getPatientID() + " already exists.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to insert patient into the database.");
            e.printStackTrace();
        }
    }

    //  Search for a patient by patientID in the BST
    public Patient search(int patientID) {
        return searchRec(root, patientID);
    }

    // Recursively search the BST for the patientID
    private Patient searchRec(PatientNode root, int patientID) {
        if (root == null) {
            return null; // Patient not found
        }

        if (root.getPatient().getPatientID() == patientID) {
            return root.getPatient(); // Patient found
        }

        if (patientID < root.getPatient().getPatientID()) {
            return searchRec(root.getLeft(), patientID);
        } else {
            return searchRec(root.getRight(), patientID);
        }
    }
    public void displayPreOrder() {
        displayPreOrderRec(root);
    }

    private void displayPreOrderRec(PatientNode root) {
        if (root != null) {
            // Display current node (patient)
            System.out.println("Patient ID: " + root.getPatient().getPatientID());
            System.out.println("Name: " + root.getPatient().getName());
            System.out.println("Age: " + root.getPatient().getAge());
            System.out.println("Disease Type: " + root.getPatient().getDiseaseType());
            System.out.println("Condition: " + root.getPatient().getCondition1());
            System.out.println("Arrival Time: " + root.getPatient().getArrivalTime());
            System.out.println("----------------------------");

            // Then visit the left and right child
            displayPreOrderRec(root.getLeft());
            displayPreOrderRec(root.getRight());
        }
    }


}
