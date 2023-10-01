package it.itpao25.craftofclans.structures;

import it.itpao25.craftofclans.player.PlayerStored;
import it.itpao25.craftofclans.village.VillageCuboid;
import it.itpao25.craftofclans.village.VillageId;

public class Structures {

	private VillageCuboid cuboid;

	public Structures(VillageCuboid cuboid) {
		this.cuboid = cuboid;
	}
	
	public Municipio getMunicipio(PlayerStored p, VillageId id, boolean isPrimo, long timestart) {
		return new Municipio(cuboid, p, id, isPrimo, timestart);
	}

}
