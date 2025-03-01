import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PatientManagementSystemGUI {

    private Connection connection;
    private LocalDateTime dateTime;
    private String status;
    private int appointmentID;

    public PatientManagementSystemGUI() {
        try {
            // Establish database connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_system", "root", "maryam123");
            JOptionPane.showMessageDialog(null, "Connected to the database successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to connect to the database. Exiting.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void showGUI() {
        JFrame frame = new JFrame("Patient Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null); // Center the window

        JPanel panel = new JPanel(new GridLayout(0, 1));
        JTextArea waitingListArea = new JTextArea(10, 30);
        waitingListArea.setEditable(false);


        // Buttons for the GUI
        JButton addPatientBtn = new JButton("Add Patient");
        JButton scheduleAppointmentBtn = new JButton("Schedule Appointment");
        JButton cancelAppointmentBtn = new JButton("Cancel Appointment");
        JButton rescheduleAppointmentBtn = new JButton("Reschedule Appointment");
        JButton manageBillingBtn = new JButton("Manage Billing");
        JButton generateReportBtn = new JButton("Generate Report");
        JButton findPatientBtn = new JButton("Find Patient");
        JButton addToWaitListBtn = new JButton("Add to Waiting List");
        JButton removeFromWaitListBtn = new JButton("Remove from Waiting List");
        JButton viewWaitListBtn = new JButton("View Waiting List");

        JButton exitBtn = new JButton("Exit");

        panel.add(addPatientBtn);
        panel.add(scheduleAppointmentBtn);
        panel.add(cancelAppointmentBtn);
        panel.add(rescheduleAppointmentBtn);
        panel.add(manageBillingBtn);
        panel.add(generateReportBtn);
        panel.add(findPatientBtn);
        panel.add(addToWaitListBtn);
        panel.add(removeFromWaitListBtn);
        panel.add(viewWaitListBtn);
        panel.add(exitBtn);

        frame.add(panel);
        frame.setVisible(true);

        // Add button action listeners
        addPatientBtn.addActionListener(e -> addPatient());
        scheduleAppointmentBtn.addActionListener(e -> scheduleAppointment());
        cancelAppointmentBtn.addActionListener(e -> cancelAppointment());
        rescheduleAppointmentBtn.addActionListener(e ->rescheduleAppointment());
        manageBillingBtn.addActionListener(e -> manageBilling());
        generateReportBtn.addActionListener(e -> generateReport());
        findPatientBtn.addActionListener(e -> findPatient());
        addToWaitListBtn.addActionListener(e -> addToWaitingList());
        removeFromWaitListBtn.addActionListener(e -> removeFromWaitingList());
        viewWaitListBtn.addActionListener(e -> viewWaitingList(waitingListArea));
        exitBtn.addActionListener(e -> {
            try {
                connection.close();
                JOptionPane.showMessageDialog(null, "Database connection closed.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Failed to close database connection.");
                ex.printStackTrace();
            }
            System.exit(0);
        });
    }

    // Method to add a patient
    private void addPatient() {
        // Get input for all fields except patientID, which will be auto-generated
        String name = JOptionPane.showInputDialog("Enter Patient Name:");
        String ageStr = JOptionPane.showInputDialog("Enter Age:");
        String contactInfo = JOptionPane.showInputDialog("Enter Contact Info (optional):");
        String medicalHistory = JOptionPane.showInputDialog("Enter Medical History (optional):");
        String visitRecords = JOptionPane.showInputDialog("Enter Visit Records (optional):");
        String diseaseType = JOptionPane.showInputDialog("Enter Disease Type (optional):");
        String condition1 = JOptionPane.showInputDialog("Enter Condition (optional):");

        try {
            int age = Integer.parseInt(ageStr);

            // Generate a unique patient ID using your custom method
            int patientID = generateUniqueID(connection);

            // Insert the patient into the database with the generated patientID
            String query = "INSERT INTO Patient (patientID, name, age, contactInfo, medicalHistory, visitRecords, diseaseType, condition1) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, patientID); // Use the generated patient ID
            stmt.setString(2, name);
            stmt.setInt(3, age);
            stmt.setString(4, contactInfo);
            stmt.setString(5, medicalHistory);
            stmt.setString(6, visitRecords);
            stmt.setString(7, diseaseType);
            stmt.setString(8, condition1);

            stmt.executeUpdate();  // Execute the insert query
            JOptionPane.showMessageDialog(null, "Patient added successfully: " + name + "\nPatient ID: " + patientID);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to add patient.");
            e.printStackTrace();
        }
    }

    // Method to generate a unique patientID
    private int generateUniqueID(Connection connection) throws SQLException {
        int randomID;
        boolean isUnique;
        do {
            randomID = (int) (Math.random() * 9999) + 1; // Generate random ID between 1 and 9999
            String query = "SELECT COUNT(*) FROM Patient WHERE patientID = ?"; // Check if the ID already exists
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, randomID);
                try (ResultSet rs = stmt.executeQuery()) {
                    rs.next();
                    isUnique = (rs.getInt(1) == 0); // ID is unique if count is 0
                }
            }
        } while (!isUnique); // Repeat if the ID already exists in the database

        return randomID;
    }
    // Method to add a patient to the waiting list
    private void addToWaitingList() {
        String patientID = JOptionPane.showInputDialog("Enter Patient ID to add to waiting list:");
        try {
            String query = "INSERT INTO WaitingList (patientID) VALUES (?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, patientID);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Patient ID " + patientID + " added to waiting list.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to add patient to waiting list.");
            e.printStackTrace();
        }
    }

    // Method to remove a patient from the waiting list
    private void removeFromWaitingList() {
        String patientID = JOptionPane.showInputDialog("Enter Patient ID to remove from waiting list:");
        try {
            String query = "DELETE FROM WaitingList WHERE patientID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, patientID);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Patient ID " + patientID + " removed from waiting list.");
            } else {
                JOptionPane.showMessageDialog(null, "Patient ID " + patientID + " not found in waiting list.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to remove patient from waiting list.");
            e.printStackTrace();
        }
    }

    // Method to view the waiting list
    private void viewWaitingList(JTextArea waitingListArea) {
        try {
            String query = "SELECT w.waitlistID, p.patientID, p.name, w.dateAdded " +
                    "FROM WaitingList w JOIN Patient p ON w.patientID = p.patientID";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            waitingListArea.setText("<< Waiting List >>\n");
            while (rs.next()) {
                int waitlistID = rs.getInt("waitlistID");
                int patientID = rs.getInt("patientID");
                String name = rs.getString("name");
                String dateAdded = rs.getString("dateAdded");

                waitingListArea.append("Waitlist ID: " + waitlistID + ", Patient ID: " + patientID +
                        ", Name: " + name + ", Date Added: " + dateAdded + "\n");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to retrieve waiting list.");
            e.printStackTrace();
        }
    }
    private void scheduleAppointment() {
        String patientIDStr = JOptionPane.showInputDialog("Enter Patient ID to schedule an appointment:");
        String dateTimeStr = JOptionPane.showInputDialog("Enter Appointment Date and Time (YYYY-MM-DD HH:mm:ss):");

        try {
            // Parse the input into LocalDateTime with the correct format
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Convert to java.sql.Timestamp for database storage
            Timestamp timestamp = Timestamp.valueOf(dateTime);

            // Check for conflicts in the database
            String conflictQuery = "SELECT a.patientID, p.name, p.diseaseType, p.condition1 " +
                    "FROM Appointment a JOIN Patient p ON a.patientID = p.patientID " +
                    "WHERE a.dateTime = ?";
            try (PreparedStatement stmt = connection.prepareStatement(conflictQuery)) {
                stmt.setTimestamp(1, timestamp);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Conflict detected
                        int existingPatientID = rs.getInt("patientID");
                        String existingPatientName = rs.getString("name");
                        String existingDiseaseType = rs.getString("diseaseType");
                        String existingCondition = rs.getString("condition1");

                        JOptionPane.showMessageDialog(null, "Conflict detected with existing appointment for patient: " +
                                existingPatientName);

                        // Calculate priorities
                        Patient newPatient = getPatientFromDB(Integer.parseInt(patientIDStr)); // Retrieve new patient details
                        Patient existingPatient = new Patient(existingPatientID, existingPatientName, existingDiseaseType, existingCondition);

                        int newPatientPriority = calculatePriority(newPatient.getDiseaseType(), newPatient.getCondition1());
                        int existingPatientPriority = calculatePriority(existingDiseaseType, existingCondition);

                        if (newPatientPriority > existingPatientPriority) {
                            // New patient has higher priority - swap appointments
                            swapAppointments(timestamp, newPatient, existingPatient);
                        } else {
                            // New patient has lower priority - add to the waiting list
                            addToWaitingList(newPatient);
                        }
                    } else {
                        // No conflict - schedule appointment
                        scheduleNewAppointment(Integer.parseInt(patientIDStr), timestamp);
                    }
                }
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(null, "Invalid date-time format. Please use the format: YYYY-MM-DD HH:mm:ss.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error scheduling appointment. Please check the input and try again.");
            e.printStackTrace();
        }
    }

    // Helper method to calculate patient priority
    private int calculatePriority(String diseaseType, String condition1) {
        int diseasePriority = switch (diseaseType.toLowerCase()) {
            case "full burn" -> 3;
            case "partial" -> 2;
            case "cold" -> 1;
            default -> 0; // Default priority for unknown disease types
        };

        int conditionPriority = switch (condition1.toLowerCase()) {
            case "critical" -> 3;
            case "serious" -> 2;
            case "stable" -> 1;
            default -> 0; // Default priority for unknown conditions
        };

        return (diseasePriority * 10) + conditionPriority;
    }

    // Helper method to swap appointments and add the existing patient to the waiting list
    private void swapAppointments(Timestamp timestamp, Patient newPatient, Patient existingPatient) throws SQLException {
        String updateQuery = "UPDATE Appointment SET patientID = ? WHERE dateTime = ?";
        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
            updateStmt.setInt(1, newPatient.getPatientID());
            updateStmt.setTimestamp(2, timestamp);
            updateStmt.executeUpdate();
        }

        // Add the old patient to the waiting list
        WaitingList waitingList = new WaitingList(connection);
        waitingList.addPatientToWaitList(connection, existingPatient);

        JOptionPane.showMessageDialog(null, "New patient has a higher priority. Replaced existing patient " +
                existingPatient.getName() + " and moved them to the waiting list.");
    }

    // Helper method to add a patient to the waiting list
    private void addToWaitingList(Patient patient) throws SQLException {
        WaitingList waitingList = new WaitingList(connection);
        waitingList.addPatientToWaitList(connection, patient);

        JOptionPane.showMessageDialog(null, "Existing patient has a higher priority. Added new patient to the waiting list.");
    }

    // Helper method to schedule a new appointment
    private void scheduleNewAppointment(int patientID, Timestamp timestamp) throws SQLException {
        String insertQuery = "INSERT INTO Appointment (appointmentID, patientID, dateTime, status) " +
                "VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            int appointmentID = generateUniqueAppointmentID(connection); // Generate a unique appointment ID
            stmt.setInt(1, appointmentID); // Set the generated appointment ID
            stmt.setInt(2, patientID); // Set the patient ID from user input
            stmt.setTimestamp(3, timestamp); // Set the appointment date and time
            stmt.setString(4, "Scheduled"); // Set the status as "Scheduled"
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Appointment scheduled successfully for patient ID: " +
                    patientID + " at " + timestamp);
        }
    }



    // Method to reschedule an appointment via the GUI
    private void rescheduleAppointment() {
        // Get the appointmentID from the user
        String appointmentIDStr = JOptionPane.showInputDialog("Enter Appointment ID to Reschedule:");

        if (appointmentIDStr == null || appointmentIDStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Appointment ID is required.");
            return;
        }

        int appointmentID;
        try {
            appointmentID = Integer.parseInt(appointmentIDStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid Appointment ID.");
            return;
        }

        // Get the new date and time for the appointment
        String newDate = JOptionPane.showInputDialog("Enter the new date (YYYY-MM-DD):");
        String newTime = JOptionPane.showInputDialog("Enter the new time (HH:mm):");

        // Validate the date and time inputs
        if (newDate == null || newTime == null || newDate.trim().isEmpty() || newTime.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Both date and time are required.");
            return;
        }

        // Try to reschedule the appointment with the entered information
        try {
            reschedule(appointmentID, newDate, newTime);
            JOptionPane.showMessageDialog(null, "Appointment rescheduled successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error while rescheduling the appointment.");
            e.printStackTrace();
        }
    }

    // Method to update the appointment in the database
    public void reschedule(int appointmentID, String newDate, String newTime) throws SQLException {
        // Parse the new date and time into a LocalDateTime object using a custom format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(newDate + " " + newTime, formatter);  // Combining newDate and newTime into one string

        String status = "Rescheduled";  // Update status to 'Rescheduled'

        // Prepare SQL query to update the appointment in the database
        String query = "UPDATE Appointment SET dateTime = ?, status = ? WHERE appointmentID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Convert LocalDateTime to Timestamp to store in the database
            stmt.setTimestamp(1, Timestamp.valueOf(dateTime));  // LocalDateTime -> Timestamp conversion
            stmt.setString(2, status);  // Set the updated status
            stmt.setInt(3, appointmentID);  // Use the specific appointmentID for the update
            stmt.executeUpdate();  // Execute the update query
        } catch (SQLException e) {
            // Handle any SQL exceptions, such as connection issues or query failures
            System.err.println("Error while rescheduling the appointment: " + e.getMessage());
            throw e;  // Rethrow the exception to notify the caller
        }
    }

    // Helper method to get a patient from the database by ID
    private Patient getPatientFromDB(int patientID) throws SQLException {
        String query = "SELECT name, diseaseType, condition1 FROM Patient WHERE patientID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, patientID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String diseaseType = rs.getString("diseaseType");
                    String condition = rs.getString("condition1");
                    return new Patient(patientID, name, diseaseType, condition);
                } else {
                    throw new SQLException("Patient not found with ID: " + patientID);
                }
            }
        }
    }

    // Helper method to generate a unique appointment ID
    private int generateUniqueAppointmentID(Connection connection) throws SQLException {
        int randomID;
        boolean isUnique;
        do {
            randomID = (int) (Math.random() * 9999) + 1; // Generate random ID between 1 and 9999
            String query = "SELECT COUNT(*) FROM Appointment WHERE appointmentID = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, randomID);
                try (ResultSet rs = stmt.executeQuery()) {
                    rs.next();
                    isUnique = (rs.getInt(1) == 0); // ID is unique if count is 0
                }
            }
        } while (!isUnique);

        return randomID;
    }

    // Method to cancel an appointment
    private void cancelAppointment() {
        String patientID = JOptionPane.showInputDialog("Enter Patient ID to cancel appointment:");

        try {
            String query = "DELETE FROM Appointments WHERE PatientID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, patientID);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Appointment canceled for Patient ID: " + patientID);
            } else {
                JOptionPane.showMessageDialog(null, "No appointment found for Patient ID: " + patientID);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to cancel appointment.");
            e.printStackTrace();
        }
    }

    // Method to manage billing
    private void manageBilling() {
        String patientID = JOptionPane.showInputDialog("Enter Patient ID for billing:");
        if (patientID == null || patientID.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Patient ID cannot be empty.");
            return;
        }

        String amountStr = JOptionPane.showInputDialog("Enter Billing Amount:");
        if (amountStr == null || amountStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Billing Amount cannot be empty.");
            return;
        }

        try {
            // Validate that the amount is a valid double
            double amount = Double.parseDouble(amountStr);

            // Check if the amount is non-negative
            if (amount < 0) {
                JOptionPane.showMessageDialog(null, "Billing amount cannot be negative.");
                return;
            }

            // SQL query to insert a new billing record
            String query = "INSERT INTO Billing (PatientID, Amount) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, patientID); // Set Patient ID
                stmt.setDouble(2, amount);    // Set Billing Amount
                stmt.executeUpdate();         // Execute the update

                // Show a success message
                JOptionPane.showMessageDialog(null, "Billing record created for Patient ID " + patientID + ": $" + amount);
            }
        } catch (NumberFormatException e) {
            // Handle invalid input for the amount (non-numeric input)
            JOptionPane.showMessageDialog(null, "Invalid billing amount. Please enter a valid number.");
            e.printStackTrace();
        } catch (SQLException e) {
            // Handle SQL exceptions (e.g., connection issues, invalid query)
            JOptionPane.showMessageDialog(null, "Failed to manage billing.");
            e.printStackTrace();
        }
    }

    // Method to generate a report
    private void generateReport() {
        try {
            // Query to fetch patient and appointment data
            String query = "SELECT p.patientID, p.name, p.age, a.dateTime " +
                    "FROM Patient p LEFT JOIN Appointment a ON p.patientID = a.patientID";

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            StringBuilder report = new StringBuilder();
            report.append("<< Patient and Appointment Report >>\n");
            report.append("-------------------------------------------------\n");
            report.append(String.format("%-10s %-20s %-5s %-20s\n", "ID", "Name", "Age", "Appointment"));
            report.append("-------------------------------------------------\n");

            while (rs.next()) {
                int patientID = rs.getInt("patientID");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String appointment = rs.getString("dateTime") != null ? rs.getString("dateTime") : "No Appointment";

                report.append(String.format("%-10d %-20s %-5d %-20s\n", patientID, name, age, appointment));
            }

            JOptionPane.showMessageDialog(null, report.toString());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to generate report.");
            e.printStackTrace();
        }
    }


    // Method to find a patient by ID
    private void findPatient() {
        String patientID = JOptionPane.showInputDialog("Enter Patient ID to search:");

        try {
            String query = "SELECT * FROM Patient WHERE patientID = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, patientID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                StringBuilder patientInfo = new StringBuilder();
                patientInfo.append("Patient found: \n");
                patientInfo.append("Name: ").append(rs.getString("name")).append("\n");
                patientInfo.append("Age: ").append(rs.getInt("age")).append("\n");
                patientInfo.append("Contact Info: ").append(rs.getString("contactInfo")).append("\n");
                patientInfo.append("Medical History: ").append(rs.getString("medicalHistory")).append("\n");
                patientInfo.append("Visit Records: ").append(rs.getString("visitRecords")).append("\n");
                patientInfo.append("Disease Type: ").append(rs.getString("diseaseType")).append("\n");
                patientInfo.append("Condition: ").append(rs.getString("condition1")).append("\n");

                JOptionPane.showMessageDialog(null, patientInfo.toString());
            } else {
                JOptionPane.showMessageDialog(null, "Patient not found.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error finding patient.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PatientManagementSystemGUI system = new PatientManagementSystemGUI();
        system.showGUI();
    }
}