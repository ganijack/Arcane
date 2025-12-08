// ==================== QUEUE FOR ENEMY WAVES ====================
class WaveQueueNode {
    EnemyLinkedList data;
    WaveQueueNode next;
    
    WaveQueueNode(EnemyLinkedList data) {
        this.data = data;
        this.next = null;
    }
}

class WaveQueue {
    private WaveQueueNode front;
    private WaveQueueNode rear;
    private int size;
    
    WaveQueue() {
        front = null;
        rear = null;
        size = 0;
    }
    
    void enqueue(EnemyLinkedList data) {
        WaveQueueNode newNode = new WaveQueueNode(data);
        if (rear == null) {
            front = rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
    }
    
    EnemyLinkedList dequeue() {
        if (front == null) return null;
        EnemyLinkedList data = front.data;
        front = front.next;
        if (front == null) rear = null;
        size--;
        return data;
    }
    
    EnemyLinkedList peek() {
        return front == null ? null : front.data;
    }
    
    boolean isEmpty() {
        return front == null;
    }
    
    int size() {
        return size;
    }
}