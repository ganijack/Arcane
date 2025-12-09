import java.util.Scanner;

// ==================== BATTLE MANAGER ====================
class BattleManager {
    private HeroLinkedList heroes;
    private EnemyLinkedList currentEnemies;
    private WaveQueue waveQueue;
    private StateStack historyStack;
    private int currentRound;
    private Scanner scanner;
    private int currentWave; // Tambah ini
    private int eventBonusDamage; // Untuk event damage boost
    private int eventBonusGold; // Untuk event gold bonus
    
    BattleManager(HeroLinkedList heroes, WaveQueue waveQueue) {
        this.heroes = heroes;
        this.waveQueue = waveQueue;
        this.historyStack = new StateStack();
        this.currentRound = 0;
        this.scanner = new Scanner(System.in);
        this.currentEnemies = new EnemyLinkedList();
        this.currentWave = 0;
        this.eventBonusDamage = 0;
        this.eventBonusGold = 0;
    }
    
    void startBattle() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║          BATTLE START!                 ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        currentWave = 1; // Reset wave counter
        
        if (!waveQueue.isEmpty()) {
            currentEnemies = waveQueue.dequeue();
            System.out.println("🌊 Wave " + currentWave + " has arrived!");
            displayEnemies();
            
            // Cek event untuk wave pertama
            checkWaveEvents(currentWave);
        }
        
        while (true) {
            if (allHeroesDead()) {
                System.out.println("\n💀 GAME OVER! All heroes have fallen...");
                break;
            }
            
            if (currentEnemies.isEmpty() || allEnemiesDead()) {
                // Apply treasure bonus gold jika ada
                if (eventBonusGold > 0) {
                    System.out.println("\n💰 TREASURE BONUS: +" + eventBonusGold + " Gold!");
                    eventBonusGold = 0; // Reset
                }
                
                if (waveQueue.isEmpty()) {
                    System.out.println("\n🎉 VICTORY! All waves defeated!");
                    break;
                }
                
                // Pindah ke wave berikutnya
                currentWave++;
                currentEnemies = waveQueue.dequeue();
                System.out.println("\n🌊 Wave " + currentWave + " incoming!");
                displayEnemies();
                
                // Cek event untuk wave baru
                checkWaveEvents(currentWave);
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
    
    // ==================== WAVE EVENT SYSTEM ====================
    private void checkWaveEvents(int waveNumber) {
        eventBonusDamage = 0; // Reset damage bonus setiap wave
        
        // Event Healing - Wave 3
        if (waveNumber == 3) {
            System.out.println("\n✨✨✨✨✨✨✨✨✨✨✨✨");
            System.out.println("   HEALING EVENT ACTIVATED!");
            System.out.println("   All heroes recover 30% HP!");
            System.out.println("✨✨✨✨✨✨✨✨✨✨✨✨");
            
            healAllHeroes(30);
        }
        
        // Event Power Boost - Wave 5
        if (waveNumber == 5) {
            System.out.println("\n⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡");
            System.out.println("   POWER BOOST ACTIVATED!");
            System.out.println("   Heroes deal +5 damage!");
            System.out.println("⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡");
            
            eventBonusDamage = 5;
        }
        
        // Event Treasure - Wave 7
        if (waveNumber == 7) {
            System.out.println("\n💰💰💰💰💰💰💰💰💰💰💰💰");
            System.out.println("   TREASURE EVENT!");
            System.out.println("   Complete this wave for bonus!");
            System.out.println("💰💰💰💰💰💰💰💰💰💰💰💰");
            
            eventBonusGold = 150;
        }
        
        // Event Boss - Wave 10
        if (waveNumber == 10) {
            System.out.println("\n👑👑👑👑👑👑👑👑👑👑👑👑");
            System.out.println("   BOSS WAVE!");
            System.out.println("   Defeat the boss!");
            System.out.println("👑👑👑👑👑👑👑👑👑👑👑👑");
        }
    }
    
    private void healAllHeroes(int percent) {
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            if (h.isAlive()) {
                int healAmount = (h.getMaxHp() * percent) / 100;
                int newHp = h.getCurrentHp() + healAmount;
                if (newHp > h.getMaxHp()) {
                    newHp = h.getMaxHp();
                }
                h.setCurrentHp(newHp);
                System.out.println("❤️ " + h.getName() + " healed +" + healAmount + " HP");
            }
        }
    }
    
    // ==================== BATTLE ROUND ====================
    private void playRound() {
        currentRound++;
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("               ROUND " + currentRound);
        System.out.println("══════════════════════════════════════════");
        
        // Save state before round
        historyStack.push(new StateSnapshot(heroes, currentEnemies, currentRound));
        
        // Combine all alive characters
        CharacterLinkedList turnOrder = new CharacterLinkedList();
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            if (h.isAlive()) turnOrder.insert(h);
        }
        for (int i = 0; i < currentEnemies.size(); i++) {
            Enemy e = currentEnemies.get(i);
            if (e.isAlive()) turnOrder.insert(e);
        }
        
        // Sort by speed
        Character[] turnArray = turnOrder.toArray();
        CharacterSorter.mergeSort(turnArray, 0, turnArray.length - 1);
        
        System.out.println("\n⚡ Turn Order (by Speed):");
        for (int i = 0; i < turnArray.length; i++) {
            System.out.println((i + 1) + ". " + turnArray[i]);
        }
        
        // Execute turns
        for (int i = 0; i < turnArray.length; i++) {
            Character c = turnArray[i];
            if (!c.isAlive()) continue;
            
            System.out.println("\n──────── " + c.getName() + "'s turn ────────");
            
            if (c instanceof Hero) {
                heroTurn((Hero) c);
            } else if (c instanceof Enemy) {
                enemyTurn((Enemy) c);
            }
            
            if (allEnemiesDead() || allHeroesDead()) break;
        }
        
        displayBattleStatus();
    }
    
