extends Node2D

var speed: float = 300.0
var row: int
var damage: int = 15
var game_scene: Node

func _process(delta):
	position.x += speed * delta
	
	# Check collision with zombies
	var zombies = game_scene.get_zombies_in_lane(row)
	for zombie in zombies:
		if abs(zombie.position.x - position.x) < 20:
			zombie.take_damage(damage)
			queue_free()
			return
	
	# Remove if off screen
	if position.x > 1400:
		queue_free()
