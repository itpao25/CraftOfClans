package it.itpao25.craftofclans;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.UUID;

import it.itpao25.craftofclans.attack.Attack;
import it.itpao25.craftofclans.attack.AttackListener;
import it.itpao25.craftofclans.attack.AttackerManager;
import it.itpao25.craftofclans.clans.ClansUIListeners;
import it.itpao25.craftofclans.command.CommandClan;
import it.itpao25.craftofclans.command.CommandMain;
import it.itpao25.craftofclans.command.CommandTier;
import it.itpao25.craftofclans.config.CraftOfClansC;
import it.itpao25.craftofclans.config.CraftOfClansClan;
import it.itpao25.craftofclans.config.CraftOfClansData;
import it.itpao25.craftofclans.config.CraftOfClansM;
import it.itpao25.craftofclans.config.CraftOfClansSkin;
import it.itpao25.craftofclans.config.CraftOfClansTier;
import it.itpao25.craftofclans.config.CraftOfClansTroops;
import it.itpao25.craftofclans.guardian.GuardianHandler;
import it.itpao25.craftofclans.guardian.GuardianNPC;
import it.itpao25.craftofclans.handler.EffectLib_hook;
import it.itpao25.craftofclans.handler.LogHandler;
import it.itpao25.craftofclans.handler.AsyncWorldEditHandler;
import it.itpao25.craftofclans.handler.CitizenHandler;
import it.itpao25.craftofclans.handler.MVdWPlaceholderAPI_hook;
import it.itpao25.craftofclans.handler.PlaceholderAPI_hook;
import it.itpao25.craftofclans.handler.PlaceholderAPI_register;
import it.itpao25.craftofclans.handler.SentinelHandler;
import it.itpao25.craftofclans.handler.TitleManagerHook;
import it.itpao25.craftofclans.handler.WorldEditHandler;
import it.itpao25.craftofclans.handler.WorldGuardHandler;
import it.itpao25.craftofclans.holo.HolographicHook;
import it.itpao25.craftofclans.holo.HolographicListner;
import it.itpao25.craftofclans.inventory.InventoryListener;
import it.itpao25.craftofclans.limit.StructuresLimit;
import it.itpao25.craftofclans.map.ExpanderRegister;
import it.itpao25.craftofclans.map.MapInfo;
import it.itpao25.craftofclans.map.Placer;
import it.itpao25.craftofclans.player.PlayerListener;
import it.itpao25.craftofclans.protocollib.EntityHider;
import it.itpao25.craftofclans.protocollib.EntityHider.Policy;
import it.itpao25.craftofclans.protocollib.ProcolLibHook;
import it.itpao25.craftofclans.storage.DatabaseHandler;
import it.itpao25.craftofclans.storage.DatabaseType;
import it.itpao25.craftofclans.storage.StorageFlat;
import it.itpao25.craftofclans.storage.StorageMySQL;
import it.itpao25.craftofclans.structures.SchematicsHandler;
import it.itpao25.craftofclans.structures.StructuresId;
import it.itpao25.craftofclans.structures.StructuresParticle;
import it.itpao25.craftofclans.util.Color;
import it.itpao25.craftofclans.validator.PluginValidator;
import it.itpao25.craftofclans.village.VillageGeneratorBasic;
import it.itpao25.craftofclans.village.VillageId;
import it.itpao25.craftofclans.village.VillageSettings;
import it.itpao25.craftofclans.village.VillagesHandler;
import it.itpao25.craftofclans.worldmanager.WorldListener;
import it.itpao25.craftofclans.structures_resource.*;
import it.itpao25.craftofclans.tier.TierListener;
import net.citizensnpcs.api.npc.NPC;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class CraftOfClans extends JavaPlugin implements Listener {

	private static CraftOfClans instance;
	public static DatabaseType type;

	// Se è abilitata la modalità freemode
	public static boolean freemode = false;
	
	// Config.yml
	public static CraftOfClansC config;

	// Troops.yml
	public static CraftOfClansTroops troops;

	// World Edit
	public static boolean isWorldEdit;
	public static boolean isAsyncWorldEdit;

	// TitleManager
	public static boolean isTitleManager;
	// Citizens
	public static boolean isCitizen;
	// Sentinel
	public static boolean isSentinel;

	// Protocollib
	public static boolean isProtocolLib;
	public static EntityHider entityHider;

	public static boolean isScoreboard = false;
	// HolographicDisplay
	public static boolean isHolographicDisplay;
	// MVdWPlaceholderAPI
	public static boolean isMVdWPlaceholderAPI;
	// PAPI
	public static boolean isPAPI;
	// EffectLib
	public static boolean isEffectLib;
	
	// WorldGuard
	public static boolean isWorldGuard;
	
	// Materiale che deve essere utilizzato per la generazione del mondo
	public static Material superfice_world;

	// Task inactive player
	public static BukkitTask inactive_task;

	public static CraftOfClans getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {

		instance = this;
		getCommand("coc").setExecutor(new CommandMain());
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new InventoryListener(), this);
		getServer().getPluginManager().registerEvents(new AttackListener(), this);
		// getServer().getPluginManager().registerEvents(new CoCScoreboardEvent(),
		// this);
		getServer().getPluginManager().registerEvents(new WorldListener(), this);
		getServer().getPluginManager().registerEvents(new TierListener(), this);
		getServer().getPluginManager().registerEvents(new ClansUIListeners(), this);

		// Gestione dei clan tramite /clan (since 0.5)
		getCommand("clan").setExecutor(new CommandClan());
		getCommand("tier").setExecutor(new CommandTier());

		// Gestione dei limiti per il plugin
		getServer().getPluginManager().registerEvents(new StructuresLimit(), this);

		// Carico file di configurazione
		loadconfig();

		// Imposto la modalità freemode se abilitata
		freemode = CraftOfClans.config.getBoolean("freemode.enable");
		
		// Imposto il materiale per la generazione
		superfice_world = VillageSettings.getMaterialGeneration();

		// Registro le strutture
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

			public void run() {
				String vl = PluginValidator.check();
				if (!vl.equals(PluginValidator.VALID) || vl == "") {
					if (vl.equals(PluginValidator.DISABLED)) {
						Bukkit.getConsoleSender().sendMessage("");
						Bukkit.getConsoleSender().sendMessage(Color.translate("&c[&6CraftOfClans&c] &cUpdate the plugin!"));
						Bukkit.getConsoleSender().sendMessage(Color.translate("&c[&6CraftOfClans&c] &4The plugin has been disabled!!"));
						Bukkit.getConsoleSender().sendMessage("");
						getInstance().setEnabled(false);
						return;
					} else if (vl.equals(PluginValidator.WAIT)) {
						Bukkit.getConsoleSender().sendMessage("");
						Bukkit.getConsoleSender().sendMessage(Color.translate("&c[&6CraftOfClans&c] &cUpdate the plugin!"));
						Bukkit.getConsoleSender().sendMessage(Color.translate("&c[&6CraftOfClans&c] &cYouhave a few days to update, then the plugin will be disabled for security!"));
						Bukkit.getConsoleSender().sendMessage("");
					}
				} else {
					Bukkit.getConsoleSender().sendMessage(Color.translate("&c[&6CraftOfClans&c] &2You using the latest version of CraftOfClans"));
				}

				// Controllo se è installato Citizens
				getCitizenHook();
				getSentinelHook();

				// Controllo se è attivo TitleManager
				getTitleManagerHook();

				// Verifico la presenza di worldedit installato nel server
				WorldEditHandler worldedit = new WorldEditHandler();
				CraftOfClans.isWorldEdit = worldedit.enable;

				// Verifico la presenza di WorldGuard installato nel server
				WorldGuardHandler worldguard = new WorldGuardHandler();
				CraftOfClans.isWorldGuard = worldguard.enable;
				
				// Supporto ad AsyncWorldEdit
				AsyncWorldEditHandler asyncWorldedit = new AsyncWorldEditHandler();
				CraftOfClans.isAsyncWorldEdit = asyncWorldedit.enable;

				// ProtocolLib
				getProtocolLIbHook();
				entityHider = new EntityHider(getInstance(), Policy.BLACKLIST);

				// HolographicDisplay
				getHolographicDisplay();
				if (isHolographicDisplay) {
					getServer().getPluginManager().registerEvents(new HolographicListner(), getInstance());
				}

				// MVdWPlaceholderAPI
				getMVdWPlaceholderAPI();

				// PlaceHolderAPI
				getPlaceholderAPI();

				// EffectLib
				EffectLib_hook effectlib = new EffectLib_hook();
				isEffectLib = effectlib.enable;

				if (!isEffectLib || !isWorldEdit || !isProtocolLib || !isCitizen || !isSentinel) {
					LogHandler.log("Check that you have the following dependencies: WorldEdit, Citizens, Sentinel, ProtocolLib, EffectLib");
					getInstance().setEnabled(false);
				}

				// Carico la gestione del database
				setStorage();
				registroVillages();
				registroStructures();
				registroExpansor();
				registroGuardiani();

				// Rigistro il placer
				Placer placer = new Placer();
				placer.runTask();
				
				// Avvio il processo per le risorse
				ResourceTask();

				// Task per player non attivi
				InactivePlayerTask();

				// Task per le particelle delle strutture
				ParticleTask();

				LogHandler.log("");
				LogHandler.log("");
				LogHandler.log(" Plugin loaded successfully");
				LogHandler.log(" For assistance use: https://discord.gg/8wtfzms");
				LogHandler.log("");
				LogHandler.log("");
			}
		}, 20 * 1);
	}

	@Override
	public void onDisable() {
		LogHandler.log("Deactivation in progress...");

		// Strutture durante gli attacchi
		int numero = 0;
		for (Entry<NPC, StructuresId> id : AttackListener.difese.entrySet()) {
			id.getKey().destroy();
			numero++;
		}
		
		if (isCitizen) {
			// Strutture che dovevano rimanere spawnate
			for (Entry<StructuresId, ArrayList<NPC>> id : AttackListener.npc_structures.entrySet()) {
				for (NPC npc : id.getValue()) {
					npc.destroy();
				}
			}
			// Rimuovo tutti i guardiani dei villaggi
			for (Entry<NPC, VillageId> key : GuardianNPC.npc_guardian.entrySet()) {
				key.getKey().destroy();
			}

			// Rimuovo tutte le truppe spawnate
			for (Entry<NPC, Player> key : AttackListener.truppe_sentinel.entrySet()) {
				key.getKey().destroy();
			}

			// Rimuovo tutte le truppe spawnate
			for (Entry<NPC, StructuresId> key : AttackListener.difese_sentinel.entrySet()) {
				key.getKey().destroy();
			}
		}
		
		// Stoppo gli attacchi in corso
		for (Entry<UUID, Attack> id : AttackerManager.battles.entrySet()) {
			id.getValue().stop(false);
		}

		// Rimuovo gli hologrammi se ci sono
		if (isHolographicDisplay) {
			for (Entry<StructuresId, com.gmail.filoghost.holographicdisplays.api.Hologram> key : HolographicListner.lista_holo.entrySet()) {
				key.getValue().delete();
			}
		}

		LogHandler.log(numero + " NPC momentary eliminated");

		// Interruzione della connessione
		if (DatabaseHandler.getType() == DatabaseType.MySQL) {
			StorageMySQL.close();
		} else if (DatabaseHandler.getType() == DatabaseType.SQLite) {
			StorageFlat.close();
		}
	}

	@SuppressWarnings("unused")
	private boolean loadconfig() {

		CraftOfClans.config = new CraftOfClansC();
		CraftOfClansM CraftOfClansM = new CraftOfClansM();
		CraftOfClansData CraftOfClansData = new CraftOfClansData();
		CraftOfClansTier CraftOfClansTier = new CraftOfClansTier();
		CraftOfClansClan CraftOfClansClan = new CraftOfClansClan();
		CraftOfClansSkin CraftOfClansSkin = new CraftOfClansSkin();
		CraftOfClans.troops = new CraftOfClansTroops();

		return true;
	}

	private boolean setStorage() {
		// Controllo se la stringa "storage.type" non è nulla
		// Così da stabilire quale tipo di database bisogna usare
		if (CraftOfClans.config.getString("storage.type") != null) {
			String database = CraftOfClans.config.getString("storage.type");
			LogHandler.log("Initiating database..");
			if (database.equalsIgnoreCase("mysql")) {
				// Imposto il database mysql
				type = DatabaseType.MySQL;
			} else if (database.equalsIgnoreCase("sqlite")) {
				// Imposto il database sqlite
				type = DatabaseType.SQLite;
			} else {
				type = DatabaseType.SQLite;
			}

			// Connessione
			DatabaseHandler.setHandlerDB(type);
			return true;
		} else {
			//
			type = DatabaseType.SQLite;
			DatabaseHandler.setHandlerDB(type);
		}
		LogHandler.error("Database can not be set, check the configuration file!");
		return false;
	}

	/**
	 * Registro in un hasmap tutti i chunk già registrati tramite il file data.yml
	 * 
	 * @return
	 */
	private boolean registroExpansor() {
		new ExpanderRegister();
		// return expansor.register();
		return true;
	}

	/**
	 * Registro tutte le strutture costruite all'interno dei villaggi
	 * 
	 * @return
	 */
	private boolean registroStructures() {
		// Carico la cartella per le schematics
		SchematicsHandler structure = new SchematicsHandler();
		return structure.registerStructures();
	}

	/**
	 * Registro tutti i villaggi (formato x;z)
	 * 
	 * @return
	 */
	private boolean registroVillages() {

		boolean erba_creare = CraftOfClans.config.getBoolean("generator.surface-grass");
		if (erba_creare) {
			// Carico i materiali di erba per le espasioni
			MapInfo.erbe_mappa = MapInfo.getErbaVillage();
		}

		// Carico la cartella per le schematics
		VillagesHandler villages = new VillagesHandler();
		return villages.registers();
	}

	/**
	 * Registro tutti i villaggi (formato x;z)
	 * 
	 * @return
	 */
	private boolean registroGuardiani() {
		GuardianHandler guardiani = new GuardianHandler();
		return guardiani.registers();
	}

	/**
	 * Avvio il task per l'aggiunta delle risorse nelle strutture
	 * 
	 * @return
	 */
	public boolean ResourceTask() {

		// Task per il collezionamento di risorse generali (GOLD, ELIXIR, ELIXIR_DARK)
		Bukkit.getScheduler().runTaskTimer(getInstance(), new ResourceTask(), 0L, 20L * 60);

		// Gemme = default 10 minuti
		int delay_gems = 600;
		if (CraftOfClans.config.getString("shop.structures-core.GEMS_COLLECTOR.delay") != null) {
			if (CraftOfClans.config.getInt("shop.structures-core.GEMS_COLLECTOR.delay") != 0) {
				delay_gems = CraftOfClans.config.getInt("shop.structures-core.GEMS_COLLECTOR.delay");
			}
		}
		Bukkit.getScheduler().runTaskTimer(getInstance(), new ResourceTaskGems(), 0L, 20L * delay_gems);
		return true;
	}

	public boolean getTitleManagerHook() {
		TitleManagerHook hook = new TitleManagerHook();
		hook.setup();
		isTitleManager = TitleManagerHook.enable;
		return true;
	}

	public boolean getCitizenHook() {
		CitizenHandler hook = new CitizenHandler();
		hook.setup();
		isCitizen = CitizenHandler.enable;
		return isCitizen;
	}

	public boolean getSentinelHook() {
		SentinelHandler hook = new SentinelHandler();
		hook.setup();
		isSentinel = SentinelHandler.enable;
		return isSentinel;
	}

	public boolean getProtocolLIbHook() {
		ProcolLibHook hook = new ProcolLibHook();
		hook.setup();
		isProtocolLib = ProcolLibHook.enable;
		return isProtocolLib;
	}

	public boolean getHolographicDisplay() {
		HolographicHook hook = new HolographicHook();
		hook.setup();
		isHolographicDisplay = hook.enable;
		return isHolographicDisplay;
	}

	/**
	 * Registro MVdWPlaceholderAPI
	 * 
	 * @return
	 */
	public boolean getMVdWPlaceholderAPI() {
		MVdWPlaceholderAPI_hook hook = new MVdWPlaceholderAPI_hook();
		hook.setup();
		isMVdWPlaceholderAPI = hook.enable;
		if (hook.enable) {
			// Registro i placers
			hook.registerPlacers();
		}
		return isMVdWPlaceholderAPI;
	}

	/**
	 * Registro PlaceholderAPI
	 * 
	 * @return
	 */
	public boolean getPlaceholderAPI() {
		PlaceholderAPI_hook hook = new PlaceholderAPI_hook();
		hook.setup();
		isPAPI = hook.enable;
		if (isPAPI) {
			new PlaceholderAPI_register().register();
		}
		return isPAPI;
	}

	/**
	 * Avvio il task per il controllo dei villaggi inattivi (ogni giorno)
	 * 
	 * @return
	 */
	public boolean InactivePlayerTask() {
		if (CraftOfClans.config.getBoolean("task-search-inactive-player.enable")) {
			// Ogni giorno controllo se ci sono player inattivi
			CraftOfClans.inactive_task = Bukkit.getScheduler().runTaskTimer(getInstance(), new InactivePlayerTask(), 0L, 20 * 60 * 60 * 24);
		}
		return true;
	}

	/**
	 * Task per le particelle
	 * 
	 * @return
	 */
	public boolean ParticleTask() {
		Bukkit.getScheduler().runTaskTimer(getInstance(), new StructuresParticle(), 0L, 40L);
		return true;
	}

	/**
	 * Imposto il generatore per la mappa, dove è possibile usare il generatore
	 * tramite Multiverse core: | mv create test normal -g CraftOfClans | oppure
	 * tramite il file bukkit.yml: worlds: test: generator: CraftOfClans
	 */
	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return new VillageGeneratorBasic(superfice_world);
	}
}
