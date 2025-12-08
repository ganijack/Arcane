import java.util.Scanner;

// ==================== BATTLE MANAGER ====================
//anjayy mabarrr
class BattleManager {
    private HeroLinkedList heroes;
    private EnemyLinkedList currentEnemies;
    private WaveQueue waveQueue;
    private StateStack historyStack;
    private int currentRound;
    private Scanner scanner;
    
    BattleManager(HeroLinkedList heroes, WaveQueue waveQueue) {
        this.heroes = heroes;
        this.waveQueue = waveQueue;
        this.historyStack = new StateStack();
        this.currentRound = 0;
        this.scanner = new Scanner(System.in);
        this.currentEnemies = new EnemyLinkedList();
    }
    
    void startBattle() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║     BATTLE START!                      ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        if (!waveQueue.isEmpty()) {
            currentEnemies = waveQueue.dequeue();
            System.out.println("🌊 Wave 1 has arrived!");
            displayEnemies();
        }
        
        while (true) {
            if (allHeroesDead()) {
                System.out.println("\n💀 GAME OVER! All heroes have fallen...");
                break;
            }
            
            if (currentEnemies.isEmpty() || allEnemiesDead()) {
                if (waveQueue.isEmpty()) {
                    System.out.println("\n🎉 VICTORY! All waves defeated!");
                    break;
                }
                currentEnemies = waveQueue.dequeue();
                System.out.println("\n🌊 Next wave incoming!");
                displayEnemies();
            }
            
            playRound();
            
            System.out.println("\n[1] Continue  [2] Undo Last Round  [3] Quit Battle");
            System.out.print("Choose: ");
            int choice = getIntInput();
            
            if (choice == 2) {
                undoRound();
            } else if (choice == 3) {
                System.out.println("Retreating from battle...");
                break;
            }
        }
    }
    
    private void playRound() {
        currentRound++;
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("          ROUND " + currentRound);
        System.out.println("═══════════════════════════════════════");
        
        // Save state before round
        historyStack.push(new StateSnapshot(heroes, currentEnemies, currentRound));
        
        // Combine all alive characters into CharacterLinkedList
        CharacterLinkedList turnOrder = new CharacterLinkedList();
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            if (h.isAlive()) turnOrder.insert(h);
        }
        for (int i = 0; i < currentEnemies.size(); i++) {
            Enemy e = currentEnemies.get(i);
            if (e.isAlive()) turnOrder.insert(e);
        }
        
        // Convert to array and sort by speed (descending)
        Character[] turnArray = turnOrder.toArray();
        CharacterSorter.mergeSort(turnArray, 0, turnArray.length - 1);
        
        System.out.println("\n🎯 Turn Order (by Speed):");
        for (int i = 0; i < turnArray.length; i++) {
            System.out.println((i + 1) + ". " + turnArray[i]);
        }
        
        // Execute turns
        for (int i = 0; i < turnArray.length; i++) {
            Character c = turnArray[i];
            if (!c.isAlive()) continue;
            
            System.out.println("\n--- " + c.getName() + "'s turn ---");
            
            if (c instanceof Hero) {
                heroTurn((Hero) c);
            } else if (c instanceof Enemy) {
                enemyTurn((Enemy) c);
            }
            
            // Check win/lose condition
            if (allEnemiesDead() || allHeroesDead()) break;
        }
        
        displayBattleStatus();
    }
    
    private void heroTurn(Hero hero) {
        System.out.println(hero);
        System.out.println("\nChoose action:");
        System.out.println("[1] Use Skill");
        System.out.println("[2] Skip Turn");
        System.out.print("Choice: ");
        
        int action = getIntInput();
        
        if (action == 1) {
            SkillNodeLinkedList skills = hero.getSkillTree().getUnlockedSkills();
            
            System.out.println("\nAvailable Skills:");
            for (int i = 0; i < skills.size(); i++) {
                SkillNode skill = skills.get(i);
                System.out.println("[" + (i + 1) + "] " + skill.skillName + 
                                 " (Damage: " + skill.damage + ")");
            }
            System.out.print("Select skill: ");
            int skillChoice = getIntInput() - 1;
            
            if (skillChoice >= 0 && skillChoice < skills.size()) {
                SkillNode selectedSkill = skills.get(skillChoice);
                
                System.out.println("\nSelect target:");
                int validTargets = 0;
                for (int i = 0; i < currentEnemies.size(); i++) {
                    Enemy e = currentEnemies.get(i);
                    if (e.isAlive()) {
                        System.out.println("[" + (++validTargets) + "] " + e);
                    }
                }
                
                System.out.print("Target: ");
                int targetChoice = getIntInput() - 1;
                
                int targetIndex = 0;
                int count = 0;
                for (int i = 0; i < currentEnemies.size(); i++) {
                    if (currentEnemies.get(i).isAlive()) {
                        if (count == targetChoice) {
                            targetIndex = i;
                            break;
                        }
                        count++;
                    }
                }
                
                Enemy target = currentEnemies.get(targetIndex);
                if (target != null && target.isAlive()) {
                    int damage = hero.useSkill(selectedSkill, target);
                    System.out.println("\n⚔️ " + hero.getName() + " uses " + 
                                     selectedSkill.skillName + " on " + target.getName() + 
                                     " for " + damage + " damage!");
                    
                    if (!target.isAlive()) {
                        System.out.println("💀 " + target.getName() + " has been defeated!");
                    }
                }
            }
        }
    }
    
    private void enemyTurn(Enemy enemy) {
        // Build list of alive heroes manually
        Hero[] aliveHeroes = new Hero[heroes.size()];
        int count = 0;
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            if (h.isAlive()) {
                aliveHeroes[count++] = h;
            }
        }
        
        if (count > 0) {
            Hero target = aliveHeroes[(int)(Math.random() * count)];
            int damage = enemy.getAttack();
            target.takeDamage(damage);
            
            System.out.println("🗡️ " + enemy.getName() + " attacks " + 
                             target.getName() + " for " + damage + " damage!");
            
            if (!target.isAlive()) {
                System.out.println("💀 " + target.getName() + " has fallen!");
            }
        }
    }
    
    private void undoRound() {
        if (historyStack.isEmpty()) {
            System.out.println("\n❌ No previous round to undo!");
            return;
        }
        
        StateSnapshot snapshot = historyStack.pop();
        snapshot.restore(heroes, currentEnemies);
        currentRound = snapshot.roundNumber - 1;
        
        System.out.println("\n⏪ Round " + snapshot.roundNumber + " has been undone!");
        displayBattleStatus();
    }
    
    private void displayBattleStatus() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("          BATTLE STATUS");
        System.out.println("═══════════════════════════════════════");
        System.out.println("\n💥 HEROES:");
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            System.out.println("  " + h + (h.isAlive() ? " ✓" : " ✗"));
        }
        
        System.out.println("\n👹 ENEMIES:");
        for (int i = 0; i < currentEnemies.size(); i++) {
            Enemy e = currentEnemies.get(i);
            if (e.isAlive()) {
                System.out.println("  " + e + " ✓");
            }
        }
    }
    
    private void displayEnemies() {
        System.out.println("\n👹 Enemies in this wave:");
        for (int i = 0; i < currentEnemies.size(); i++) {
            System.out.println("  " + currentEnemies.get(i));
        }
    }
    
    private boolean allHeroesDead() {
        for (int i = 0; i < heroes.size(); i++) {
            if (heroes.get(i).isAlive()) return false;
        }
        return true;
    }
    
    private boolean allEnemiesDead() {
        for (int i = 0; i < currentEnemies.size(); i++) {
            if (currentEnemies.get(i).isAlive()) return false;
        }
        return true;
    }
    
    private int getIntInput() {
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("Invalid input. Try again: ");
        }
        return scanner.nextInt();
    }
