extends Plant

var explode_timer: float = 0.0
var explode_delay: float = 1.5
var is_active: bool = false

func _ready():
	hp = 150

func _process(delta):
	if not is_active:
		var zombies_in_range = game_scene.get_zombies_in_range(position, 200)
		if zombies_in_range.size() > 0:
			is_active = true
			explode_timer = 0.0
	
	if is_active:
		explode_timer += delta
		modulate = Color.RED
		
		if explode_timer >= explode_delay:
			explode()

func explode():
	var zombies_in_range = game_scene.get_zombies_in_range(position, 300)
	for zombie in zombies_in_range:
		zombie.take_damage(300)
	
	die()

func get_plant_name() -> String:
	return "Cherry Bomb"
