import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Billing {
    Scanner M =new Scanner(System.in);
    private String patientID;
    private double billingAmount;
    private ArrayList<Payment> paymentHistory;

    private class Payment {
        double amount;
        Date date;

        Payment(double amount) {
            this.amount = amount;
            this.date = new Date();
        }

        @Override
        public String toString() {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            return "Paid: " + amount + " on " + formatter.format(date);
        }
    }

    public Billing(String patientID, double billingAmount) {
        this.patientID = patientID.toUpperCase();
        this.billingAmount = billingAmount;
        this.paymentHistory = new ArrayList<>();
    }


    public double generateBill() {
        return Math.max(billingAmount, 0);
    }

    public void addPayment(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid payment amount.");
            return;
        }
        if (amount > billingAmount) {
            System.out.println("Payment exceeds the outstanding balance. Please enter a valid amount.");
            return;
        }
        paymentHistory.add(new Payment(amount));
        billingAmount -= amount;
        System.out.println("Payment of " + amount + " added successfully.");
        savePaymentToFile(amount);
        DisplayPaymentHistory();
    }

    public String getPaymentStatus() {
        return billingAmount <= 0 ? "Payment complete. No outstanding balance." : "Outstanding balance: " + billingAmount;
    }

    public void displayPaymentHistory() {
        System.out.println("Payment History for Patient ID: " + patientID);
        if (paymentHistory.isEmpty()) {
            System.out.println("No payments made yet.");
        } else {
            for (Payment payment : paymentHistory) {
                System.out.println(payment);
            }
        }
    }

    private void savePaymentToFile(double amount) {
        //يخزن
        try (FileWriter writer = new FileWriter(patientID + "_PaymentHistory.txt", true)) {
            Payment payment = paymentHistory.get(paymentHistory.size() - 1); // Get the last payment added
            writer.write(payment.toString() + "\n");
            writer.write("Current outstanding balance: " + billingAmount + "\n");
            writer.write("-------------------------------------------------------------------------------------------------------\n");
        } catch (IOException e) {
            System.err.println("Error writing to payment history file: " + e.getMessage());
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void readPaymentHistoryFromFile() {
        //يقرا
        System.out.println("ENTER THE ID");
        String NN1=M.next().toUpperCase();
        try (Scanner MM = new Scanner(new File(NN1 + "_PaymentHistory.txt"))) {
            System.out.println("Reading payment history from file:");
            while (MM.hasNextLine()) {
                System.out.println(MM.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.err.println("Payment history file not found: " + e.getMessage());
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //يقرا
    public void readAllPaymentHistoryFromFile() {
        try (Scanner MM = new Scanner(new File("ALL_DISPLAY"+"_PaymentHistory.txt"))) {
            while (MM.hasNextLine()) {
                String data =MM.nextLine();
                System.out.println(data);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Payment history file not found: " + e.getMessage());
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //يخزن
    public void DisplayPaymentHistory() {
        try (FileWriter writer = new FileWriter("ALL_DISPLAY"+"_PaymentHistory.txt", true)) {
            writer.write("Payment History for Patient ID: " + patientID + "\n");


            for (int i = 0; i < paymentHistory.size(); i++) {
                writer.write(paymentHistory.get(i).toString() + "\n");
            }

            writer.write("-------------------------------------------------------------------------------------------------------\n");
        } catch (IOException e) {
            System.err.println("Error writing to ALL_DISPLAY_MASSAGE file: " + e.getMessage());
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void removeALLMASSAGE() {
        try (FileWriter writer = new FileWriter("ALL_DISPLAY"+"_PaymentHistory.txt")) {
            writer.write("");
        } catch (IOException e) {
            System.err.println("Error: Unable to write to file");
        }
    }

    public void removePaymentHistory() {
        System.out.println("ENTER THE ID FILE DELETE");
        String NN2=M.next().toUpperCase();           // دي علشان لو كان لكل واح اسم اضف برضوا ميثود 2
        try (FileWriter writer = new FileWriter(NN2 + "_PaymentHistory.txt")) {
            writer.write("");
        } catch (IOException e) {
            System.err.println("Error: Unable to write to file");
        }
    }
}