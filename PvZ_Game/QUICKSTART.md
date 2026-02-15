# Quick Start Guide - Plants vs Space Zombies

## 🚀 Getting Started

### For Playing (Desktop)

1. **Install Godot 4.0 or Later**
   - Download from https://godotengine.org/download

2. **Open the Project**
   - Launch Godot
   - Click "Open Project"
   - Navigate to the `PvZ_Game` folder
   - Select `project.godot` and click "Open"

3. **Run the Game**
   - Click the "Play ▶️" button in the top right
   - OR Press `F5`

### For Playing on Android (APK)

See [BUILD_ANDROID.md](BUILD_ANDROID.md) for detailed instructions.

## 🎮 How to Play

### Main Menu
1. See your current **Coins** balance (top-left)
2. Click **"Start Game"** to begin a level
3. Click **"Shop"** to:
   - View available upgrades
   - Buy "Hybrid Pea Launcher" for 500 coins
   - See which upgrades you've unlocked

### In-Game

1. **Select a Plant**
   - Click "Pea Shooter" (50 Sun) - shoots at zombies
   - Click "Cherry Bomb" (150 Sun) - explodes when zombies come near
   - Click "Hybrid Pea" (100 Sun) - if you've bought the upgrade

2. **Place Plants**
   - Click on any empty grid cell
   - Plant will be placed and start defending

3. **Defend Your Lane**
   - Shooting plants damage zombies from a distance
   - Bombs explode when zombies get close
   - Your plants die when their health reaches 0

4. **Win the Level**
   - Kill all 8 zombies without any reaching the left edge
   - More coins are earned than the previous level

### Game Over Conditions

**You WIN when:**
- You eliminate all 8 spawned zombies

**You LOSE when:**
- A zombie reaches the left edge of the screen
- Returns to main menu with coins earned so far

## 💰 Economy

### Sun (In-Game Currency)
- **Start with**: 200 Sun per level
- **Use for**: Buying plants
- **Earned by**: Defeating zombies
- **Not saved**: Resets each level

### Coins (Menu Currency)
- **Earned by**: Beating levels
  - Level 1: 100 coins
  - Level 2: 150 coins
  - Level 3: 200 coins (etc.)
- **Use for**: Shop upgrades
- **Saved**: Persists between sessions
- **View**: Top-left of main menu

## 🌱 Plant Types

| Plant | Cost | Damage | Effect |
|-------|------|--------|--------|
| Pea Shooter | 50 Sun | 15 | Shoots peas at zombies |
| Cherry Bomb | 150 Sun | 300 | Explodes in an area |
| Hybrid Pea* | 100 Sun | 50 | Shoots explosive peas |

*Requires purchasing "Hybrid Pea Launcher" upgrade for 500 coins

## 🧟 Zombie Stats

- **Health**: 100 HP
- **Speed**: Medium (moves from right to left)
- **Attack**: Eats plants (10 damage/sec)
- **Spawn Rate**: 1 every 3 seconds
- **Max per Level**: 8 zombies

## 💡 Strategy Tips

1. **Balance Your Plants**
   - Mix shooting plants and bombs
   - Spread plants across the grid
   - Use expensive plants wisely

2. **Sun Management**
   - Don't waste early Sun on expensive plants
   - Build up your defenses gradually
   - Save Sun for emergency plants

3. **Plant Placement**
   - Place shooting plants in front lanes
   - Put bombs in back lanes
   - Stagger plants vertically for coverage

4. **Upgrade Priority**
   - Buy "Hybrid Pea Launcher" upgrade first
   - It gives you more damage options
   - Makes harder levels easier

## 🔧 Troubleshooting

**Game won't start?**
- Ensure Godot 4.0+ is properly installed
- Check that project.godot is in the correct folder

**Plants aren't shooting?**
- Make sure you have enough Sun
- Check that you clicked the plant type button first
- Plants need zombies in their lane to shoot

**Can't buy upgrade?**
- You need 500 coins for Hybrid Pea Launcher
- Win more levels to earn more coins
- Coins are displayed in top-left of main menu

**Game runs slowly?**
- Lower graphics quality in Godot settings
- Close other applications
- Restart the game

**Save file is missing?**
- Game creates a new save automatically
- Save file stored at: `user://pvz_save.json`
- Delete this file to reset progress

## ⚙️ Settings

There are no in-game settings in this prototype. To modify game parameters, edit:
- `scripts/game.gd` for gameplay values
- `project.godot` for display settings

## 🎯 Achievements Ideas (Not Yet Implemented)

- Beat 5 levels without losing
- Don't spend any Sun
- Use only one plant type per level
- Survive with 1 plant remaining

---

**Need More Help?** See [GAME_DESIGN.md](GAME_DESIGN.md) for detailed game mechanics.
