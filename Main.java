import java.util.Scanner;

// ==================== MAIN ====================
public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static HeroLinkedList heroes;
    private static WaveQueue waveQueue;
    
    public static void main(String[] args) {
        initializeGame();
        
        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    viewHeroes();
                    break;
                case 2:
                    upgradeSkills();
                    break;
                case 3:
                    startBattle();
                    break;
                case 4:
                    running = false;
                    System.out.println("\n👋 Thanks for playing Arcane Battle Rush!");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
    
    private static void initializeGame() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║                                        ║");
        System.out.println("║     ARCANE BATTLE RUSH                 ║");
        System.out.println("║     Turn-Based RPG Battle              ║");
        System.out.println("║                                        ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        // Initialize heroes
        heroes = new HeroLinkedList();
        heroes.insert(new Hero(1, "Aria the Swift", 100, 15, 25));
        heroes.insert(new Hero(2, "Brutus the Tank", 150, 20, 10));
        heroes.insert(new Hero(3, "Celeste the Mage", 80, 25, 20));
        
        // Initialize waves
        waveQueue = new WaveQueue();
        
        // Wave 1
        EnemyLinkedList wave1 = new EnemyLinkedList();
        wave1.insert(new Enemy("Goblin Scout", 40, 8, 15, 1));
        wave1.insert(new Enemy("Goblin Warrior", 50, 10, 12, 1));
        waveQueue.enqueue(wave1);
        
        // Wave 2
        EnemyLinkedList wave2 = new EnemyLinkedList();
        wave2.insert(new Enemy("Orc Berserker", 70, 15, 18, 2));
        wave2.insert(new Enemy("Orc Shaman", 60, 12, 20, 2));
        wave2.insert(new Enemy("Orc Grunt", 55, 10, 14, 2));
        waveQueue.enqueue(wave2);
        
        // Wave 3 (Boss)
        EnemyLinkedList wave3 = new EnemyLinkedList();
        wave3.insert(new Enemy("Dark Knight", 120, 20, 22, 3));
        waveQueue.enqueue(wave3);
        
        System.out.println("✓ Game initialized with " + heroes.size() + " heroes");
        System.out.println("✓ " + waveQueue.size() + " waves prepared\n");
    }
    
    private static void displayMainMenu() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║          MAIN MENU                     ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║  [1] View Heroes                       ║");
        System.out.println("║  [2] Upgrade Skills                    ║");
        System.out.println("║  [3] Start Battle                      ║");
        System.out.println("║  [4] Exit Game                         ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.print("Choose option: ");
    }
    
    private static void viewHeroes() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║          YOUR HEROES                   ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            System.out.println("🛡️ " + h);
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        scanner.nextLine();
    }
    
    private static void upgradeSkills() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║       UPGRADE SKILLS                   ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        System.out.println("Select a hero:");
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            System.out.println("[" + (i + 1) + "] " + h.getName());
        }
        System.out.print("Choice: ");
        int heroChoice = getIntInput() - 1;
        
        if (heroChoice >= 0 && heroChoice < heroes.size()) {
            Hero selectedHero = heroes.get(heroChoice);
            System.out.println("\n🌟 " + selectedHero.getName() + "'s Skill Tree:");
            selectedHero.getSkillTree().displayTree(
                selectedHero.getSkillTree().root, "", true);
            
            System.out.println("\n[1] Unlock Skill  [2] Back");
            System.out.print("Choice: ");
            int action = getIntInput();
            
            if (action == 1) {
                scanner.nextLine(); // Clear buffer
                System.out.print("Enter skill name to unlock: ");
                String skillName = scanner.nextLine();
                
                if (selectedHero.getSkillTree().unlockSkill(skillName)) {
                    System.out.println("✓ " + skillName + " has been unlocked!");
                } else {
                    System.out.println("✗ Cannot unlock " + skillName + 
                                     ". Parent skill must be unlocked first or skill already unlocked.");
                }
            }
        }
    }
    
    private static void startBattle() {
        // Re-initialize waves for new battle
        waveQueue = new WaveQueue();
        
        EnemyLinkedList wave1 = new EnemyLinkedList();
        wave1.insert(new Enemy("Goblin Scout", 40, 8, 15, 1));
        wave1.insert(new Enemy("Goblin Warrior", 50, 10, 12, 1));
        waveQueue.enqueue(wave1);
        
        EnemyLinkedList wave2 = new EnemyLinkedList();
        wave2.insert(new Enemy("Orc Berserker", 70, 15, 18, 2));
        wave2.insert(new Enemy("Orc Shaman", 60, 12, 20, 2));
        wave2.insert(new Enemy("Orc Grunt", 55, 10, 14, 2));
        waveQueue.enqueue(wave2);
        
        EnemyLinkedList wave3 = new EnemyLinkedList();
        wave3.insert(new Enemy("Dark Knight", 120, 20, 22, 3));
        waveQueue.enqueue(wave3);
        
        // Reset heroes HP
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            h.setCurrentHp(h.getMaxHp());
        }
        
        BattleManager battleManager = new BattleManager(heroes, waveQueue);
        battleManager.startBattle();
    }
    
    private static int getIntInput() {
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("Invalid input. Try again: ");
        }
        int result = scanner.nextInt();
        return result;
    }
}