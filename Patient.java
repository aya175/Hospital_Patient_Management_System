import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Patient {
    private int patientID;
    private String name;
    private int age;
    private String contactInfo;
    private String medicalHistory;
    private String visitRecords;
    private String diseaseType; // Disease type (Total Burn, Partial Burn, etc.)
    private String condition1; // Patient condition (Critical, Serious, Stable)
    private LocalDateTime arrivalTime= LocalDateTime.now();

    public Patient(Connection connection, String name, int age, String contactInfo, String medicalHistory, String visitRecords, String diseaseType, String condition1,LocalDateTime arrivalTime) throws SQLException {
        this.patientID = generateUniqueID(connection);
        this.name = name;
        this.age = age;
        this.contactInfo = contactInfo;
        this.medicalHistory = medicalHistory;
        this.visitRecords = visitRecords;
        this.diseaseType = diseaseType;
        this.condition1 = condition1;
        this.arrivalTime=arrivalTime;
//        saveToDatabase(connection);
    }



    public Patient(int patientID, String name, String diseaseType, String condition1) {
        this.patientID = patientID;
        this.name = name;
        this.diseaseType = diseaseType;
        this.condition1 = condition1;
    }

    private int generateUniqueID(Connection connection) throws SQLException {
        int randomID;
        boolean isUnique;
        do {
            randomID = (int) (Math.random() * 9999) + 1; // Generate random ID between 1 and 9999
            String query = "SELECT COUNT(*) FROM patient WHERE patientID = ?";
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

    private void saveToDatabase(Connection connection) throws SQLException {
        // Modify the query to check if a patient with the same name and age already exists
        String checkQuery = "SELECT COUNT(*) FROM patient WHERE name = ? AND age = ?";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setString(1, name);  // Check by name
            checkStmt.setInt(2, age);      // Check by age

            try (ResultSet rs = checkStmt.executeQuery()) {
                rs.next();

                if (rs.getInt(1) == 0) {  // If no such patient exists, insert new record
                    String insertQuery = "INSERT INTO patient (patientID, name, age, contactInfo, medicalHistory, visitRecords, diseaseType, condition1, arrivalTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                        insertStmt.setInt(1, patientID);
                        insertStmt.setString(2, name);
                        insertStmt.setInt(3, age);
                        insertStmt.setString(4, contactInfo);
                        insertStmt.setString(5, medicalHistory);
                        insertStmt.setString(6, visitRecords);
                        insertStmt.setString(7, diseaseType);
                        insertStmt.setString(8, condition1);
                        insertStmt.setTimestamp(9, Timestamp.valueOf(arrivalTime)); // Convert LocalDateTime to Timestamp
                        insertStmt.executeUpdate();
                    }
                } else {
                    System.out.println("Patient with name " + name + " and age " + age + " already exists.");
                }
            }
        }
    }


    // Constructor to initialize the patient object
    public Patient(int patientID) {
        this.patientID = patientID;
    }

    // Method to get patient info from the database
    public String getPatientInfo() {
        Connection connection = DatabaseConnection.getConnection();
        String query = "SELECT * FROM patient WHERE patientID = ?";
        StringBuilder patientInfo = new StringBuilder();

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, patientID); // Set the patientID in the query

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Fetch patient details from the database
                    name = rs.getString("name");
                    age = rs.getInt("age");
                    contactInfo = rs.getString("contactInfo");
                    medicalHistory = rs.getString("medicalHistory");
                    visitRecords = rs.getString("visitRecords");
                    diseaseType = rs.getString("diseaseType");
                    condition1 = rs.getString("condition1");
                    Timestamp timestamp = rs.getTimestamp("arrivalTime");
                    if (timestamp != null) {
                        arrivalTime = timestamp.toLocalDateTime();
                    }


                    // Format the patient's information into a string
                    patientInfo.append("Patient ID: ").append(patientID).append("\n")
                            .append("Name: ").append(name).append("\n")
                            .append("Age: ").append(age).append("\n")
                            .append("Contact Info: ").append(contactInfo).append("\n")
                            .append("Medical History: ").append(medicalHistory).append("\n")
                            .append("Visit Records: ").append(visitRecords).append("\n")
                            .append("Disease Type: ").append(diseaseType).append("\n")
                            .append("Condition: ").append(condition1).append("\n")
                            .append("Arrival Time: ").append(arrivalTime);

                } else {
                    patientInfo.append("No patient found with ID: ").append(patientID);
                }
            }
        } catch (SQLException e) {
            patientInfo.append("Error retrieving patient information: ").append(e.getMessage());
        }

        return patientInfo.toString();
    }

    public int getPatientID() {
        return patientID;
    }
    public String getName() {
        return name;

    }
    public String getDiseaseType() {
        return diseaseType;
    }
    public String getCondition1() {
        return condition1;
    }
    public int getAge() {
        return age;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public String getVisitRecords() {
        return visitRecords;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public String getContactInfo() {
        return contactInfo;
    }
    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}

