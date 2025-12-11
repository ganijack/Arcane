// ==================== SKILL NODE ====================
class SkillNode {
    String skillName;
    int damage;
    boolean unlocked;
    SkillNodeLinkedList children;
    
    SkillNode(String skillName, int damage, boolean unlocked) {
        this.skillName = skillName;
        this.damage = damage;
        this.unlocked = unlocked;
        this.children = new SkillNodeLinkedList();
    }
    
    void addChild(SkillNode child) {
        children.insert(child);
    }
}

// ==================== SKILL NODE LINKED LIST ====================
class SkillListNode {
    SkillNode data;
    SkillListNode next;
    
    SkillListNode(SkillNode data) {
        this.data = data;
        this.next = null;
    }
}

class SkillNodeLinkedList {
    private SkillListNode head;
    private int size;
    
    SkillNodeLinkedList() {
        head = null;
        size = 0;
    }
    
    void insert(SkillNode data) {
        SkillListNode newNode = new SkillListNode(data);
        if (head == null) {
            head = newNode;
        } else {
            SkillListNode current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }
    
    SkillNode get(int index) {
        if (index < 0 || index >= size) return null;
        SkillListNode current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }
    
    int size() {
        return size;
    }
}

// ==================== SKILL TREE ====================
class SkillTree {
    SkillNode root;
    
    SkillTree(String heroName) {
        // Root = Basic Attack (always unlocked)
        root = new SkillNode("Basic Attack", 10, true);
        
        // Level 1 skills
        SkillNode powerShot = new SkillNode("Power Shot", 25, false);
        SkillNode quickDash = new SkillNode("Quick Dash", 15, false);
        
        root.addChild(powerShot);
        root.addChild(quickDash);
        
        // Level 2 skills
        SkillNode explosiveShot = new SkillNode("Explosive Shot", 40, false);
        powerShot.addChild(explosiveShot);
    }
    
    void displayTree(SkillNode node, String prefix, boolean isLast) {
        if (node == null) return;
        
        System.out.print(prefix);
        System.out.print(isLast ? "└── " : "├── ");
        System.out.println(node.skillName + " (DMG: " + node.damage + ") " + 
                          (node.unlocked ? "[UNLOCKED]" : "[LOCKED]"));
        
        for (int i = 0; i < node.children.size(); i++) {
            SkillNode child = node.children.get(i);
            displayTree(child, prefix + (isLast ? "    " : "│   "), 
                       i == node.children.size() - 1);
        }
    }
    
    boolean unlockSkill(String skillName) {
        return unlockSkillRecursive(root, skillName.trim());
    }
    
    private boolean unlockSkillRecursive(SkillNode node, String skillName) {
        if (node == null) return false;
        
        for (int i = 0; i < node.children.size(); i++) {
            SkillNode child = node.children.get(i);
            if (child.skillName.equalsIgnoreCase(skillName)) {
                if (node.unlocked && !child.unlocked) {
                    child.unlocked = true;
                    return true;
                }
                return false;
            }
            if (unlockSkillRecursive(child, skillName)) {
                return true;
            }
        }
        return false;
    }
    
    SkillNode findSkill(String skillName) {
        return findSkillRecursive(root, skillName);
    }
    
    private SkillNode findSkillRecursive(SkillNode node, String skillName) {
        if (node == null) return null;
        if (node.skillName.equals(skillName)) return node;
        
        for (int i = 0; i < node.children.size(); i++) {
            SkillNode result = findSkillRecursive(node.children.get(i), skillName);
            if (result != null) return result;
        }
        return null;
    }
    
    SkillNodeLinkedList getUnlockedSkills() {
        SkillNodeLinkedList unlocked = new SkillNodeLinkedList();
        collectUnlockedSkills(root, unlocked);
        return unlocked;
    }
    
    private void collectUnlockedSkills(SkillNode node, SkillNodeLinkedList list) {
        if (node == null) return;
        if (node.unlocked) {
            list.insert(node);
        }
        for (int i = 0; i < node.children.size(); i++) {
            collectUnlockedSkills(node.children.get(i), list);
        }
    }
}