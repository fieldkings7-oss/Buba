# Plants vs Space Zombies - Game Design Document

## Overview

**Plants vs Space Zombies** is an early prototype of a 2D tower defense game inspired by Plants vs Zombies. The game features a space theme with plants defending against space zombie astronauts.

⚠️ **Status**: Early Prototype - Features are basic but functional

## Core Game Features

### 1. Main Menu
- **Animated Background**: Space-themed with stars
- **Truck Animation**: Freight truck flying across the space background
- **Menu Options**:
  - "Start Game" - Begin a new level
  - "Shop" - Access upgrade shop
- **Coin Display**: Shows current coins earned from previous levels
- **Prototype Notice**: Clearly marked as early prototype

### 2. Shop System (Main Menu)
- **Currency**: Coins (earned by beating levels)
- **Available Upgrades**:
  - **Hybrid Pea Launcher** - 500 Coins
    - Unlocks ability to use the Hybrid Pea plant in-game
    - Shoots explosive cherries dealing 50 damage instead of 15
- **Persistence**: Purchased upgrades are saved automatically
- **Status Display**: Shows which upgrades are unlocked

### 3. In-Game Currency System

#### Sun ☀️
- **Purpose**: Purchase plants during gameplay
- **Starting Amount**: 200 per level
- **Passive Gain**: Zombies drop sun when defeated
- **Plant Costs**:
  - Pea Shooter: 50 Sun
  - Cherry Bomb: 150 Sun
  - Hybrid Pea: 100 Sun (requires unlock from shop)

#### Coins 🪙
- **Purpose**: Buy upgrades in the shop
- **Earning**: Win levels to earn coins
  - Base: 100 coins
  - Bonus: +50 coins per level number
  - Example: Level 1 = 100 coins, Level 2 = 150 coins, etc.
- **Persistence**: Automatically saved to device

### 4. Game Grid
- **Dimensions**: 6 rows × 15 columns
- **Cell Size**: 80×80 pixels
- **Total Screen**: 1280×720 pixels
- **Playable Area**: 1200×480 pixels (offset from edges)

### 5. Plants

#### Pea Shooter 🟢
- **Cost**: 50 Sun
- **Health**: 100 HP
- **Attack**: Shoots peas in a straight line
- **Damage**: 15 per hit
- **Attack Speed**: Every 1.5 seconds
- **Range**: Entire grid width (shoots to the right)
- **Color**: Green square

#### Cherry Bomb 🔴
- **Cost**: 150 Sun
- **Health**: 150 HP
- **Attack**: Explodes when zombies approach
- **Damage**: 300 explosion damage
- **Trigger Range**: 200 pixels
- **Activation Time**: 1.5 seconds after detecting zombie
- **Effect**: Kills or heavily damages all zombies in 300-pixel radius
- **Color**: Red square

#### Hybrid Pea Launcher (Premium - Unlock for 500 coins)
- **Cost**: 100 Sun
- **Health**: 100 HP
- **Attack**: Shoots explosive cherries
- **Damage**: 50 per hit
- **Attack Speed**: Every 1.2 seconds (faster than basic Pea Shooter)
- **Explosion Radius**: 150 pixels (damages nearby zombies)
- **Color**: Green-yellow square
- **Requirement**: Must purchase "Hybrid Pea Launcher" upgrade from shop first

### 6. Enemies

#### Space Astronaut Zombie 👾
- **Health**: 100 HP
- **Speed**: 50 pixels/second (moves left)
- **Attack**: Eats plants (10 damage per second)
- **Engagement**: Stops moving when adjacent to a plant
- **Color**: Gray square
- **Spawn Pattern**: Random rows, spawn from right side (x=1280)
- **Spawning**: One zombie every 3 seconds up to 8 zombies per level

### 7. Game Mechanics

#### Placement
- Click on grid cells to place plants
- Must have sufficient Sun currency
- Can only place one plant per cell (overwrites previous if clicked again)
- Placement is instantaneous

#### Combat
- Peas travel from left to right, hitting the first zombie they encounter
- Cherry Bombs activate passively when a zombie enters range
- Explosive peas trigger secondary explosions on impact
- Zombies stop when they reach a plant and start eating it
- Each plant has separate health

#### Victory Condition
- All 8 zombies spawned and eliminated
- Player survives to the end

#### Defeat Condition
- Any zombie reaches the left edge of the screen (x = 0)
- Player loses immediately upon this condition

