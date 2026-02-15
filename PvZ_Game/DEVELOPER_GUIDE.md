# 👨‍💻 Developer Guide - How to Extend the Game

## Overview

This guide helps you understand and modify the Plants vs Space Zombies codebase.

---

## Architecture Overview

```
┌─────────────────────────────────────┐
│     Main Menu (main_menu.gd)        │
│  - Displays coins                   │
│  - Shop UI                          │
│  - Navigation                       │
└──────────────┬──────────────────────┘
               │
    ┌──────────┴─────────┬────────────┐
    │                    │            │
┌───▼──────────┐  ┌──────▼──┐  ┌──────▼──────┐
│  Game Scene  │  │  SaveMgr │  │ GameManager │
│  (game.gd)   │  │          │  │             │
│              │  │ - Coins  │  │ - Game state│
│ Core loop    │  │ - Upgrades  │             │
└──┬───────────┘  └──────────┘  └─────────────┘
   │
   ├─ Plants
   │  ├─ pea_shooter.gd
   │  ├─ cherry_bomb.gd
   │  └─ hybrid_pea.gd
   │
   ├─ Projectiles
   │  ├─ pea.gd
   │  └─ explosive_pea.gd
   │
   └─ Zombies
      └─ zombie.gd
```

---

## Key Files & Their Purpose

### Scripts/

| File | Purpose | Lines |
|------|---------|-------|
| `main_menu.gd` | Menu UI, Shop | ~200 |
| `game.gd` | Game loop, Grid, Spawning | ~350 |
| `game_manager.gd` | Game state autoload | ~40 |
| `save_manager.gd` | Persistence autoload | ~50 |
| `plant.gd` | Base plant class | ~30 |
| `pea_shooter.gd` | Pea Shooter logic | ~40 |
| `cherry_bomb.gd` | Cherry Bomb logic | ~40 |
| `hybrid_pea.gd` | Hybrid Pea logic | ~40 |
| `zombie.gd` | Zombie logic | ~60 |
| `pea.gd` | Projectile logic | ~30 |
| `explosive_pea.gd` | Explosive projectile | ~30 |

**Total**: ~850 lines of GDScript

---

## How to Add a New Plant

### Step 1: Create the GDScript File

Create `scripts/sunflower.gd`:

```gdscript
extends Plant

var produce_timer: float = 0.0
var produce_cooldown: float = 8.0

func _ready():
	hp = 80

func _process(delta):
	produce_timer += delta
	if produce_timer >= produce_cooldown:
		produce_sun()
		produce_timer = 0.0

func produce_sun():
	if game_scene:
		game_scene.add_sun(25)

func get_plant_name() -> String:
	return "Sunflower"
```

### Step 2: Create the Scene File

Create `scenes/sunflower.tscn`:

```
[gd_scene load_steps=2 format=3 uid="uid://unique_id"]

[ext_resource type="Script" path="res://scripts/sunflower.gd"]

[node name="Sunflower" type="Node2D"]
script = ExtResource("res://scripts/sunflower.gd")

[node name="ColorRect" type="ColorRect" parent="."]
offset_left = -20.0
offset_top = -20.0
offset_right = 20.0
offset_bottom = 20.0
color = Color(1, 1, 0, 1)
```

### Step 3: Add to Game UI

Edit `game.gd`, in `create_ui()` function:

```gdscript
var sunflower_btn = Button.new()
sunflower_btn.text = "Sunflower\n(40 Sun)"
sunflower_btn.position = Vector2(1100, 20)
sunflower_btn.size = Vector2(150, 60)
sunflower_btn.pressed.connect(_on_sunflower_selected)
ui_container.add_child(sunflower_btn)
```

### Step 4: Add Selection Handler

In `game.gd`, add:

```gdscript
func _on_sunflower_selected():
	selected_plant = "sunflower"
```

### Step 5: Add Planting Logic

In `plant_on_grid()` function:

