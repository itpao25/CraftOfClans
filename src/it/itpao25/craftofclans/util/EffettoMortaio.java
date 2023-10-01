package it.itpao25.craftofclans.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;

public class EffettoMortaio extends Effect {

	/**
	 * ParticleType of spawned particle
	 */
	public Particle particle = Particle.FLAME;

	/**
	 * Height of the arc in blocks
	 */
	public float height = 10;

	/**
	 * Particles per arc
	 */
	public int particles = 100;

	protected final Vector link;
	protected final float lenght;
	protected int step = 0;

	public Location loc;

	public EffettoMortaio(EffectManager effectManager, Location start, Location stop) {
		super(effectManager);
		link = stop.toVector().subtract(start.toVector());
		lenght = (float) link.length();

		type = EffectType.REPEATING;
		period = 0;
		iterations = 2;
		loc = start;
	}

	@Override
	public void onRun() {
		float pitch = (float) (4 * height / Math.pow(lenght, 2));
		for (int i = 0; i < particles; i++) {
			Vector v = link.clone().normalize().multiply((float) lenght * i / particles);
			float x = ((float) i / particles) * lenght - lenght / 2;
			float y = (float) (-pitch * Math.pow(x, 2) + height);
			loc.add(v);
			loc.add(0, y, 0);

			if (loc.getBlock().getType() != Material.AIR) {
				this.cancel(false);
				return;
			}

			display(this.particle, loc);
			loc.subtract(0, y, 0);
			loc.subtract(v);

			step++;
		}
	}

}