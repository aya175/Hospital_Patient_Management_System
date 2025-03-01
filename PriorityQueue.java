public class PriorityQueue {
    private Node front;

    public PriorityQueue() {
        this.front = null;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void enqueue(String patientName, int priority) {
        Node newNode = new Node(patientName, priority);

        // دور priorityQueue

        if (front == null || front.priority > priority) {
            newNode.next = front;
            front = newNode;
        } else {
            Node current = front;
            while (current.next != null && current.next.priority <= priority) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    public String dequeue() {
        if (front == null) {
            throw new IllegalStateException("Priority Queue is empty");
        }
        String patientName = front.patientName;

        front = front.next;
        return patientName;



    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean isEmpty() {
        return front == null;
    }


    /////////////////////////
    public int peek() {
        if (front == null) {
            throw new IllegalStateException("Priority Queue is empty");
        }
        return front.priority;
    }

}