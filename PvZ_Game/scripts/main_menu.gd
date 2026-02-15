extends Node2D

var save_manager: Node
var truck_position: float = -200.0
var truck_speed: float = 100.0
var plants_falling: int = 0
var show_shop: bool = false

var coins_label: Label
var upgrade_status: Label

func _ready():
	save_manager = get_node("/root/SaveManager")
	
	# Create UI
	create_menu_ui()
	draw_space_background()

func create_menu_ui():
	var ui_layer = CanvasLayer.new()
	ui_layer.name = "UILayer"
	add_child(ui_layer)
	
	# Title
	var title = Label.new()
	title.text = "Plants vs Space Zombies"
	title.add_theme_font_size_override("font_size", 64)
	title.position = Vector2(640 - 350, 50)
	ui_layer.add_child(title)
	
	# Coins display
	coins_label = Label.new()
	coins_label.add_theme_font_size_override("font_size", 24)
	coins_label.position = Vector2(50, 100)
	update_coins_display()
	ui_layer.add_child(coins_label)
	
	# Early prototype notice
	var proto_label = Label.new()
	proto_label.text = "EARLY PROTOTYPE"
	proto_label.add_theme_font_size_override("font_size", 20)
	proto_label.modulate = Color.YELLOW
	proto_label.position = Vector2(50, 140)
	ui_layer.add_child(proto_label)
	
	# Upgrade status
	upgrade_status = Label.new()
	upgrade_status.add_theme_font_size_override("font_size", 18)
	upgrade_status.position = Vector2(50, 180)
	update_upgrade_status()
	ui_layer.add_child(upgrade_status)
	
	# Start button
	var start_btn = Button.new()
	start_btn.text = "Start Game"
	start_btn.size = Vector2(300, 80)
	start_btn.position = Vector2(490, 350)
	start_btn.pressed.connect(func(): get_tree().change_scene_to_file("res://scenes/game.tscn"))
	ui_layer.add_child(start_btn)
	
	# Shop button
	var shop_btn = Button.new()
	shop_btn.text = "Shop"
	shop_btn.size = Vector2(300, 80)
	shop_btn.position = Vector2(490, 480)
	shop_btn.pressed.connect(_on_shop_pressed)
	ui_layer.add_child(shop_btn)
	
	# Shop panel (hidden by default)
	create_shop_panel(ui_layer)

func create_shop_panel(parent: CanvasLayer):
	var shop_panel = PanelContainer.new()
	shop_panel.name = "ShopPanel"
	shop_panel.size = Vector2(600, 400)
	shop_panel.position = Vector2(340, 150)
	shop_panel.modulate.a = 0
	shop_panel.mouse_filter = Control.MOUSE_FILTER_IGNORE
	parent.add_child(shop_panel)
	
	var vbox = VBoxContainer.new()
	shop_panel.add_child(vbox)
	
	var title = Label.new()
	title.text = "SHOP"
	title.add_theme_font_size_override("font_size", 32)
	vbox.add_child(title)
	
	var space = Control.new()
	space.custom_minimum_size = Vector2(0, 20)
	vbox.add_child(space)
	
	# Hybrid pea upgrade
	var hybrid_text = Label.new()
	hybrid_text.text = "Hybrid Pea Launcher (500 Coins)"
	hybrid_text.add_theme_font_size_override("font_size", 16)
	vbox.add_child(hybrid_text)
	
	var hybrid_desc = Label.new()
	hybrid_desc.text = "Upgrade Pea Shooter to shoot\nexplosive cherries (50 DMG)"
	hybrid_desc.add_theme_font_size_override("font_size", 12)
	vbox.add_child(hybrid_desc)
	
	var hybrid_btn = Button.new()
	hybrid_btn.text = "BUY"
	hybrid_btn.custom_minimum_size = Vector2(100, 40)
	hybrid_btn.pressed.connect(_on_buy_hybrid)
	vbox.add_child(hybrid_btn)
	
	var close_btn = Button.new()
	close_btn.text = "Close"
	close_btn.custom_minimum_size = Vector2(100, 40)
	close_btn.pressed.connect(_on_close_shop)
	vbox.add_child(close_btn)

func draw_space_background():
	# Draw stars (simple dots)
	randomize()
	for i in range(100):
		var x = randf_range(0, 1280)
		var y = randf_range(0, 720)
		draw_circle(Vector2(x, y), 2, Color.WHITE)

func _process(delta):
	# Animate truck flying across screen
	truck_position += truck_speed * delta
	queue_redraw()
	
	if truck_position < 1280 + 200:
		# Truck animation
		var truck_size = 120
		var truck_x = truck_position
		var truck_y = 250
		
		# Simple truck shape
		draw_line(Vector2(truck_x, truck_y), Vector2(truck_x + truck_size, truck_y), Color.GREEN, 3)
		
		# Drop plants occasionally
		if int(truck_position) % 100 == 0:
			pass  # Could add particle effects here

func _on_shop_pressed():
	var ui_layer = find_child("UILayer")
	if ui_layer:
		var shop_panel = ui_layer.find_child("ShopPanel")
		if shop_panel:
			var tween = create_tween()
			tween.tween_property(shop_panel, "modulate:a", 1.0, 0.3)
			shop_panel.mouse_filter = Control.MOUSE_FILTER_STOP

func _on_close_shop():
	var ui_layer = find_child("UILayer")
	if ui_layer:
		var shop_panel = ui_layer.find_child("ShopPanel")
		if shop_panel:
			var tween = create_tween()
			tween.tween_property(shop_panel, "modulate:a", 0.0, 0.3)
			await tween.finished
			shop_panel.mouse_filter = Control.MOUSE_FILTER_IGNORE

func _on_buy_hybrid():
	if save_manager.buy_upgrade("hybrid_pea"):
		update_upgrade_status()
		update_coins_display()
		_on_close_shop()
	else:
		print("Not enough coins!")

func update_coins_display():
	coins_label.text = "Coins: %d" % save_manager.coins

func update_upgrade_status():
	if save_manager.has_upgrade("hybrid_pea"):
		upgrade_status.text = "✓ Hybrid Pea UNLOCKED"
		upgrade_status.modulate = Color.GREEN
	else:
		upgrade_status.text = "Hybrid Pea: 500 Coins"
		upgrade_status.modulate = Color.GRAY
