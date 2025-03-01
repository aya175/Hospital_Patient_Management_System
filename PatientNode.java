public class PatientNode {
    private Patient patient;
    private PatientNode left, right;

    public PatientNode(Patient patient) {
        this.patient = patient;
        this.left = null;
        this.right = null;
    }

    public Patient getp() {
        return patient;
    }

    public PatientNode getLeft() {
        return left;
    }

    public void setLeft(PatientNode left) {
        this.left = left;
    }

    public PatientNode getRight() {
        return right;
    }

    public void setRight(PatientNode right) {
        this.right = right;
    }

    public Patient getPatient() {
        return patient;
    }
}