=======
import java.util.Scanner;

// ==================== BATTLE MANAGER ====================
//anjayy mabarrr wimar
class BattleManager {
    private HeroLinkedList heroes;
    private EnemyLinkedList currentEnemies;
    private WaveQueue waveQueue;
    private StateStack historyStack;
    private int currentRound;
    private Scanner scanner;
    
    BattleManager(HeroLinkedList heroes, WaveQueue waveQueue) {
        this.heroes = heroes;
        this.waveQueue = waveQueue;
        this.historyStack = new StateStack();
        this.currentRound = 0;
        this.scanner = new Scanner(System.in);
        this.currentEnemies = new EnemyLinkedList();
    }
    
    void startBattle() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║     BATTLE START!                      ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        if (!waveQueue.isEmpty()) {
            currentEnemies = waveQueue.dequeue();
            System.out.println("🌊 Wave 1 has arrived!");
            displayEnemies();
        }
        
        while (true) {
            if (allHeroesDead()) {
                System.out.println("\n💀 GAME OVER! All heroes have fallen...");
                break;
            }
            
            if (currentEnemies.isEmpty() || allEnemiesDead()) {
                if (waveQueue.isEmpty()) {
                    System.out.println("\n🎉 VICTORY! All waves defeated!");
                    break;
                }
                currentEnemies = waveQueue.dequeue();
                System.out.println("\n🌊 Next wave incoming!");
                displayEnemies();
            }
            
            playRound();
            
            System.out.println("\n[1] Continue  [2] Undo Last Round  [3] Quit Battle");
            System.out.print("Choose: ");
            int choice = getIntInput();
            
            if (choice == 2) {
                undoRound();
            } else if (choice == 3) {
                System.out.println("Retreating from battle...");
                break;
            }
        }
    }
    
    private void playRound() {
        currentRound++;
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("          ROUND " + currentRound);
        System.out.println("═══════════════════════════════════════");
        
        // Save state before round
        historyStack.push(new StateSnapshot(heroes, currentEnemies, currentRound));
        
        // Combine all alive characters into CharacterLinkedList
        CharacterLinkedList turnOrder = new CharacterLinkedList();
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            if (h.isAlive()) turnOrder.insert(h);
        }
        for (int i = 0; i < currentEnemies.size(); i++) {
            Enemy e = currentEnemies.get(i);
            if (e.isAlive()) turnOrder.insert(e);
        }
        
        // Convert to array and sort by speed (descending)
        Character[] turnArray = turnOrder.toArray();
        CharacterSorter.mergeSort(turnArray, 0, turnArray.length - 1);
        
        // --- BAGIAN YANG DIUBAH (Hanya Tampilkan Hero) ---
        System.out.println("\n🎯 Turn Order (by Speed):");
        int displayCount = 1;
        for (int i = 0; i < turnArray.length; i++) {
            if (turnArray[i] instanceof Hero) {
                System.out.println(displayCount + ". " + turnArray[i]);
                displayCount++;
            }
        }
        // ------------------------------------------------
        
        // Execute turns (Logic tetep jalan untuk semua character termasuk Enemy)
        for (int i = 0; i < turnArray.length; i++) {
            Character c = turnArray[i];
            if (!c.isAlive()) continue;
            
            System.out.println("\n--- " + c.getName() + "'s turn ---");
            
            if (c instanceof Hero) {
                heroTurn((Hero) c);
            } else if (c instanceof Enemy) {
                enemyTurn((Enemy) c);
            }
            
            // Check win/lose condition
            if (allEnemiesDead() || allHeroesDead()) break;
        }
        
        displayBattleStatus();
    }
    
    private void heroTurn(Hero hero) {
        System.out.println(hero);
        System.out.println("\nChoose action:");
        System.out.println("[1] Use Skill");
        System.out.println("[2] Skip Turn");
        System.out.print("Choice: ");
        
        int action = getIntInput();
        
        if (action == 1) {
            SkillNodeLinkedList skills = hero.getSkillTree().getUnlockedSkills();
            
            System.out.println("\nAvailable Skills:");
            for (int i = 0; i < skills.size(); i++) {
                SkillNode skill = skills.get(i);
                System.out.println("[" + (i + 1) + "] " + skill.skillName + 
                                 " (Damage: " + skill.damage + ")");
            }
            System.out.print("Select skill: ");
            int skillChoice = getIntInput() - 1;
            
            if (skillChoice >= 0 && skillChoice < skills.size()) {
                SkillNode selectedSkill = skills.get(skillChoice);
                
                System.out.println("\nSelect target:");
                int validTargets = 0;
                for (int i = 0; i < currentEnemies.size(); i++) {
                    Enemy e = currentEnemies.get(i);
                    if (e.isAlive()) {
                        System.out.println("[" + (++validTargets) + "] " + e);
                    }
                }
                
                System.out.print("Target: ");
                int targetChoice = getIntInput() - 1;
                
                int targetIndex = 0;
                int count = 0;
                for (int i = 0; i < currentEnemies.size(); i++) {
                    if (currentEnemies.get(i).isAlive()) {
                        if (count == targetChoice) {
                            targetIndex = i;
                            break;
                        }
                        count++;
                    }
                }
                
                Enemy target = currentEnemies.get(targetIndex);
                if (target != null && target.isAlive()) {
                    int damage = hero.useSkill(selectedSkill, target);
                    System.out.println("\n⚔️ " + hero.getName() + " uses " + 
                                     selectedSkill.skillName + " on " + target.getName() + 
                                     " for " + damage + " damage!");
                    
                    if (!target.isAlive()) {
                        System.out.println("💀 " + target.getName() + " has been defeated!");
                    }
                }
            }
        }
    }
    
    private void enemyTurn(Enemy enemy) {
        // Build list of alive heroes manually
        Hero[] aliveHeroes = new Hero[heroes.size()];
        int count = 0;
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            if (h.isAlive()) {
                aliveHeroes[count++] = h;
            }
        }
        
        if (count > 0) {
            Hero target = aliveHeroes[(int)(Math.random() * count)];
            int damage = enemy.getAttack();
            target.takeDamage(damage);
            
            System.out.println("🗡️ " + enemy.getName() + " attacks " + 
                             target.getName() + " for " + damage + " damage!");
            
            if (!target.isAlive()) {
                System.out.println("💀 " + target.getName() + " has fallen!");
            }
        }
    }
    
    private void undoRound() {
        if (historyStack.isEmpty()) {
            System.out.println("\n❌ No previous round to undo!");
            return;
        }
        
        StateSnapshot snapshot = historyStack.pop();
        snapshot.restore(heroes, currentEnemies);
        currentRound = snapshot.roundNumber - 1;
        
        System.out.println("\n⏪ Round " + snapshot.roundNumber + " has been undone!");
        displayBattleStatus();
    }
    
    private void displayBattleStatus() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("          BATTLE STATUS");
        System.out.println("═══════════════════════════════════════");
        System.out.println("\n💥 HEROES:");
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            System.out.println("  " + h + (h.isAlive() ? " ✓" : " ✗"));
        }
        
        System.out.println("\n👹 ENEMIES:");
        for (int i = 0; i < currentEnemies.size(); i++) {
            Enemy e = currentEnemies.get(i);
            if (e.isAlive()) {
                System.out.println("  " + e + " ✓");
            }
        }
    }
    
    private void displayEnemies() {
        System.out.println("\n👹 Enemies in this wave:");
        for (int i = 0; i < currentEnemies.size(); i++) {
            System.out.println("  " + currentEnemies.get(i));
        }
    }
    
    private boolean allHeroesDead() {
        for (int i = 0; i < heroes.size(); i++) {
            if (heroes.get(i).isAlive()) return false;
        }
        return true;
    }
    
    private boolean allEnemiesDead() {
        for (int i = 0; i < currentEnemies.size(); i++) {
            if (currentEnemies.get(i).isAlive()) return false;
        }
        return true;
    }
    
    private int getIntInput() {
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("Invalid input. Try again: ");
        }
        return scanner.nextInt();
    }
>>>>>>> 208478233914e4152380f3c165dbbf8a53492591:Arcane/BattleManager.java
}