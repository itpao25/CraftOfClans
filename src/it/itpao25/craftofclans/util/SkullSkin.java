package it.itpao25.craftofclans.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import it.itpao25.craftofclans.config.CraftOfClansSkin;

public class SkullSkin {

	/**
	 * Imposto la skin ad una head
	 * 
	 * @param playerHead
	 * @param nome_troop
	 */
	public static ItemMeta setSkullSkin(ItemMeta playerHead, String nome_troop) {
		
		String texture = CraftOfClansSkin.getString("skins." + nome_troop + ".texture");
		
		SkullMeta headMeta = (SkullMeta) playerHead;
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);

		profile.getProperties().put("textures", new Property("textures", texture));

		try {
			Method mtd = headMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
			mtd.setAccessible(true);
			mtd.invoke(headMeta, profile);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
			ex.printStackTrace();
		}
		
		return playerHead;
	}
}
