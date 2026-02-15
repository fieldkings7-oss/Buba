extends Node

const SAVE_PATH = "user://pvz_save.json"

var coins: int = 0
var upgrades: Dictionary = {
	"hybrid_pea": false
}

func _ready():
	load_game()

func load_game():
	if ResourceLoader.exists(SAVE_PATH):
		var file = FileAccess.open(SAVE_PATH, FileAccess.READ)
		if file:
			var data = JSON.parse_string(file.get_as_text())
			if data:
				coins = data.get("coins", 0)
				upgrades = data.get("upgrades", upgrades)
	else:
		coins = 0
		upgrades = {"hybrid_pea": false}

func save_game():
	var data = {
		"coins": coins,
		"upgrades": upgrades
	}
	var file = FileAccess.open(SAVE_PATH, FileAccess.WRITE)
	file.store_string(JSON.stringify(data))

func add_coins(amount: int):
	coins += amount
	save_game()

func buy_upgrade(upgrade_key: String) -> bool:
	if upgrade_key == "hybrid_pea":
		if coins >= 500:
			coins -= 500
			upgrades["hybrid_pea"] = true
			save_game()
			return true
	return false

func has_upgrade(upgrade_key: String) -> bool:
	return upgrades.get(upgrade_key, false)
