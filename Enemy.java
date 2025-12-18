// ==================== ENEMY CLASS ====================
class Enemy extends Character {
    private int waveNumber;
    
    Enemy(String name, int maxHp, int attack, int speed, int waveNumber) {
        super(name, maxHp, attack, speed);
        this.waveNumber = waveNumber;
    }
    
    int getWaveNumber() { return waveNumber; }
    
    void attackHero(Hero target) {
        target.takeDamage(attack);
    }
}

// ==================== ENEMY LINKED LIST ====================
class EnemyNode {
    Enemy data;
    EnemyNode next;
    
    EnemyNode(Enemy data) {
        this.data = data;
        this.next = null;
    }
}

class EnemyLinkedList {
    private EnemyNode head;
    private int size;
    
    EnemyLinkedList() {
        head = null;
        size = 0;
    }
    
    void insert(Enemy data) {
        EnemyNode newNode = new EnemyNode(data);
        if (head == null) {
            head = newNode;
        } else {
            EnemyNode current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }
    
    boolean delete(Enemy data) {
        if (head == null) return false;
        
        if (head.data.equals(data)) {
            head = head.next;
            size--;
            return true;
        }
        
        EnemyNode current = head;
        while (current.next != null) {
            if (current.next.data.equals(data)) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }
    
    Enemy get(int index) {
        if (index < 0 || index >= size) return null;
        EnemyNode current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }
    
    int size() {
        return size;
    }
    
    boolean isEmpty() {
        return size == 0;
    }
    
    void traverse() {
        EnemyNode current = head;
        while (current != null) {
            System.out.println(current.data);
            current = current.next;
        }
    }
}
// ==================== ENEMY SEARCHER ====================
class EnemySearcher {
    static Enemy linearSearchEnemy(EnemyLinkedList enemies, String name) {
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            if (e.getName().equalsIgnoreCase(name)) {
                return e;
            }
        }
        return null;
    }
}