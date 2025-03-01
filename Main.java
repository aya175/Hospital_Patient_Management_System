import java.util.Scanner;

public class Main {
    private static final WaitingList waitingList = new WaitingList();
    private static final NotificationSystem notificationSystem = new NotificationSystem(1000);

    private  static final Billing billing = new Billing("55544", 100);
// اعمل set id اغير

    public static void main(String[] args) {

        Scanner M = new Scanner(System.in);
        int choice;

        do {
            System.out.println("Enter The NUM (1) for WaitingList");
            System.out.println("Enter The NUM (2) for Notification");
            System.out.println("Enter The NUM (3) for Billing");
            System.out.println("Enter The NUM (0) to Exit");
            choice = M.nextInt();

            switch (choice) {
                case 1:
                    handleWaitingList(M);
                    break;
                case 2:
                    handleNotification(M);
                    break;
                case 3:
                    handleBilling(M);
                    break;
                case 0:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        } while (choice != 0);

        M.close();
    }

    private static void handleWaitingList(Scanner scanner) {
        System.out.println("Enter THE NUM (1) to add Patient To WaitList");
        System.out.println("Enter THE NUM (2) to add multiple Patients To WaitList");
        System.out.println("Enter THE NUM (3) delete multiple Patients From WaitList ");
        System.out.println("Enter THE NUM (4) to delete File Patients From WaitList");
        System.out.println("Enter THE NUM (5) to  View File WaitList");
        System.out.println("Enter THE NUM (6) to  delete  Any Patient File");

        int option = scanner.nextInt();

        switch (option) {
            case 1:
                System.out.println("Enter Patient Name:");
                String name = scanner.next();
                System.out.println("Enter Priority :");
                int priority = scanner.nextInt();
                waitingList.addPatientToWaitList(name,priority);
                break;
            case 2:
                waitingList.addMultiplePatientsToWaitList();

                break;
            case 3:
                waitingList.DeleteElement();
                break;

            case 4:
                waitingList.deleteWaitingListFile();

                break;
            case 5:
                waitingList.readWaitingList();
                break;

            case 6:
                System.out.println("Enter The Name ");
                 String NAME7 =scanner.next().toUpperCase();
                waitingList.deletePatientFile(NAME7);
                break;
            default:
                System.out.println("Invalid option, please try again.");
        }
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static void handleNotification(Scanner scanner) {
        System.out.println("Enter Customer Name:");
        String name = scanner.next().toUpperCase();
        System.out.println("Enter THE NUM (1) to send Confirmation");
        System.out.println("Enter THE NUM (2) to notify Availability");
        System.out.println("Enter THE NUM (3) to display Notifications");
        System.out.println("Enter THE NUM (4) to remove Confirmation");
        System.out.println("Enter THE NUM (5) to remove Notify Availability");
        System.out.println("Enter THE NUM (6) to remove All Messages");
        int option = scanner.nextInt();

        switch (option) {
            case 1:
                notificationSystem.sendConfirmation(name);
                break;
            case 2:
                notificationSystem.notifyAvailability(name);
                break;
            case 3:
                notificationSystem.displayNotifications();
                notificationSystem.read();
                break;
            case 4:
                notificationSystem.removeConfirmation();
                break;
            case 5:
                notificationSystem.removeNotifyAvailability(name);
                break;
            case 6:
                notificationSystem.removeALLMASSAGE();
                break;
            default:
                System.out.println("Invalid option, please try again.");
        }
    }



    private static void handleBilling(Scanner scanner) {


        System.out.println("choice");
        System.out.println("Enter THE NUM (1) to add Payment");
        System.out.println("Enter THE NUM (2) to display Payment History");
        System.out.println("Enter THE NUM (3) to check Payment Status");
        System.out.println("Enter THE NUM (4) to Read ONE Payment History From File");
        System.out.println("Enter THE NUM (5) to Read The Existing File  ");
        System.out.println("Enter THE NUM (6) to Remove Payment History");
        System.out.println("Enter THE NUM (7) to Remove ALL Payment History");


        int option = scanner.nextInt();

        switch (option) {
            case 1:

                System.out.println("Enter the amount to add:");
                double amount = scanner.nextDouble();
                billing.addPayment(amount);
                System.out.println("Payment added successfully.");
                break;
            case 2:
                billing.displayPaymentHistory();
                break;
            case 3:
                System.out.println(billing.getPaymentStatus());
                break;
            case 4:
                billing.readPaymentHistoryFromFile();
                break;
            case 5:
                billing.readAllPaymentHistoryFromFile();

                break;
            case 6:
                billing.removePaymentHistory();
                break;
            case 7:
                billing.removeALLMASSAGE();

                break;
            default:
                System.out.println("Invalid option, please try again.");
        }
    }
}