extends Node

signal level_complete(coins_earned: int)
signal game_over

const GRID_COLS = 15
const GRID_ROWS = 6
const CELL_SIZE = 80
const SUN_VALUE = 25

var sun: int = 200
var score: int = 0
var level: int = 1
var is_game_lost = false

func _ready():
	pass

func add_sun(amount: int):
	sun += amount

func remove_sun(amount: int) -> bool:
	if sun >= amount:
		sun -= amount
		return true
	return false

func can_plant_here(col: int, row: int) -> bool:
	if col < 0 or col >= GRID_COLS or row < 0 or row >= GRID_ROWS:
		return false
	return true

func end_level_victory():
	var coins_earned = 100 + (level * 50)
	level_complete.emit(coins_earned)

func end_level_defeat():
	is_game_lost = true
	game_over.emit()
