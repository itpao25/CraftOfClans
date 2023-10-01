package it.itpao25.craftofclans.village;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import it.itpao25.craftofclans.CraftOfClans;
import it.itpao25.craftofclans.map.MapInfo;

public class VillageGeneratorBasic extends ChunkGenerator {

	private Material superficie;
	
	public VillageGeneratorBasic() {
		this.superficie = VillageSettings.getMaterialGeneration();
	}
	
	public VillageGeneratorBasic(Material materiale) {
		this.superficie = materiale;
	}

	@Override
	public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {

		int altezza = VillageSettings.getHeight(null);

		ChunkData chunkData = createChunkData(world);
		chunkData.setRegion(0, 0, 0, 16, 1, 16, Material.BEDROCK);
		chunkData.setRegion(0, 1, 0, 16, altezza - 20, 16, Material.DIRT);
		chunkData.setRegion(0, altezza - 20, 0, 16, altezza, 16, superficie);
		
		// Se ha il generatore di blocchi random
		if (CraftOfClans.config.getBoolean("generator.surface-block-pattern")) {

			List<String> materiali = CraftOfClans.config.get().getStringList("generator.surface-block-list");
			List<Material> material_obj = new ArrayList<>();

			for (String value : materiali) {
				material_obj.add(Material.getMaterial(value));
			}

			// Se bisogna mettere l'erba sopra
			boolean erba_creare = CraftOfClans.config.getBoolean("generator.surface-grass");
			List<Material> erbe = new ArrayList<>();
			if (erba_creare) {
				erbe = MapInfo.getErbaVillage();
			}

			int massimo_rand = materiali.size() - 1;
			int erba_rand = erbe.size() - 1;

			for (int chunk_x = 0; chunk_x < 16; chunk_x++) {
				for (int chunk_z = 0; chunk_z < 16; chunk_z++) {

					int random_int = (int) Math.floor(Math.random() * (massimo_rand - 0 + 1) + 0);
					chunkData.setBlock(chunk_x, altezza - 1, chunk_z, material_obj.get(random_int));

					if (erba_creare) {
						int interval_100 = (int) Math.floor(Math.random() * (100 - 0 + 1) + 0);

						if (interval_100 % 10 == 0) {
							int random_erba_int = (int) Math.floor(Math.random() * (erba_rand - 0 + 1) + 0);
							chunkData.setBlock(chunk_x, altezza, chunk_z, erbe.get(random_erba_int));
						}
					}
				}
			}
		}
		return chunkData;
	}

	@Override
	public boolean canSpawn(World world, int x, int z) {
		return true;
	}
}
