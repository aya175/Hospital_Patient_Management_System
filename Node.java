public  class Node {
        String patientName;
        int priority;
        Node next;

        Node(String patientName, int priority) {
            this.patientName = patientName;
            this.priority = priority;
            this.next = null;
        }
    }
