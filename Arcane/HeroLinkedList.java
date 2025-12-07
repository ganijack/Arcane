// ==================== HERO LINKED LIST ====================
class HeroNode {
    Hero data;
    HeroNode next;
    
    HeroNode(Hero data) {
        this.data = data;
        this.next = null;
    }
}

class HeroLinkedList {
    private HeroNode head;
    private int size;
    
    HeroLinkedList() {
        head = null;
        size = 0;
    }
    
    void insert(Hero data) {
        HeroNode newNode = new HeroNode(data);
        if (head == null) {
            head = newNode;
        } else {
            HeroNode current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }
    
    boolean delete(Hero data) {
        if (head == null) return false;
        
        if (head.data.equals(data)) {
            head = head.next;
            size--;
            return true;
        }
        
        HeroNode current = head;
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
    
    Hero get(int index) {
        if (index < 0 || index >= size) return null;
        HeroNode current = head;
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
        HeroNode current = head;
        while (current != null) {
            System.out.println(current.data);
            current = current.next;
        }
    }
}

// ==================== HERO SEARCHER ====================
class HeroSearcher {
    static Hero linearSearchHero(HeroLinkedList heroes, String name) {
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            if (h.getName().equalsIgnoreCase(name)) {
                return h;
            }
        }
        return null;
    }
    
    static Hero searchHeroById(HeroLinkedList heroes, int id) {
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            if (h.getId() == id) {
                return h;
            }
        }
        return null;
    }
}