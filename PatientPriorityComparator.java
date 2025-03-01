import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;

public class PatientPriorityComparator implements Comparator<Patient> {

    private Connection connection;

    // Constructor that accepts a database connection (if needed)
    public PatientPriorityComparator(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int compare(Patient p1, Patient p2) {
        // 1. Sort by disease type (Full Burn > Partial Burn > Cold)
        int diseasePriority1 = getDiseasePriority(p1.getDiseaseType());
        int diseasePriority2 = getDiseasePriority(p2.getDiseaseType());

        if (diseasePriority1 != diseasePriority2) {
            return diseasePriority2 - diseasePriority1; // The higher priority comes first
        }

        // 2. Sort by condition (Critical > Serious > Stable)
        int conditionPriority1 = getConditionPriority(p1.getCondition1());
        int conditionPriority2 = getConditionPriority(p2.getCondition1());

        if (conditionPriority1 != conditionPriority2) {
            return conditionPriority2 - conditionPriority1; // The higher priority comes first
        }

        // 3. If disease and condition are the same, sort by arrival time (earliest first)
        return compareDateTime(p1, p2);
    }

    private int getDiseasePriority(String disease) {
        switch (disease.toLowerCase()) {
            case "full burn": return 3;
            case "partial burn": return 2;
            case "cold": return 1;
            default: return 0;
        }
    }

    private int getConditionPriority(String condition1) {
        switch (condition1.toLowerCase()) {
            case "critical": return 3;
            case "serious": return 2;
            case "stable": return 1;
            default: return 0;
        }
    }

    // Method to compare arrival times from the database (if required)
    private int compareDateTime(Patient p1, Patient p2) {
        try {
            // Query to select both patient appointment timestamps at once
            String query = "SELECT patientID, dateTime FROM appointment WHERE patientID IN (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);

            // Set patient IDs as the parameters for the query
            stmt.setInt(1, p1.getPatientID());
            stmt.setInt(2, p2.getPatientID());

            ResultSet rs = stmt.executeQuery();

            java.sql.Timestamp dateTime1 = null;
            java.sql.Timestamp dateTime2 = null;

            // Loop through the result set to assign the timestamps to each patient
            while (rs.next()) {
                int patientID = rs.getInt("patientID");
                java.sql.Timestamp dateTime = rs.getTimestamp("dateTime");

                if (patientID == p1.getPatientID()) {
                    dateTime1 = dateTime;
                } else if (patientID == p2.getPatientID()) {
                    dateTime2 = dateTime;
                }
            }

            // If both timestamps are present, compare them
            if (dateTime1 != null && dateTime2 != null) {
                return dateTime1.compareTo(dateTime2);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // If unable to retrieve timestamps, fallback to default (return 0 for equality)
        return 0;
    } }