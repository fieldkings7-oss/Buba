extends Node2D
class_name Zombie

var hp: int = 100
var speed: float = 50.0
var col: int
var row: int
var game_scene: Node
var is_eating: bool = false
var target_plant: Plant = null

func _ready():
	modulate = Color.WHITE

func _process(delta):
	if is_eating and target_plant:
		eat_plant(delta)
	else:
		move(delta)

func move(delta):
	position.x -= speed * delta
	
	# Check for plants at this position
	var plants_here = game_scene.get_plants_at_row(row)
	for plant in plants_here:
		if abs(plant.position.x - position.x) < 30:
			is_eating = true
			target_plant = plant
			break

func eat_plant(delta):
	if target_plant:
		target_plant.take_damage(10)
		if target_plant.hp <= 0:
			target_plant = null
			is_eating = false

func take_damage(amount: int):
	hp -= amount
	if hp <= 0:
		die()

func die():
	if game_scene and self in game_scene.zombies:
		game_scene.zombies.erase(self)
	queue_free()

func get_zombie_name() -> String:
	return "Space Zombie"
