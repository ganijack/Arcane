import java.util.Scanner;

// ==================== MAIN ====================
public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static HeroLinkedList heroes;
    private static WaveQueue waveQueue;
    private static int playerGold = 0;
    
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
        System.out.println("         _=====_                               _=====_");
        System.out.println("        / _____ \\                             / _____ \\");
        System.out.println("      +.-'_____'-.---------------------------.-'_____'-.+");
        System.out.println("     /   |     |  '.      U N R A M        .'  |  _  |   \\");
        System.out.println("    / ___| /|\\ |___ \\                     / ___| /_\\ |___ \\");
        System.out.println("   / |      |      | ;  __           _   ; | _         _ | ;");
        System.out.println("   | | <---   ---> | | |__|         |_:> | ||_|       (_)| |");
        System.out.println("   | |___   |   ___| ;SELECT       START ; |___       ___| ;");
        System.out.println("   |\\    | \\|/ |    /  _     ___      _   \\    | (X) |    /|");
        System.out.println("   | \\   |_____|  .',\\\" \\\"', |___|  ,'\\\" \\\"', '|_____|  .' |");
        System.out.println("   |  '-.______.-' /       \\ANALOG/       \\  '-._____.-'   |");
        System.out.println("   |               |       |------|       |                |");
        System.out.println("   |              /\\       /      \\       /\\               |");
        System.out.println("   |             /  '.___.'        '.___.'  \\              |");
        System.out.println("   |            /                            \\             |");
        System.out.println("    \\          /                              \\           /");
        System.out.println("     \\________/                                \\_________/");
        System.out.println();
        System.out.println("╔═════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                             ║");
        System.out.println("║  $$$$$$\\                                                    ║");
        System.out.println("║ $$  __$$\\                                                   ║");
        System.out.println("║ $$ /  $$ | $$$$$$\\   $$$$$$$\\ $$$$$$\\  $$$$$$$\\   $$$$$$\\   ║");
        System.out.println("║ $$$$$$$$ |$$  __$$\\ $$  _____|\\____$$\\ $$  __$$\\ $$  __$$\\  ║");
        System.out.println("║ $$  __$$ |$$ |  \\__|$$ /      $$$$$$$ |$$ |  $$ |$$$$$$$$ | ║");
        System.out.println("║ $$ |  $$ |$$ |      $$ |     $$  __$$ |$$ |  $$ |$$   ____| ║");
        System.out.println("║ $$ |  $$ |$$ |      \\$$$$$$$\\\\$$$$$$$ |$$ |  $$ |\\$$$$$$$\\  ║");
        System.out.println("║ \\__|  \\__|\\__|       \\_______|\\_______|\\__|  \\__| \\_______| ║");
        System.out.println("║                                                             ║");
        System.out.println("╚═════════════════════════════════════════════════════════════╝");
        
        // Initialize heroes
        heroes = new HeroLinkedList();
        heroes.insert(new Hero(1, "Aria the Swift", 100, 15, 25));
        heroes.insert(new Hero(2, "Brutus the Tank", 150, 20, 10));
        heroes.insert(new Hero(3, "Celeste the Mage", 80, 25, 20));
        
        playerGold = 200;
    }
    
    private static void displayMainMenu() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║          MAIN MENU                    ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║  [1] View Heroes                      ║");
        System.out.println("║  [2] Upgrade Skills                   ║");
        System.out.println("║  [3] Start Battle                     ║");
        System.out.println("║  [4] Exit Game                        ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.println("Current Gold: " + playerGold);
        System.out.print("\nChoose option: ");
    }
    
    private static void viewHeroes() {
        boolean viewingHeroes = true;
        
        while (viewingHeroes) {
            System.out.println("\n╔═══════════════════════════════════════╗");
            System.out.println("║          YOUR HEROES                  ║");
            System.out.println("╚═══════════════════════════════════════╝\n");
            
            for (int i = 0; i < heroes.size(); i++) {
                Hero h = heroes.get(i);
                System.out.println("🛡️ ID:" + h.getId() + " - " + h);
            }
            
            System.out.println("\n╔═══════════════════════════════════════╗");
            System.out.println("║  [1] Search Hero by ID                ║");
            System.out.println("║  [2] Back to Main Menu                ║");
            System.out.println("╚═══════════════════════════════════════╝");
            System.out.print("Choose option: ");
            
            int choice = getIntInput();
            
            if (choice == 1) {
                searchAndManageHero();
            } else if (choice == 2) {
                viewingHeroes = false;
            } else {
                System.out.println("Invalid choice!");
            }
        }
    }
    
    private static void searchAndManageHero() {
        System.out.print("\n🔍 Enter Hero ID to search: ");
        int searchId = getIntInput();
        
        // Gunakan HeroSearcher untuk mencari hero by ID
        Hero foundHero = HeroSearcher.searchHeroById(heroes, searchId);
        
        if (foundHero == null) {
            System.out.println("\n❌ Hero with ID " + searchId + " not found!");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        // Hero ditemukan, tampilkan detail dan opsi manipulasi
        boolean managingHero = true;
        
        while (managingHero) {
            System.out.println("\n╔══════════════════════════════════════════════════════╗");
            System.out.println("║            HERO STATISTICS                           ║");
            System.out.println("╚══════════════════════════════════════════════════════╝");
            System.out.println("\n🛡️ Hero Found!");
            System.out.println("═════════════════════════════════════════════════════════");
            System.out.println("🆔 ID        : " + foundHero.getId());
            System.out.println("👤 Name      : " + foundHero.getName());
            System.out.println("❤️ HP        : " + foundHero.getCurrentHp() + "/" + foundHero.getMaxHp());
            System.out.println("⚔️ Attack    : " + foundHero.getAttack());
            System.out.println("⚡ Speed     : " + foundHero.getSpeed());
            System.out.println("💚 Status    : " + (foundHero.isAlive() ? "Alive" : "Dead"));
            System.out.println("═════════════════════════════════════════════════════════");
            
            System.out.println("\n╔═══════════════════════════════════════════════════════╗");
            System.out.println("║           MANAGE HERO STATS                           ║");
            System.out.println("╠═══════════════════════════════════════════════════════╣");
            System.out.println("║  [1] Modify Max HP (Cost: 50 Gold)                    ║");
            System.out.println("║  [2] Modify Attack (Cost: 50 Gold)                    ║");
            System.out.println("║  [3] Modify Speed (Cost: 50 Gold)                     ║");
            System.out.println("║  [4] View Skill Tree                                  ║");
            System.out.println("║  [5] Back to Hero List                                ║");
            System.out.println("╚═══════════════════════════════════════════════════════╝");
            System.out.println("💰 Your Gold: " + playerGold);
            System.out.print("Choose option: ");
            
            int action = getIntInput();
            
            switch (action) {
                case 1:
                    modifyMaxHP(foundHero);
                    break;
                case 2:
                    modifyAttack(foundHero);
                    break;
                case 3:
                    modifySpeed(foundHero);
                    break;
                
                case 4:
                    viewHeroSkillTree(foundHero);
                    break;
                case 5:
                    managingHero = false;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
    
    private static void modifyMaxHP(Hero hero) {
    if (playerGold < 50) {
        System.out.println("\n❌ Not enough gold! Need 50 gold.");
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        return;
    }
    
    System.out.println("\n👉 Current Max HP: " + hero.getMaxHp());
    System.out.print("Enter new Max HP (50-500): ");
    int newMaxHP = getIntInput();
    
    if (newMaxHP >= 50 && newMaxHP <= 500) {
        // Set both max HP and current HP to the new value
        hero.setMaxHp(newMaxHP);
        hero.setCurrentHp(newMaxHP);  // Langsung set current HP ke nilai baru
        
        playerGold -= 50;
        System.out.println("\n✅ Max HP updated to " + newMaxHP + "!");
        System.out.println("💚 Current HP also set to " + newMaxHP + "!");
        System.out.println("💰 Remaining Gold: " + playerGold);
    } else {
        System.out.println("\n❌ Invalid HP value! Must be between 50-500.");
    }
    
    System.out.println("\nPress Enter to continue...");
    scanner.nextLine();
}
    
    private static void modifyAttack(Hero hero) {
        if (playerGold < 50) {
            System.out.println("\n❌ Not enough gold! Need 50 gold.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("\n💉 Current Attack: " + hero.getAttack());
        System.out.print("Enter new Attack (5-100): ");
        int newAttack = getIntInput();
        
        if (newAttack >= 5 && newAttack <= 100) {
            hero.setAttack(newAttack);
            playerGold -= 50;
            System.out.println("\n✅ Attack updated to " + newAttack + "!");
            System.out.println("💰 Remaining Gold: " + playerGold);
        } else {
            System.out.println("\n❌ Invalid Attack value! Must be between 5-100.");
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private static void modifySpeed(Hero hero) {
        if (playerGold < 50) {
            System.out.println("\n❌ Not enough gold! Need 50 gold.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("\n💉 Current Speed: " + hero.getSpeed());
        System.out.print("Enter new Speed (5-50): ");
        int newSpeed = getIntInput();
        
        if (newSpeed >= 5 && newSpeed <= 50) {
            hero.setSpeed(newSpeed);
            playerGold -= 50;
            System.out.println("\n✅ Speed updated to " + newSpeed + "!");
            System.out.println("💰 Remaining Gold: " + playerGold);
        } else {
            System.out.println("\n❌ Invalid Speed value! Must be between 5-50.");
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private static void restoreHP(Hero hero) {
        if (playerGold < 30) {
            System.out.println("\n❌ Not enough gold! Need 30 gold.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        int oldHP = hero.getCurrentHp();
        hero.setCurrentHp(hero.getMaxHp());
        int restored = hero.getMaxHp() - oldHP;
        
        playerGold -= 30;
        System.out.println("\n✅ " + hero.getName() + " HP restored!");
        System.out.println("💚 Restored: +" + restored + " HP");
        System.out.println("💰 Remaining Gold: " + playerGold);
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private static void viewHeroSkillTree(Hero hero) {
        System.out.println("\n🌟 " + hero.getName() + "'s Skill Tree:");
        System.out.println("═════════════════════════════════════════════");
        hero.getSkillTree().displayTree(hero.getSkillTree().root, "", true);
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    private static void upgradeSkills() {
        System.out.println("\n╔═══════════════════════════════════════╗");
        System.out.println("║       UPGRADE SKILLS                   ║");
        System.out.println("╚═══════════════════════════════════════╝\n");
        
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
            
            System.out.println("\n[1] Unlock Skill (100 Gold)  [2] Back");
            System.out.print("Choice: ");
            int action = getIntInput();
            
            if (action == 1) {
                if (playerGold >= 100) {
                    scanner.nextLine(); // Clear buffer
                    System.out.print("Enter skill name to unlock: ");
                    String skillName = scanner.nextLine();
                    
                    if (selectedHero.getSkillTree().unlockSkill(skillName)) {
                        playerGold -= 100;
                        System.out.println("✅ " + skillName + " has been unlocked!");
                        System.out.println("💰 Remaining Gold: " + playerGold);
                    } else {
                        System.out.println("❌ Cannot unlock " + skillName + 
                                         ". Parent skill must be unlocked first or skill already unlocked.");
                    }
                } else {
                    System.out.println("❌ Not enough gold! Need 100 gold.");
                }
            }
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        scanner.nextLine();
    }
    
    private static void startBattle() {
        // Beri info tentang event wave
        System.out.println("\n⚠️  WAVE EVENTS INFO:");
        System.out.println("Wave 3: Healing Event - Heroes recover 30% HP");
        System.out.println("Wave 5: Power Event - Heroes +5 damage");
        System.out.println("Wave 7: Treasure Event - Bonus 150 gold");
        System.out.println("Wave 10: Boss Wave - Ultimate challenge!");
        
        System.out.println("\n[1] Start Normal Battle (3 Waves)");
        System.out.println("[2] Start Extended Battle (10 Waves with Events)");
        System.out.println("[3] Back to Menu");
        System.out.print("Choice: ");
        int battleChoice = getIntInput();
        
        if (battleChoice == 3) return;
        
        // Re-initialize waves for new battle
        waveQueue = new WaveQueue();
        
        if (battleChoice == 1) {
            // Normal Battle (3 waves tanpa event)
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
            
        } else if (battleChoice == 2) {
            // Extended Battle (10 waves dengan event)
            System.out.println("\n🎮 Starting Extended Battle with Wave Events!");
            
            // Wave 1-2: Normal
            EnemyLinkedList wave1 = new EnemyLinkedList();
            wave1.insert(new Enemy("Goblin Scout", 40, 8, 15, 1));
            wave1.insert(new Enemy("Goblin Warrior", 50, 10, 12, 1));
            waveQueue.enqueue(wave1);
            
            EnemyLinkedList wave2 = new EnemyLinkedList();
            wave2.insert(new Enemy("Orc Grunt", 60, 12, 14, 2));
            wave2.insert(new Enemy("Orc Archer", 45, 10, 18, 2));
            waveQueue.enqueue(wave2);
            
            // Wave 3: Healing Event Wave
            EnemyLinkedList wave3 = new EnemyLinkedList();
            wave3.insert(new Enemy("Orc Berserker", 70, 15, 16, 3));
            wave3.insert(new Enemy("Orc Shaman", 55, 12, 20, 3));
            waveQueue.enqueue(wave3);
            
            // Wave 4: Normal
            EnemyLinkedList wave4 = new EnemyLinkedList();
            wave4.insert(new Enemy("Dark Knight", 80, 18, 15, 4));
            waveQueue.enqueue(wave4);
            
            // Wave 5: Power Event Wave
            EnemyLinkedList wave5 = new EnemyLinkedList();
            wave5.insert(new Enemy("Elite Guard", 90, 20, 18, 5));
            wave5.insert(new Enemy("Battle Mage", 65, 22, 16, 5));
            waveQueue.enqueue(wave5);
            
            // Wave 6: Normal
            EnemyLinkedList wave6 = new EnemyLinkedList();
            wave6.insert(new Enemy("Ancient Guardian", 100, 25, 12, 6));
            waveQueue.enqueue(wave6);
            
            // Wave 7: Treasure Event Wave
            EnemyLinkedList wave7 = new EnemyLinkedList();
            wave7.insert(new Enemy("Demon Warrior", 110, 28, 20, 7));
            wave7.insert(new Enemy("Shadow Assassin", 75, 30, 25, 7));
            waveQueue.enqueue(wave7);
            
            // Wave 8-9: Normal
            EnemyLinkedList wave8 = new EnemyLinkedList();
            wave8.insert(new Enemy("Abyssal Horror", 120, 32, 15, 8));
            waveQueue.enqueue(wave8);
            
            EnemyLinkedList wave9 = new EnemyLinkedList();
            wave9.insert(new Enemy("Dragonkin", 130, 35, 22, 9));
            waveQueue.enqueue(wave9);
            
            // Wave 10: Boss Wave
            EnemyLinkedList wave10 = new EnemyLinkedList();
            wave10.insert(new Enemy("DRAGON LORD", 200, 40, 18, 10));
            wave10.insert(new Enemy("Dragon Whelp", 50, 15, 25, 10));
            waveQueue.enqueue(wave10);
        }
        
        // Reset heroes HP
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            h.setCurrentHp(h.getMaxHp());
        }
        
        // Reset skill unlock (unlock basic skills)
        for (int i = 0; i < heroes.size(); i++) {
            Hero h = heroes.get(i);
            // Unlock Power Shot untuk semua hero
            h.getSkillTree().unlockSkill("Power Shot");
        }
        
        System.out.println("\n✅ Battle prepared! Starting now...\n");
        
        // Jalankan battle dengan BattleManager yang sudah ada event
        BattleManager battleManager = new BattleManager(heroes, waveQueue);
        battleManager.startBattle();
        
        // Beri reward gold setelah battle
        int reward = (battleChoice == 1) ? 100 : 500;
        playerGold += reward;
        System.out.println("\n💰 Battle Complete! Reward: +" + reward + " Gold");
        System.out.println("💰 Total Gold: " + playerGold);
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
        scanner.nextLine();
    }
    
    private static int getIntInput() {
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("Invalid input. Try again: ");
        }
        int result = scanner.nextInt();
        scanner.nextLine(); // Clear newline
        return result;
    }

}