```gdscript
elif selected_plant == "sunflower":
	cost = 40
	plant_path = "res://scenes/sunflower.tscn"
```

---

## How to Add a New Zombie Type

### Step 1: Create Zombie Script

Create `scripts/flying_zombie.gd`:

```gdscript
extends Zombie

func _ready():
	hp = 150
	speed = 75.0  # Faster

func _process(delta):
	if is_eating and target_plant:
		eat_plant(delta)
	else:
		move(delta)
		# Flying zombies hover
		position.y = 150 + row * 80 + sin(Time.get_ticks_msec() / 500.0) * 5

func get_zombie_name() -> String:
	return "Flying Space Zombie"
```

### Step 2: Create Scene

Create `scenes/flying_zombie.tscn` (similar to zombie.tscn)

### Step 3: Spawn in Game

Edit `game.gd`, modify `spawn_zombie()`:

```gdscript
func spawn_zombie():
	var zombie_types = [
		"res://scenes/zombie.tscn",
		"res://scenes/flying_zombie.tscn"
	]
	var random_zombie = zombie_types[randi() % zombie_types.size()]
	var z = load(random_zombie).instantiate()
	# ... rest of code
```

---

## How to Add Sound Effects

### Step 1: Add AudioStreamPlayer

Edit a plant script (e.g., `pea_shooter.gd`):

```gdscript
func shoot():
	if pea_scene:
		var pea = pea_scene.instantiate()
		pea.position = position
		pea.row = row
		game_scene.add_child(pea)
		
		# Play sound
		var audio = AudioStreamPlayer.new()
		audio.stream = load("res://assets/sounds/shoot.mp3")
		audio.bus = "SFX"  # Optional: use audio bus
		add_child(audio)
		audio.play()
```

### Step 2: Create Audio Assets

Place `.mp3` or `.ogg` files in `res://assets/sounds/`

---

## How to Modify Game Parameters

### Difficulty Settings

Edit `game.gd`:

```gdscript
# Zombie spawning
var zombie_spawn_interval: float = 2.0  # Spawn faster
var max_zombies: int = 15  # More zombies per level

# Starting resources
const STARTING_SUN = 300  # More sun to start
```

### Plant Costs

Edit `game.gd`, in `plant_on_grid()`:

```gdscript
if selected_plant == "pea":
	cost = 40  # Changed from 50
```

### Zombie Health

Edit `game.gd`, in `spawn_zombie()`:

```gdscript
z.hp = 150  # Changed from 100
```

### Plant Damage

Edit respective plant script:

```gdscript
# pea_shooter.gd
var pea_scene.damage = 20  # Changed from 15
```

---

## How to Add an Upgrade

### Step 1: Update SaveManager

Edit `save_manager.gd`:

```gdscript
var upgrades: Dictionary = {
	"hybrid_pea": false,
	"triple_shot": false  # New upgrade
}

func buy_upgrade(upgrade_key: String) -> bool:
	if upgrade_key == "triple_shot":
		if coins >= 750:
			coins -= 750
			upgrades["triple_shot"] = true
			save_game()
			return true
	# ... existing code
```

### Step 2: Create Upgrade Plant

Create a new plant script that uses the upgrade:

```gdscript
# Scripts for triple_shot_pea.gd
extends Plant

func _ready():
	hp = 100

func shoot():
	if game_scene:
		# Shoot 3 peas instead of 1
		for i in range(-1, 2):
			var offset = i * 40
			# Create pea at offset positions
```

### Step 3: Add to Shop

Edit `main_menu.gd`, in `create_shop_panel()`:

```gdscript
var triple_text = Label.new()
triple_text.text = "Triple Shot (750 Coins)"
vbox.add_child(triple_text)

var triple_btn = Button.new()
triple_btn.text = "BUY"
triple_btn.pressed.connect(_on_buy_triple)
vbox.add_child(triple_btn)

# Add method
func _on_buy_triple():
	if save_manager.buy_upgrade("triple_shot"):
		update_upgrade_status()
```

