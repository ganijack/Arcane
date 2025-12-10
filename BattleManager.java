import java.util.Scanner;

// ==================== BATTLE MANAGER ====================
class BattleManager {
    private HeroLinkedList heroes;
    private EnemyLinkedList currentEnemies;
    private WaveQueue waveQueue;
    private StateStack historyStack;
    private int currentRound;
    private Scanner scanner;
    private int currentWave;
    private int eventBonusDamage;
    private int eventBonusGold;
    
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
        
        currentWave = 1;
        
        if (!waveQueue.isEmpty()) {
            currentEnemies = waveQueue.dequeue();
            System.out.println("Wave " + currentWave + " has arrived!");
            displayEnemies();
            checkWaveEvents(currentWave);
        }
        
        while (true) {
            if (allHeroesDead()) {
                System.out.println("\nGAME OVER! All heroes have fallen...");
                break;
            }
            
            if (currentEnemies.isEmpty() || allEnemiesDead()) {
                if (eventBonusGold > 0) {
                    System.out.println("\nTREASURE BONUS: +" + eventBonusGold + " Gold!");
                    eventBonusGold = 0;
                }
                
                if (waveQueue.isEmpty()) {
                    System.out.println("\nVICTORY! All waves defeated!");
                    break;
                }
                
                currentWave++;
                currentEnemies = waveQueue.dequeue();
                System.out.println("\nWave " + currentWave + " incoming!");
                displayEnemies();
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
        eventBonusDamage = 0;
        
        if (waveNumber == 3) {
            System.out.println("HEALING EVENT ACTIVATED!");
            System.out.println("All heroes recover 30% HP!");
            healAllHeroes(30);
        }
        
        if (waveNumber == 5) {
            System.out.println("POWER BOOST ACTIVATED!");
            System.out.println("Heroes deal +5 damage!");
            eventBonusDamage = 5;
        }
        
        if (waveNumber == 7) {
            System.out.println("TREASURE EVENT!");
            System.out.println("Complete this wave for bonus!");
            eventBonusGold = 150;
        }
        
        if (waveNumber == 10) {
            System.out.println("BOSS WAVE!");
            System.out.println("Defeat the boss!");
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
                System.out.println(h.getName() + " healed +" + healAmount + " HP");
            }
        }
    }
    
