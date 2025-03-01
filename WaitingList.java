import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class WaitingList {
    private PriorityQueue waiting; // Ensure you have a proper implementation of PriorityQueue
    private Scanner scanner;

    public WaitingList() {
        this.waiting = new PriorityQueue();
        this.scanner = new Scanner(System.in);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 1

    public void addPatientToWaitList(String patientName, int priority) {
        waiting.enqueue( patientName.toUpperCase(), priority);
        System.out.println("Added to waitlist: " + patientName + " with priority " + priority);
        savePatientToFile(patientName, priority);

    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 2
    public String removePatientFromWaitList() {

         String patientName = waiting.dequeue();
        if (patientName != null) {
            System.out.println("Removed from waitlist: " + patientName );
        } else {
            System.out.println("No patients in the waitlist.");
        }
        return patientName;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // كل واحد
    public void savePatientToFile(String patientName, int priority) {
        try (FileWriter writer = new FileWriter(patientName.toUpperCase() + ".txt")) {
            writer.write("Patient Name: " + patientName + "\n");
            writer.write("Priority: " + priority + "\n");
            writer.write("-------------------------------------------------------------------------------------------------------\n");
        } catch (IOException e) {
            System.err.println("Error writing to patient file: " + e.getMessage());
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 6
    public void deletePatientFile(String patientName) {
        File file = new File(patientName.toUpperCase() + ".txt");
        if (file.delete()) {
            System.out.println("Patient file deleted successfully: " + patientName + ".txt");
        } else {
            System.out.println("Failed to delete the patient file or it does not exist: " + patientName + ".txt");
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void saveWaitingListToFile(String patient, int priority) {
        try (FileWriter writer = new FileWriter("WaitingList.txt", true)) { // Append mode
            writer.write("Patient: " + patient.toUpperCase() + ", Priority: " + priority + "\n");
            writer.write("-------------------------------------------------------------------------------------------------------\n");
        } catch (IOException e) {
            System.err.println("Error writing to waiting list file: " + e.getMessage());
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //4
    public void addMultiplePatientsToWaitList() {
        System.out.println("Enter the number of patients you want to add:");
        int count = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character
        for (int i = 0; i < count; i++) {
            System.out.println("Enter Patient Name:");
            String name = scanner.nextLine().toUpperCase();
            System.out.println("Enter Priority:");
            int priority = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character
            addPatientToWaitList(name, priority);
            saveWaitingListToFile(name, priority);

        }
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //3
      public void DeleteElement(){
        System.out.println("ENTER  NUM   ELEMENT  TO WANT DELETE");
        int d= scanner.nextInt();
        for (int i =0;i<d;i++){
            removePatientFromWaitList();

        }
  System.out.println("DONE ");
       deleteWaitingListFile();
   }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //ALL//3
    public void deleteWaitingListFile() {

        File file = new File("WaitingList.txt");
        if (file.delete()) {
            System.out.println("Waiting list file deleted successfully.");
        } else {
            System.out.println("Failed to delete the waiting list file or it does not exist.");
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //5
    public void readWaitingList() {
        try (Scanner M5 = new Scanner(new File("WaitingList.txt"))) {
            while (M5.hasNextLine()) {
                String data = M5.nextLine();
                System.out.println(data);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Waiting list file not found: " + e.getMessage());
        }
    }
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////