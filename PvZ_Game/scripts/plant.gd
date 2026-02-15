extends Node2D
class_name Plant

var hp: int = 100
var col: int
var row: int
var game_scene: Node

func _ready():
	modulate = Color.WHITE

func take_damage(amount: int):
	hp -= amount
	if hp <= 0:
		die()

func die():
	if game_scene and self in game_scene.plants:
		game_scene.plants.erase(self)
	queue_free()

func get_plant_name() -> String:
	return "Plant"