    // ==================== SISTEM ROUND 1:1 ====================
    private void playRound() {
        currentRound++;
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("               ROUND " + currentRound);
        System.out.println("══════════════════════════════════════════");
        
        // Save state before round
        historyStack.push(new StateSnapshot(heroes, currentEnemies, currentRound));
        
        // SISTEM BARU: Setiap hero yang menyerang, enemy langsung membalas
        // Step 1: Hero pilih aksi
        for (int i = 0; i < heroes.size(); i++) {
            Hero hero = heroes.get(i);
            if (!hero.isAlive() || allEnemiesDead()) continue;
            
            System.out.println("\n--- " + hero.getName() + "'s Action ---");
            System.out.println(hero);
            
            // Tampilkan damage bonus jika ada
            if (eventBonusDamage > 0) {
                System.out.println("Power Boost Active: +" + eventBonusDamage + " damage!");
            }
            
            System.out.println("\nChoose action:");
            System.out.println("[1] Attack");
            System.out.println("[2] Skip");
            System.out.print("Choice: ");
            
            int action = getIntInput();
            
            if (action == 1) {
                // Hero menyerang
                executeHeroAttack(hero);
                
                // SETIAP HERO SERANG, ENEMY LANGSUNG BALAS (jika masih ada enemy hidup)
                if (hasAliveEnemy() && !allHeroesDead()) {
                    executeEnemyCounterAttack();
                }
            } else {
                System.out.println(hero.getName() + " skips turn.");
            }
            
            // Cek jika battle selesai
            if (allEnemiesDead() || allHeroesDead()) break;
        }
        
        // Step 2: Enemy yang belum sempat balas (jika hero lebih sedikit dari enemy)
        if (!allEnemiesDead() && !allHeroesDead()) {
            int remainingEnemies = countAliveEnemies() - countHeroActionsTaken();
            if (remainingEnemies > 0) {
                System.out.println("\n--- Remaining Enemy Attacks ---");
                for (int i = 0; i < remainingEnemies && !allHeroesDead(); i++) {
                    executeEnemyAttack();
                }
            }
        }
        
        displayBattleStatus();
    }
    
    
    private void executeHeroAttack(Hero hero) {
  
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
                    validTargets++;
                    System.out.println("[" + validTargets + "] " + e);
                }
            }
            
            if (validTargets == 0) {
                System.out.println("No enemies to target!");
                return;
            }
            
            System.out.print("Target: ");
            int targetChoice = getIntInput() - 1;
            
            
            int targetIndex = -1;
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
            
            if (targetIndex != -1) {
                Enemy target = currentEnemies.get(targetIndex);
                int totalDamage = selectedSkill.damage + eventBonusDamage;
                
                target.takeDamage(totalDamage);
                System.out.println("\n" + hero.getName() + " uses " + 
                                 selectedSkill.skillName + " on " + target.getName() + 
                                 " for " + totalDamage + " damage!");
                
                if (!target.isAlive()) {
                    System.out.println(target.getName() + " has been defeated!");
                }
            }
        }
    }
    
    private void executeEnemyCounterAttack() {
        
        Enemy counterAttacker = findAliveEnemy();
        if (counterAttacker == null) return;
        
        Hero target = findHeroWithLowestHP();
        if (target == null) return;
        
        int damage = counterAttacker.getAttack();
        
        if (currentWave == 5) {
            damage += 3;
            System.out.println("Enemy empowered!");
        }
        
        target.takeDamage(damage);
        System.out.println(counterAttacker.getName() + " counterattacks " + 
                         target.getName() + " for " + damage + " damage!");
        
        if (!target.isAlive()) {
            System.out.println(target.getName() + " has fallen!");
        }
    }
    
    private void executeEnemyAttack() {
     
        Enemy attacker = findAliveEnemy();
        if (attacker == null) return;
        
        Hero target = findHeroWithLowestHP();
        if (target == null) return;
        
        int damage = attacker.getAttack();
        if (currentWave == 5) {
            damage += 3;
        }
        
        target.takeDamage(damage);
        System.out.println(attacker.getName() + " attacks " + 
                         target.getName() + " for " + damage + " damage!");
        
        if (!target.isAlive()) {
            System.out.println(target.getName() + " has fallen!");
        }
    }
    
    // ==================== HELPER METHODS ====================
    private int countHeroActionsTaken() {
        // Hitung berapa hero yang melakukan attack (bukan skip)
        // Untuk sistem sederhana, anggap semua hero yang alive menyerang
        return countAliveHeroes();
    }
    
    private Enemy findAliveEnemy() {
        for (int i = 0; i < currentEnemies.size(); i++) {
            Enemy e = currentEnemies.get(i);
            if (e != null && e.isAlive()) {
                return e;
            }
        }
        return null;
    }
    
    private Hero findHeroWithLowestHP() {
        Hero lowestHPHero = null;
        int lowestHP = Integer.MAX_VALUE;
        
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            if (h != null && h.isAlive() && h.getCurrentHp() < lowestHP) {
                lowestHP = h.getCurrentHp();
                lowestHPHero = h;
            }
        }
        
        return lowestHPHero;
    }
    
    private void undoRound() {
        if (historyStack.isEmpty()) {
            System.out.println("\nNo previous round to undo!");
            return;
        }
        
        StateSnapshot snapshot = historyStack.pop();
        snapshot.restore(heroes, currentEnemies);
        currentRound = snapshot.roundNumber - 1;
        
        System.out.println("\nRound " + snapshot.roundNumber + " has been undone!");
        displayBattleStatus();
    }
    
    private void displayBattleStatus() {
        System.out.println("\n══════════════════════════════════════════");
        System.out.println("            BATTLE STATUS");
        System.out.println("══════════════════════════════════════════");
        System.out.println("\nHEROES:");
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            System.out.println("  " + h + (h.isAlive() ? " (Alive)" : " (Dead)"));
        }
        
        System.out.println("\nENEMIES:");
        for (int i = 0; i < currentEnemies.size(); i++) {
            Enemy e = currentEnemies.get(i);
            if (e.isAlive()) {
                System.out.println("  " + e + " (Alive)");
            }
        }
        
        // Tampilkan event aktif
        if (eventBonusDamage > 0) {
            System.out.println("\nACTIVE EVENT: Power Boost (+" + eventBonusDamage + " damage)");
        }
    }
    
    private void displayEnemies() {
        System.out.println("\nEnemies in this wave:");
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
    
    private boolean hasAliveEnemy() {
        return !allEnemiesDead();
    }
    
    private int countAliveHeroes() {
        int count = 0;
        for (int i = 0; i < heroes.size(); i++) {
            if (heroes.get(i).isAlive()) count++;
        }
        return count;
    }
    
    private int countAliveEnemies() {
        int count = 0;
        for (int i = 0; i < currentEnemies.size(); i++) {
            if (currentEnemies.get(i).isAlive()) count++;
        }
        return count;
    }
    
    private int getIntInput() {
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("Invalid input. Try again: ");
        }
        return scanner.nextInt();
    }
}