---

## Common Debugging Tips

### Print Variables
```gdscript
print("Sun: ", game_scene.sun)
print("Zombies alive: ", game_scene.zombies.size())
print("Coins: ", SaveManager.coins)
```

### Check Collisions
```gdscript
# In zombie.gd
func move(delta):
	position.x -= speed * delta
	print("Zombie at x=", position.x, " row=", row)
```

### Test Plant Logic
```gdscript
# In plant.gd
func take_damage(amount: int):
	print(get_plant_name(), " took ", amount, " damage")
	hp -= amount
	if hp <= 0:
		die()
```

---

## Performance Optimization Tips

1. **Reduce Object Count**
   - Limit max zombies per level
   - Remove off-screen projectiles faster

2. **Use Object Pooling**
   - Reuse pea objects instead of creating new ones
   - Pre-allocate zombie objects

3. **Optimize Physics**
   - Use faster collision detection (AABB instead of circle)
   - Reduce update frequency for distant objects

4. **Memory Management**
   - Call `queue_free()` on deleted objects
   - Avoid circular references

Example optimization:

```gdscript
# In game.gd - Reuse projectiles
var pea_pool = []

func get_pea():
	if pea_pool.size() > 0:
		return pea_pool.pop_front()
	else:
		return load("res://scenes/pea.tscn").instantiate()

func return_pea(pea):
	pea.hide()
	pea_pool.append(pea)
```

---

## Testing Your Modifications

### Quick Test Checklist
- [ ] Plant places correctly
- [ ] Plant shoots/acts correctly
- [ ] Zombies still spawn
- [ ] Collision detection works
- [ ] Game doesn't crash
- [ ] FPS stays above 30
- [ ] Save system works

### Debug Build
```bash
# Run with debug logging
godot --debug
```

---

## Code Style Guidelines

```gdscript
# Use snake_case for variables and functions
var my_variable = 0
func my_function():
	pass

# Use PascalCase for classes
class MyClass:
	pass

# Use UPPER_CASE for constants
const MAX_ZOMBIES = 8

# Comment complex logic
# Player defeated all zombies
if zombies_spawned >= max_zombies and zombies.size() == 0:
	complete_level()
```

---

## File Organization

**Keep it organized:**
```
scripts/
├── core/  (game logic)
│   ├── game.gd
│   ├── game_manager.gd
│   └── save_manager.gd
├── plants/
│   ├── plant.gd (base)
│   ├── pea_shooter.gd
│   ├── cherry_bomb.gd
│   └── hybrid_pea.gd
├── enemies/
│   └── zombie.gd
├── projectiles/
│   ├── pea.gd
│   └── explosive_pea.gd
└── ui/
    └── main_menu.gd
```

---

## Useful Godot API

```gdscript
# Position & Area
position = Vector2(x, y)
distance_to(other_pos)
get_tree().get_all_nodes_in_group("plants")

# Random
randi() % 10  # Random int 0-9
randf_range(min, max)  # Random float

# Timers
await get_tree().create_timer(seconds).timeout

# Signals
signal my_signal
my_signal.emit()
my_signal.connect(func():
    pass)

# Process
_process(delta)  # Every frame
_physics_process(delta)  # Physics updates
```

---

## Resources

- **Godot Docs**: https://docs.godotengine.org/
- **GDScript Docs**: https://docs.godotengine.org/en/stable/tutorials/scripting/gdscript/
- **Node Reference**: https://docs.godotengine.org/en/stable/classes/
- **Example Projects**: https://github.com/godotengine/godot-examples

---

## Questions?

Check these files:
- [GAME_DESIGN.md](GAME_DESIGN.md) - Game mechanics
- [project.godot](project.godot) - Project configuration
- Individual script files - Inline comments

---

**Happy Developing! 🚀**

Version: 1.0  
Last Updated: February 2026
