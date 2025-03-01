Description:
The Patient Management System organizes patient records and manages appointment
scheduling for a hospital. This system provides efficient access and modification of patient details, ensuring smooth operations for healthcare providers.

Main Functionalities:

1.Patient
•
Attributes: patientID, name, age, contactInfo, medicalHistory, visitRecords
•
Methods: updateContactInfo(), addVisitRecord(), getPatientInfo()
•
Description: Represents a patient, holding all personal and medical information.
•
Data Structure: save the patient data in a Binary Search Tree to be able to search for a patient Quickly.

2.Appointment
•
Attributes: appointmentID, patient, date, time, status
•
Methods: schedule(), cancel(), reschedule()
•
Description: Manages individual appointments, including scheduling, canceling, and rescheduling.

3.WaitingList
•
Attributes: queue (to store waiting patients)
•
Methods: addToWaitList(), removeFromWaitList()
•
Description: Manages the queue of patients waiting for available appointments

4.Billing
•
Attributes: patientID, billingAmount, paymentHistory
•
Methods: generateBill(), addPayment(), getPaymentStatus()
•
Description: Tracks billing and payment history for each patient.

5.ReportGenerator
•
Attributes: reportType, data
•
Methods: generatePatientReport(), generateAppointmentReport(), generateRevenueReport()
•
Description: Generates various reports, such as patient visit history, appointment statistics, and revenue summaries.
•
Sorting Algorithms: Use algorithms like Merge Sort or Quick Sort for organizing data before generating reports, such as patient visit histories or revenue summaries.

6.PatientManagementSystem
•
Attributes: patientList, appointmentQueue, waitingList, billingRecords
•
Methods: addPatient(), findPatient(), scheduleAppointment(), cancelAppointment(), generateReport()
•
Description: The main system class, managing the overall operations and interactions between Patient, Appointment, WaitingList, Billing, and ReportGenerator.

---> Graphical user interface (GUI)
---> Priority queue to manage patients in waiting list
