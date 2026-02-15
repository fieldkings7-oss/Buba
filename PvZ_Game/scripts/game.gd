extends Node2D

const GRID_COLS = 15
const GRID_ROWS = 6
const CELL_SIZE = 80
const STARTING_SUN = 200

var game_manager: Node
var save_manager: Node
var sun: int = STARTING_SUN
var selected_plant: String = ""
var plants: Array = []
var zombies: Array = []
var zombie_spawn_timer: float = 0.0
var zombie_spawn_interval: float = 3.0
var level: int = 1
var zombies_spawned: int = 0
var max_zombies: int = 8
var game_over: bool = false
var level_complete: bool = false

var ui_label: Label
var sun_label: Label
var level_label: Label

func _ready():
	game_manager = get_node("/root/GameManager")
	save_manager = get_node("/root/SaveManager")
	
	# Create UI
	create_ui()
	
	# Setup plants
	setup_plants()
	
	# Setup grid visualization
	draw_grid()

func create_ui():
	var ui_container = CanvasLayer.new()
	add_child(ui_container)
	
	sun_label = Label.new()
	sun_label.text = "Sun: %d" % sun
	sun_label.position = Vector2(50, 20)
	ui_container.add_child(sun_label)
	
	level_label = Label.new()
	level_label.text = "Level: %d" % level
	level_label.position = Vector2(50, 60)
	ui_container.add_child(level_label)
	
	# Plant selection buttons
	var plant_y = 20
	
	var pea_btn = Button.new()
	pea_btn.text = "Pea Shooter\n(50 Sun)"
	pea_btn.position = Vector2(500, plant_y)
	pea_btn.size = Vector2(150, 60)
	pea_btn.pressed.connect(_on_pea_selected)
	ui_container.add_child(pea_btn)
	
	var cherry_btn = Button.new()
	cherry_btn.text = "Cherry Bomb\n(150 Sun)"
	cherry_btn.position = Vector2(700, plant_y)
	cherry_btn.size = Vector2(150, 60)
	cherry_btn.pressed.connect(_on_cherry_selected)
	ui_container.add_child(cherry_btn)
	
	if save_manager.has_upgrade("hybrid_pea"):
		var hybrid_btn = Button.new()
		hybrid_btn.text = "Hybrid Pea\n(100 Sun)"
		hybrid_btn.position = Vector2(900, plant_y)
		hybrid_btn.size = Vector2(150, 60)
		hybrid_btn.pressed.connect(_on_hybrid_selected)
		ui_container.add_child(hybrid_btn)

func setup_plants():
	pass

func draw_grid():
	var color = Color(0.3, 0.3, 0.3, 0.2)
	for row in range(GRID_ROWS):
		for col in range(GRID_COLS):
			var x = col * CELL_SIZE + 100
			var y = row * CELL_SIZE + 150
			draw_rect(Rect2(x, y, CELL_SIZE, CELL_SIZE), color, false)

func _process(delta):
	queue_redraw()
	
	if game_over or level_complete:
		return
	
	# Spawn zombies
	zombie_spawn_timer += delta
	if zombie_spawn_timer >= zombie_spawn_interval and zombies_spawned < max_zombies:
		spawn_zombie()
		zombie_spawn_timer = 0.0
	
	# Check win condition - all zombies killed
	if zombies_spawned >= max_zombies and zombies.size() == 0:
		complete_level()
	
	# Check lose condition - zombie reached end
	for zombie in zombies:
		if zombie.position.x < 0:
			lose_game()

func spawn_zombie():
	var z = load("res://scenes/zombie.tscn").instantiate()
	if z:
		var row = randi() % GRID_ROWS
		z.row = row
		z.position = Vector2(1280, 150 + row * CELL_SIZE + CELL_SIZE / 2)
		z.game_scene = self
		z.hp = 100
		add_child(z)
		zombies.append(z)
		zombies_spawned += 1

func _input(event):
	if event is InputEventMouseButton and event.pressed:
		var grid_x = int((event.position.x - 100) / CELL_SIZE)
		var grid_y = int((event.position.y - 150) / CELL_SIZE)
		
		if grid_x >= 0 and grid_x < GRID_COLS and grid_y >= 0 and grid_y < GRID_ROWS:
			plant_on_grid(grid_x, grid_y)

func plant_on_grid(col: int, row: int):
	var cost = 0
	var plant_path = ""
	
	if selected_plant == "pea":
		cost = 50
		plant_path = "res://scenes/pea_shooter.tscn"
	elif selected_plant == "cherry":
		cost = 150
		plant_path = "res://scenes/cherry_bomb.tscn"
	elif selected_plant == "hybrid":
		cost = 100
		plant_path = "res://scenes/hybrid_pea.tscn"
	else:
		return
	
	if sun >= cost:
		sun -= cost
		sun_label.text = "Sun: %d" % sun
		
		var plant = load(plant_path).instantiate()
		if plant:
			var x = col * CELL_SIZE + 100 + CELL_SIZE / 2
			var y = row * CELL_SIZE + 150 + CELL_SIZE / 2
			plant.position = Vector2(x, y)
			plant.col = col
			plant.row = row
			plant.game_scene = self
			add_child(plant)
			plants.append(plant)

func _on_pea_selected():
	selected_plant = "pea"

func _on_cherry_selected():
	selected_plant = "cherry"

func _on_hybrid_selected():
	selected_plant = "hybrid"

func get_plants_at_row(row: int) -> Array:
	return plants.filter(func(p): return p.row == row)

func get_zombies_in_lane(row: int) -> Array:
	return zombies.filter(func(z): return z.row == row)

func get_zombies_in_range(pos: Vector2, range_dist: float) -> Array:
	return zombies.filter(func(z): return pos.distance_to(z.position) < range_dist)

func complete_level():
	level_complete = true
	var coins_earned = 100 + (level * 50)
	save_manager.add_coins(coins_earned)
	
	await get_tree().create_timer(2.0).timeout
	get_tree().change_scene_to_file("res://scenes/main_menu.tscn")

func lose_game():
	game_over = true
	print("Game Over!")
	await get_tree().create_timer(2.0).timeout
	get_tree().change_scene_to_file("res://scenes/main_menu.tscn")
