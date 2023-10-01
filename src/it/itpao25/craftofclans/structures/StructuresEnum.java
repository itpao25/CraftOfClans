package it.itpao25.craftofclans.structures;

public enum StructuresEnum {
	
	TOWNHALL, 
	GOLD_MINE, 
	COLLECTOR_ELIXIR, 
	ELIXIR_STORAGE, 
	GOLD_STORAGE, 
	ARCHER_TOWER, 
	CANNON, 
	GEMS_COLLECTOR, 
	WIZARD_TOWER, 
	MORTAR, 
	DARK_ELIXIR_STORAGE,
	DARK_ELIXIR_DRILL, 
	TESLA, 
	BOMB,
	GUARDIAN,
	SKELETON_TRAP,
	LABORATORY,
	BARRACKS,
	VILLAGE_WALL, 
	VILLAGE_GATE_WALL,
	DECORATION;
	
	public String type_struttura;
	
	public StructuresEnum setType(String tipo) {
		this.type_struttura = tipo;
		return this;
	}
}