#### Progression
- Beating a level grants coins and returns to main menu
- Players can replay levels
- Level counter increments (affects coin rewards)
- No traditional level difficulty increase in this prototype

### 8. Auto-Save System

**Saved Data**:
- **Location**: `user://pvz_save.json`
- **Contents**:
  - Coins balance
  - Unlocked upgrades (Hybrid Pea status)

**Save Triggers**:
- When coins earned (after level completion)
- When upgrade purchased

**Loading**:
- Automatic on game startup
- Creates default save file if none exists (0 coins, no upgrades)

## Controls

| Action | Input |
|--------|-------|
| Select Plant Type | Click "Pea Shooter", "Cherry Bomb", or "Hybrid Pea" buttons |
| Place Plant | Click on grid cell |
| Open Shop | Click "Shop" button in main menu |
| Close Shop | Click "Close" in shop panel |
| Start Game | Click "Start Game" button |
| Buy Upgrade | Click "BUY" in shop for desired upgrade |

## Screen Resolutions

- **Primary**: 1280×720 (16:9 landscape)
- **Android**: Will scale to device resolution maintaining aspect ratio

## File Structure

```
PvZ_Game/
├── project.godot              # Godot project configuration
├── README.md                  # Project overview
├── BUILD_ANDROID.md          # APK build instructions
├── GAME_DESIGN.md            # This file
│
├── scenes/                    # All scene files
│   ├── main_menu.tscn        # Main menu interface
│   ├── game.tscn             # Game level scene
│   ├── pea_shooter.tscn      # Pea Shooter plant
│   ├── cherry_bomb.tscn      # Cherry Bomb plant
│   ├── hybrid_pea.tscn       # Hybrid Pea plant (premium)
│   ├── zombie.tscn           # Space Astronaut Zombie
│   ├── pea.tscn             # Regular pea projectile
│   └── explosive_pea.tscn   # Explosive pea projectile
│
└── scripts/                   # Game logic (GDScript)
    ├── main_menu.gd         # Main menu and shop UI logic
    ├── game.gd              # Core game loop and mechanics
    ├── game_manager.gd      # Game state management (autoload)
    ├── save_manager.gd      # Persistence layer (autoload)
    │
    ├── plant.gd             # Base plant class
    ├── pea_shooter.gd       # Pea Shooter logic
    ├── cherry_bomb.gd       # Cherry Bomb logic
    ├── hybrid_pea.gd        # Hybrid Pea logic
    │
    ├── zombie.gd            # Base zombie class
    │
    ├── pea.gd              # Pea projectile logic
    └── explosive_pea.gd    # Explosive pea logic
```

## Technical Details

### Game Engine
- **Godot 4.0+**
- **Language**: GDScript
- **Platform**: Cross-platform (Windows, Mac, Linux, Android)

### Performance
- **Target FPS**: 60
- **Resolution**: 1280×720
- **Memory Target**: < 512MB (mobile)

### Dependencies
- None (pure Godot, no external plugins)

## Known Limitations (Early Prototype)

1. **No Audio**: No sound effects or background music
2. **Simple Graphics**: Uses colored rectangles instead of sprites
3. **Fixed Difficulty**: No difficulty selection
4. **Limited Content**: Only 2-3 plant types, 1 zombie type
5. **No Animations**: Plants and zombies are static
6. **No Leaderboard**: No score tracking
7. **Limited UI Polish**: Basic buttons and labels
8. **No Tutorial**: No in-game instructions

## Future Enhancement Ideas

- [ ] More plant types (Sunflower, Corn, Melon, etc.)
- [ ] More zombie types (flying zombies, tough zombies, fast zombies)
- [ ] Difficulty/Wave selection
- [ ] Power-ups and special events
- [ ] Sound effects and music
- [ ] Custom graphics/animations
- [ ] Endless mode
- [ ] Level achievements/challenges
- [ ] Multiplayer support
- [ ] Mobile optimizations
- [ ] Leaderboard system
- [ ] Tutorial/in-game help

## Gameplay Loop

```
Main Menu
    ↓
[Start Game] → Game Scene → [Place Plants] → [Zombies Spawn]
    ↓                                              ↓
[Shop] → Purchase Upgrades → [Spawn More Zombies & Battle]
    ↓                                              ↓
    ← ← ← ← ← ← Victory/Defeat → ← [Return to Menu]
```

---

**Version**: 0.1 (Early Prototype)  
**Last Updated**: February 2026
