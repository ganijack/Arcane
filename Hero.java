// ==================== CHARACTER BASE CLASS ====================
abstract class Character {
    protected String name;
    protected int maxHp;
    protected int currentHp;
    protected int attack;
    protected int speed;
    protected boolean isAlive;
    
    Character(String name, int maxHp, int attack, int speed) {
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.attack = attack;
        this.speed = speed;
        this.isAlive = true;
    }
    
    void takeDamage(int damage) {
        currentHp -= damage;
        if (currentHp <= 0) {
            currentHp = 0;
            isAlive = false;
        }
    }
    
    String getName() { return name; }
    int getCurrentHp() { return currentHp; }
    int getMaxHp() { return maxHp; }
    int getAttack() { return attack; }
    int getSpeed() { return speed; }
    boolean isAlive() { return isAlive; }
    
    void setCurrentHp(int hp) { 
        currentHp = hp;
        isAlive = hp > 0;
    }
    
    void setMaxHp(int hp) {
        maxHp = hp;
    }
    
    void setAttack(int atk) {
        attack = atk;
    }
    
    void setSpeed(int spd) {
        speed = spd;
    }
    
    public String toString() {
        return name + " [HP: " + currentHp + "/" + maxHp + ", ATK: " + attack + ", SPD: " + speed + "]";
    }
}

// ==================== HERO CLASS ====================
class Hero extends Character {
    private int id;
    private SkillTree skillTree;
    
    Hero(int id, String name, int maxHp, int attack, int speed) {
        super(name, maxHp, attack, speed);
        this.id = id;
        this.skillTree = new SkillTree(name);
    }
    
    int getId() { return id; }
    SkillTree getSkillTree() { return skillTree; }
    
    int useSkill(SkillNode skill, Character target) {
        int damage = skill.damage;
        target.takeDamage(damage);
        return damage;
    }
}

// ==================== CHARACTER SNAPSHOT ====================
class CharacterSnapshot {
    String name;
    int currentHp;
    boolean isAlive;
    
    CharacterSnapshot(Character c) {
        this.name = c.getName();
        this.currentHp = c.getCurrentHp();
        this.isAlive = c.isAlive();
    }
}

// ==================== SNAPSHOT LIST ====================
class SnapshotNode {
    CharacterSnapshot data;
    SnapshotNode next;
    
    SnapshotNode(CharacterSnapshot data) {
        this.data = data;
        this.next = null;
    }
}

class SnapshotList {
    private SnapshotNode head;
    private int size;
    
    SnapshotList() {
        head = null;
        size = 0;
    }
    
    void insert(CharacterSnapshot data) {
        SnapshotNode newNode = new SnapshotNode(data);
        if (head == null) {
            head = newNode;
        } else {
            SnapshotNode current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }
    
    CharacterSnapshot get(int index) {
        if (index < 0 || index >= size) return null;
        SnapshotNode current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }
    
    int size() {
        return size;
    }
}

// ==================== CHARACTER SORTING ====================
class CharacterSorter {
    static void mergeSort(Character[] arr, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }
    
    private static void merge(Character[] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        
        Character[] L = new Character[n1];
        Character[] R = new Character[n2];
        
        for (int i = 0; i < n1; i++)
            L[i] = arr[left + i];
        for (int j = 0; j < n2; j++)
            R[j] = arr[mid + 1 + j];
        
        int i = 0, j = 0, k = left;
        
        while (i < n1 && j < n2) {
            if (L[i].getSpeed() >= R[j].getSpeed()) {
                arr[k] = L[i];
                i++;
            } else {
                arr[k] = R[j];
                j++;
            }
            k++;
        }
        
        while (i < n1) {
            arr[k] = L[i];
            i++;
            k++;
        }
        
        while (j < n2) {
            arr[k] = R[j];
            j++;
            k++;
        }
    }
}

// ==================== CHARACTER LINKED LIST ====================
class CharacterNode {
    Character data;
    CharacterNode next;
    
    CharacterNode(Character data) {
        this.data = data;
        this.next = null;
    }
}

class CharacterLinkedList {
    private CharacterNode head;
    private int size;
    
    CharacterLinkedList() {
        head = null;
        size = 0;
    }
    
    void insert(Character data) {
        CharacterNode newNode = new CharacterNode(data);
        if (head == null) {
            head = newNode;
        } else {
            CharacterNode current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }
    
    Character get(int index) {
        if (index < 0 || index >= size) return null;
        CharacterNode current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }
    
    int size() {
        return size;
    }
    
    Character[] toArray() {
        Character[] arr = new Character[size];
        CharacterNode current = head;
        int i = 0;
        while (current != null) {
            arr[i++] = current.data;
            current = current.next;
        }
        return arr;
    }
}