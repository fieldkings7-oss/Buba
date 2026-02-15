extends Plant

var shoot_timer: float = 0.0
var shoot_cooldown: float = 1.5
var pea_scene: PackedScene

func _ready():
	hp = 100
	pea_scene = preload("res://scenes/pea.tscn")

func _process(delta):
	shoot_timer += delta
	if shoot_timer >= shoot_cooldown:
		var zombies_in_row = game_scene.get_zombies_in_lane(row)
		if zombies_in_row.size() > 0:
			shoot()
			shoot_timer = 0.0

func shoot():
	if pea_scene:
		var pea = pea_scene.instantiate()
		pea.position = position
		pea.row = row
		game_scene.add_child(pea)

func get_plant_name() -> String:
	return "Pea Shooter"
