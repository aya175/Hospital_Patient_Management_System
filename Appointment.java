import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment {
    private static int appointmentIDCounter = 100;
    private int appointmentID;
    private Patient patient;
    private LocalDateTime dateTime;
    private String status; // Status: Scheduled, Cancelled, Rescheduled

    // Constructor to initialize an appointment and save to the database
    public Appointment(Connection connection, Patient patient, LocalDateTime dateTime, String status) throws SQLException {
        this.appointmentID = generateNextAppointmentID(connection);
        this.patient = patient;
        this.dateTime = dateTime;
        this.status = status;
        saveToDatabase();
    }


    // Method to retrieve the last used appointment ID and increment it for the next appointment
    private int generateNextAppointmentID(Connection connection) throws SQLException {
        // Query to get the highest appointmentID currently in the table
        String query = "SELECT MAX(appointmentID) FROM Appointment";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                appointmentIDCounter = rs.getInt(1) + 1; // Set the counter to the next ID
            }
        }
        return appointmentIDCounter++;
    }

    // Method to save the appointment to the database
    private void saveToDatabase() throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String query = "INSERT INTO Appointment (appointmentID, patientID, dateTime, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, appointmentID);
            stmt.setInt(2, patient.getPatientID()); // Assuming PatientNode has getPatientID method
            stmt.setTimestamp(3, Timestamp.valueOf(dateTime)); // Convert LocalDateTime to Timestamp for SQL
            stmt.setString(4, status);
            stmt.executeUpdate();
        }
    }

    // Method to reschedule an appointment (update date/time in the database)
    public void reschedule(Connection connection, String newDate, String newTime) throws SQLException {
        // Parse the new date and time into a LocalDateTime object
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.dateTime = LocalDateTime.parse(newDate + " " + newTime, formatter);
        this.status = "Rescheduled";

        // Update the appointment in the database
        String query = "UPDATE Appointment SET dateTime = ?, status = ? WHERE appointmentID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setTimestamp(1, Timestamp.valueOf(dateTime)); // Convert LocalDateTime to Timestamp for SQL
            stmt.setString(2, status);
            stmt.setInt(3, appointmentID);
            stmt.executeUpdate();
        }
    }

    // Method to cancel an appointment (set the status to "Cancelled" in the database)
    public void cancel(Connection connection) throws SQLException {
        this.status = "Cancelled";

        // Update the appointment status in the database
        String query = "UPDATE Appointment SET status = ? WHERE appointmentID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, appointmentID);
            stmt.executeUpdate();
        }
    }

    // Method to schedule an appointment (set the status to "Scheduled" in the database)
    public void schedule(Connection connection) throws SQLException {
        // Check if the appointment time is already taken
        String conflictQuery = "SELECT a.patientID, p.name, p.diseaseType, p.condition1 " +
                "FROM Appointment a JOIN patient p ON a.patientID = p.patientID " +
                "WHERE a.dateTime = ?";
        try (PreparedStatement stmt = connection.prepareStatement(conflictQuery)) {
            stmt.setTimestamp(1, Timestamp.valueOf(dateTime)); // Set the dateTime in the query
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // A conflict exists
                    int existingPatientID = rs.getInt("patientID");
                    String existingPatientName = rs.getString("name");
                    String existingDiseaseType = rs.getString("diseaseType");
                    String existingCondition = rs.getString("condition1");

                    System.out.println("Conflict detected with existing appointment for patient: " + existingPatientName);

                    // Compare priority (higher priority stays in the appointment)
                    int newPatientPriority = calculatePriority(patient.getDiseaseType(), patient.getCondition1());
                    int existingPatientPriority = calculatePriority(existingDiseaseType, existingCondition);

                    if (newPatientPriority > existingPatientPriority) {
                        // New patient has higher priority - swap them
                        System.out.println("New patient has a higher priority. Replacing existing patient with " + patient.getName());

                        // Update the appointment for the new patient
                        String updateQuery = "UPDATE Appointment SET patientID = ? WHERE dateTime = ?";
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                            updateStmt.setInt(1, patient.getPatientID());
                            updateStmt.setTimestamp(2, Timestamp.valueOf(dateTime));
                            updateStmt.executeUpdate();
                        }

                        // Add the old patient to the waiting list
                        Patient oldPatient = new Patient(existingPatientID, existingPatientName, existingDiseaseType, existingCondition);
                        WaitingList waitingList = new WaitingList(connection);
                        waitingList.addPatientToWaitList(connection, oldPatient);

                        System.out.println("Moved existing patient to waiting list: " + existingPatientName);
                    } else {
                        // New patient has lower priority - add to the waiting list
                        System.out.println("Existing patient has a higher priority. Adding new patient to the waiting list.");
                        WaitingList waitingList = new WaitingList(connection);
                        waitingList.addPatientToWaitList(connection, patient);
                    }
                } else {
                    // No conflict, schedule the appointment as usual
                    this.status = "Scheduled";
                    String query = "INSERT INTO Appointment (appointmentID, patientID, dateTime, status) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement stmt2 = connection.prepareStatement(query)) {
                        stmt2.setInt(1, appointmentID);
                        stmt2.setInt(2, patient.getPatientID());
                        stmt2.setTimestamp(3, Timestamp.valueOf(dateTime));
                        stmt2.setString(4, status);
                        stmt2.executeUpdate();
                        System.out.println("Appointment scheduled for patient: " + patient.getName());
                    }
                }
            }
        }
    }

    
     // Helper method to calculate the priority of a patient based on disease type and condition

    private int calculatePriority(String diseaseType, String condition) {
        int diseasePriority = switch (diseaseType.toLowerCase()) {
            case "full burn" -> 3;
            case "partial burn" -> 2;
            case "cold" -> 1;
            default -> 0;
        };

        int conditionPriority = switch (condition.toLowerCase()) {
            case "critical" -> 3;
            case "serious" -> 2;
            case "stable" -> 1;
            default -> 0;
        };

        // Combine disease and condition priority to create a single score
        return diseasePriority * 10 + conditionPriority; // Disease has more weight than condition
    }

    // Method to get appointment details from the database
    public String getAppointmentDetails(Connection connection) {
        String query = "SELECT a.appointmentID, a.dateTime, a.status, p.name " +
                "FROM Appointment a JOIN patient p ON a.patientID = p.patientID " +
                "WHERE a.appointmentID = ?";
        StringBuilder appointmentDetails = new StringBuilder();

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, appointmentID); // Set the appointmentID in the query

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Fetch appointment details from the database
                    appointmentID = rs.getInt("appointmentID");
                    String patientName = rs.getString("name");
                    LocalDateTime appointmentDateTime = rs.getTimestamp("dateTime").toLocalDateTime();
                    String status = rs.getString("status");

                    // Format the appointment details into a string
                    appointmentDetails.append("Appointment ID: ").append(appointmentID).append("\n")
                            .append("Patient: ").append(patientName).append("\n")
                            .append("Date and Time: ").append(appointmentDateTime).append("\n")
                            .append("Status: ").append(status);
                } else {
                    appointmentDetails.append("No appointment found with ID: ").append(appointmentID);
                }
            }
        } catch (SQLException e) {
            appointmentDetails.append("Error retrieving appointment information: ").append(e.getMessage());
        }

        return appointmentDetails.toString();
    }

    public int getAppointmentID() {
        return appointmentID;
    }
    public Patient getPatient() {
        return patient;
    }
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    public String getStatus() {
        return status;
    }
}
