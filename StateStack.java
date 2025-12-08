// ==================== STATE SNAPSHOT ====================
class StateSnapshot {
    SnapshotList heroSnapshots;
    SnapshotList enemySnapshots;
    int roundNumber;
    
    StateSnapshot(HeroLinkedList heroes, EnemyLinkedList enemies, int roundNumber) {
        this.roundNumber = roundNumber;
        heroSnapshots = new SnapshotList();
        enemySnapshots = new SnapshotList();
        
        for (int i = 0; i < heroes.size(); i++) {
            heroSnapshots.insert(new CharacterSnapshot(heroes.get(i)));
        }
        
        for (int i = 0; i < enemies.size(); i++) {
            enemySnapshots.insert(new CharacterSnapshot(enemies.get(i)));
        }
    }
    
    void restore(HeroLinkedList heroes, EnemyLinkedList enemies) {
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            if (i < heroSnapshots.size()) {
                CharacterSnapshot snap = heroSnapshots.get(i);
                h.setCurrentHp(snap.currentHp);
            }
        }
        
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            if (i < enemySnapshots.size()) {
                CharacterSnapshot snap = enemySnapshots.get(i);
                e.setCurrentHp(snap.currentHp);
            }
        }
    }
}

// ==================== STACK FOR STATE HISTORY ====================
class StateStackNode {
    StateSnapshot data;
    StateStackNode next;
    
    StateStackNode(StateSnapshot data) {
        this.data = data;
        this.next = null;
    }
}

class StateStack {
    private StateStackNode top; //bagian atas dari stack
    private int size;
    
    StateStack() {
        top = null;
        size = 0;
    }
    
    void push(StateSnapshot data) {
        StateStackNode newNode = new StateStackNode(data);
        newNode.next = top;
        top = newNode;
        size++;
    }
    
    StateSnapshot pop() {
        if (top == null) return null;
        StateSnapshot data = top.data;
        top = top.next;
        size--;
        return data;
    }
    
    StateSnapshot peek() {
        return top == null ? null : top.data;
    }
    
    boolean isEmpty() {
        return top == null;
    }
    
    int size() {
        return size;
    }
}