    private void heroTurn(Hero hero) {
        System.out.println(hero);
        
        // Tampilkan damage bonus jika ada
        if (eventBonusDamage > 0) {
            System.out.println("⚡ Power Boost Active: +" + eventBonusDamage + " damage!");
        }
        
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
                int totalDamage = skill.damage + eventBonusDamage;
                System.out.println("[" + (i + 1) + "] " + skill.skillName + 
                                 " (Damage: " + totalDamage + ")");
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
                
                if (validTargets == 0) {
                    System.out.println("No enemies to target!");
                    return;
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
                    // Apply damage bonus dari event
                    int totalDamage = selectedSkill.damage + eventBonusDamage;
                    target.takeDamage(totalDamage);
                    
                    System.out.println("\n🗡️ " + hero.getName() + " uses " + 
                                     selectedSkill.skillName + " on " + target.getName() + 
                                     " for " + totalDamage + " damage!");
                    
                    if (!target.isAlive()) {
                        System.out.println("💀 " + target.getName() + " has been defeated!");
                    }
                }
            }
        }
    }
    
    private void enemyTurn(Enemy enemy) {
        // Build list of alive heroes
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
            
            // Jika wave 5 (Power Event), enemy lebih kuat
            if (currentWave == 5) {
                damage += 3;
                System.out.println("⚡ Enemy empowered!");
            }
            
            target.takeDamage(damage);
            
            System.out.println("👹 " + enemy.getName() + " attacks " + 
                             target.getName() + " for " + damage + " damage!");
            
            if (!target.isAlive()) {
                System.out.println("💀 " + target.getName() + " has fallen!");
            }
        }
    }
    
    // ==================== HELPER METHODS ====================
    private void undoRound() {
        if (historyStack.isEmpty()) {
            System.out.println("\n⚠️ No previous round to undo!");
            return;
        }
        
        StateSnapshot snapshot = historyStack.pop();
        snapshot.restore(heroes, currentEnemies);
        currentRound = snapshot.roundNumber - 1;
        
        System.out.println("\n↩️ Round " + snapshot.roundNumber + " has been undone!");
        displayBattleStatus();
    }
    
    private void displayBattleStatus() {
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("            BATTLE STATUS");
        System.out.println("══════════════════════════════════════════");
        System.out.println("\n🛡️ HEROES:");
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            System.out.println("  " + h + (h.isAlive() ? " ✅" : " ❌"));
        }
        
        System.out.println("\n👹 ENEMIES:");
        for (int i = 0; i < currentEnemies.size(); i++) {
            Enemy e = currentEnemies.get(i);
            if (e.isAlive()) {
                System.out.println("  " + e + " ✅");
            }
        }
        
        // Tampilkan event aktif
        if (eventBonusDamage > 0) {
            System.out.println("\n⚡ ACTIVE EVENT: Power Boost (+" + eventBonusDamage + " damage)");
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
}