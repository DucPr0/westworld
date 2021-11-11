package me.ducpro.westworldcore.main;

import java.util.HashMap;
import java.util.UUID;
import java.util.HashSet;

import org.bukkit.block.Block;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitRunnable;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.ducpro.westworldcharacter.WantedPosters;
import me.ducpro.westworldcombat.CombatList;
import me.ducpro.westworldcore.RepeatingTasks;
import me.ducpro.westworldcore.WantedTeam;
import me.ducpro.westworldcore.files.*;
import me.ducpro.westworldcore.utils.DestroyArmorstands;
import me.ducpro.westworldheist.info.HeistInfo;
import me.ducpro.westworldheist.load.LoadChests;
import me.ducpro.westworldheist.load.RemoveAllHeists;
import me.ducpro.westworldheist.info.ChestInfo;
import me.ducpro.westworldhorse.info.HorseInfo;
import me.ducpro.westworldhorse.utils.KillHorses;
import me.ducpro.westworldhouse.LockpickTask;
import me.ducpro.westworldhouse.info.DoorInfo;
import me.ducpro.westworldranks.utils.SpectatorReset;
import me.ducpro.westworldtrain.TrainManager;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {
	private FileHandler configfile, doorfile;
	private DataHandler playerdata;
	private PermHandler permHandler;
	public static int INTRO_STAGES = 5, MAX_HORSES = 1, MAX_DOORS = 4, PLAYER_HORSE_LIMIT = 1;
	public static int DONATOR_MAX_DOORS = 6, DONATOR_HORSE_LIMIT = 4;
	public static int RESPAWN_SECONDS = 300, JAIL_SECONDS = 600;
	private HashSet<Material> allowed = new HashSet<Material>();
	private String[] woodtypes = {"ACACIA", "BIRCH", "DARK_OAK", "JUNGLE", "OAK", "SPRUCE"};
	public TrainManager train = null;
	public boolean isAllowed(Material mat) {
		return allowed.contains(mat);
	}
	public void initAllowed() {
		allowed.add(Material.CHEST);
		allowed.add(Material.ACACIA_FENCE_GATE);
		allowed.add(Material.STONE_BUTTON);
		allowed.add(Material.IRON_DOOR);
		allowed.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
		allowed.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
		allowed.add(Material.STONE_PRESSURE_PLATE);
		for (int i = 0; i < woodtypes.length; i++) {
			allowed.add(Material.getMaterial(woodtypes[i] + "_BUTTON"));
			allowed.add(Material.getMaterial(woodtypes[i] + "_DOOR"));
			allowed.add(Material.getMaterial(woodtypes[i] + "_PRESSURE_PLATE"));
		}
	}
	public static final int HASH_LIMIT = 1000000007;
	public static class Coordinates {
		World world;
		int x, y, z;
		public Coordinates(Block b) {
			world = b.getWorld();
			x = b.getLocation().getBlockX();
			y = b.getLocation().getBlockY();
			z = b.getLocation().getBlockZ();
		}
		public Coordinates(World world, int x, int y, int z) {
			this.world = world;
			this.x = x;
			this.y = y;
			this.z = z;
		}
		@Override
		public int hashCode() {
			int result = 0;
			result += (((long) x) * 115822) % HASH_LIMIT;
			result %= HASH_LIMIT;
			result += (((long) y) * 823432) % HASH_LIMIT;
			result %= HASH_LIMIT;
			result += (((long) z) * z * 217315) % HASH_LIMIT;
			result %= HASH_LIMIT;
			result += (((long) world.getUID().hashCode()) * 218123) % HASH_LIMIT;
			result %= HASH_LIMIT;
			return result;
		}
		@Override
		public boolean equals(Object o) {
			if (o == this) return true;
			if (!(o instanceof Coordinates)) return false;
			Coordinates other = (Coordinates) o;
			if (other.getWorld().getUID() == world.getUID() && other.getBlock().getX() == x && other.getBlock().getY() == y &&
					other.getBlock().getZ() == z) return true;
			return false;
		}
		public Block getBlock() {
			return world.getBlockAt(new Location(world, x, y, z));
		}
		public void add(int x2, int y2, int z2) {
			x += x2;
			y += y2;
			z += z2;
		}
		public Location getLocation() {
			return new Location(world, x, y, z);
		}
		public World getWorld() {
			return world;
		}
	}
	private HashMap<UUID, PlayerInfo> playerinfo = new HashMap<UUID, PlayerInfo>();
	public PlayerInfo getPlayerInfo(Player p) {
		return playerinfo.get(p.getUniqueId());
	}
	public PlayerInfo getPlayerInfo(UUID uuid) {
		return playerinfo.get(uuid);
	}
	public PlayerInfo getPlayerInfo(HumanEntity entity) {
		return playerinfo.get(entity.getUniqueId());
	}
	public void addPlayerInfo(Player p, PlayerInfo pinfo) {
		playerinfo.put(p.getUniqueId(), pinfo);
	}
	public void removePlayerInfo(Player p) {
		playerinfo.remove(p.getUniqueId());
	}
	public boolean containsPlayer(UUID uuid) {
		return playerinfo.containsKey(uuid);
	}
	
	public HashMap<Coordinates, DoorInfo> doorinfo = new HashMap<Coordinates, DoorInfo>();
	public HashSet<Coordinates> interacteddoor = new HashSet<Coordinates>();
	//interacteddoor: Prevent PlayerInteractEvent and PlayerAnimationEvent both firing
	
	public HashMap<UUID, HorseInfo> horseinfo = new HashMap<UUID, HorseInfo>();
	
	public HashMap<Coordinates, ChestInfo> chestinfo = new HashMap<Coordinates, ChestInfo>();
	public HashMap<String, HeistInfo> heistinfo = new HashMap<String, HeistInfo>();
	
	private WantedTeam heistTeam, fightTeam, lockpickTeam;
	public WantedTeam getFightTeam() {
		return fightTeam;
	}
	public WantedTeam getHeistTeam() {
		return heistTeam;
	}
	public WantedTeam getLockpickTeam() {
		return lockpickTeam;
	}
	public boolean isWanted(Player p) {
		return heistTeam.isInTeam(p) || fightTeam.isInTeam(p) || lockpickTeam.isInTeam(p);
	}
	public FileHandler getConfigFile() {
		return configfile;
	}
	public DataHandler getData() {
		return playerdata;
	}
	public PermHandler getPermHandler() {
		return permHandler;
	}
	private Economy economy;
	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		economy = rsp.getProvider();
		return economy != null;
    }
	public Economy getEconomy() {
		return economy;
	}
	private CombatList combatList = new CombatList();
	public CombatList getCombatList() {
		return combatList;
	}
	
	private WantedPosters wanted;
	private LockpickTask lockpickTask;
	private RepeatingTasks repeatingTasks;
	@Override
	public void onEnable() {
		//Note to new developer: A lot of things are hardcoded as requested by Sixbits.
		configfile = new FileHandler(this);
		configfile.loadFile("config.yml");
		
		doorfile = new FileHandler(this);
		doorfile.loadFile("doors.yml");
		doorfile.loadDoors();
		
		playerdata = new DataHandler(this);
		playerdata.load();
		
		permHandler = new PermHandler(this);
		
		initAllowed();
		
		me.ducpro.westworldhouse.utils.UtilStacks.init();
		me.ducpro.westworldhorse.utils.UtilStacks.init();
		me.ducpro.westworldtrain.utils.UtilStacks.init();
		
		LoadChests.loadChests(this);
		heistTeam = new WantedTeam(this);
		fightTeam = new WantedTeam(this);
		lockpickTeam = new WantedTeam(this);
		
		lockpickTask = new LockpickTask(this);
		
		repeatingTasks = new RepeatingTasks(this);
		
		if (!setupEconomy()) {
			Bukkit.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
		}
		
		if (getConfigFile().getTrainStart()) {
			train = new TrainManager(this);
		}
		
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			PlaceholderExpansion roleplayname = new PlaceholderExpansion() {
				@Override
				public boolean canRegister() {
					return true;
				}
				@Override
				public String getVersion() {
					return "1.0.0";
				}
				@Override
				public String getIdentifier() {
					return "westcore";
				}
				@Override
				public String getAuthor() {
					return "DucPro";
				}
				@Override
				public String getPlugin() {
					return "WestworldCore";
				}
				@Override
				public String onPlaceholderRequest(Player p, String argument) {
					if (p == null) return "";
					if (argument.equals("roleplayname")) {
						return playerinfo.get(p.getUniqueId()).getName();
					}
					return null;
				}
			};
			boolean success = roleplayname.register();
			if (!success) {
				Bukkit.broadcastMessage(ChatColor.RED + "Placeholder failed!");
			}
		}
		
		getCommand("westcore").setExecutor(new me.ducpro.westworldcore.commands.AdminCommands(this, train));
		getCommand("westcorereset").setExecutor(new me.ducpro.westworldcore.commands.PlayerCommands(this));
		getCommand("ooc").setExecutor(new me.ducpro.westworldcore.commands.PlayerCommands(this));
		getCommand("looc").setExecutor(new me.ducpro.westworldcore.commands.PlayerCommands(this));
		getCommand("local").setExecutor(new me.ducpro.westworldcore.commands.PlayerCommands(this));
		getCommand("suicide").setExecutor(new me.ducpro.westworldcore.commands.PlayerCommands(this));
		getCommand("sit").setExecutor(new me.ducpro.westworldcore.commands.PlayerCommands(this));
		getCommand("toggleooc").setExecutor(new me.ducpro.westworldcore.commands.PlayerCommands(this));
		getCommand("message").setExecutor(new me.ducpro.westworldcore.commands.PlayerCommands(this));
		getCommand("reply").setExecutor(new me.ducpro.westworldcore.commands.PlayerCommands(this));
		getCommand("coinflip").setExecutor(new me.ducpro.westworldcore.commands.PlayerCommands(this));
		getCommand("roll").setExecutor(new me.ducpro.westworldcore.commands.PlayerCommands(this));
		getCommand("desc").setExecutor(new me.ducpro.westworldcore.commands.PlayerCommands(this));
		getCommand("spit").setExecutor(new me.ducpro.westworldcore.commands.PlayerCommands(this));
		getCommand("fight").setExecutor(new me.ducpro.westworldcombat.commands.FightCommand(this));
		getCommand("me").setExecutor(new me.ducpro.westworldcharacter.commands.ProfileCommand(this));
		getCommand("duel").setExecutor(new me.ducpro.westworldcombat.commands.DuelCommand(this));
		
		getCommand("help").setExecutor(new me.ducpro.westworldranks.commands.HelpCommand(this));
		getCommand("ban").setExecutor(new me.ducpro.westworldranks.commands.BanCommand(this));
		getCommand("unban").setExecutor(new me.ducpro.westworldranks.commands.UnbanCommand(this));
		getCommand("kick").setExecutor(new me.ducpro.westworldranks.commands.KickCommand(this));
		getCommand("mute").setExecutor(new me.ducpro.westworldranks.commands.MuteCommand(this));
		getCommand("unmute").setExecutor(new me.ducpro.westworldranks.commands.UnmuteCommand(this));
		getCommand("staff").setExecutor(new me.ducpro.westworldranks.commands.StaffChatCommand(this));
		getCommand("promote").setExecutor(new me.ducpro.westworldranks.commands.PromoteCommand(this));
		getCommand("demote").setExecutor(new me.ducpro.westworldranks.commands.DemoteCommand(this));
		getCommand("identify").setExecutor(new me.ducpro.westworldranks.commands.IdentifyCommand(this));
		getCommand("teleport").setExecutor(new me.ducpro.westworldranks.commands.TPCommand(this));
		getCommand("broadcast").setExecutor(new me.ducpro.westworldranks.commands.BroadcastCommand(this));
		getCommand("noclip").setExecutor(new me.ducpro.westworldranks.commands.NoClipCommand(this));
		
		
		getServer().getPluginManager().registerEvents(new me.ducpro.westworldintro.listeners.PlayerEvents(this), this);
		getServer().getPluginManager().registerEvents(new me.ducpro.westworldintro.listeners.NPCEvents(this), this);
		
		getServer().getPluginManager().registerEvents(new me.ducpro.westworldcore.listeners.PlayerEvents(this), this);
		getServer().getPluginManager().registerEvents(new me.ducpro.westworldcore.listeners.ChairEvents(this), this);
		getServer().getPluginManager().registerEvents(new me.ducpro.westworldcore.listeners.InteractEvents(this), this);
		getServer().getPluginManager().registerEvents(new me.ducpro.westworldcore.listeners.CommandEvents(this), this);
		
		getServer().getPluginManager().registerEvents(new me.ducpro.westworldchat.listeners.ChatEvents(this), this);
		
		getServer().getPluginManager().registerEvents(new me.ducpro.westworldhouse.listeners.GUIEvents(this), this);
		getServer().getPluginManager().registerEvents(new me.ducpro.westworldhouse.listeners.PlayerEvents(this), this);
		
		getServer().getPluginManager().registerEvents(new me.ducpro.westworldhorse.listeners.GUIEvents(this), this);
		getServer().getPluginManager().registerEvents(new me.ducpro.westworldhorse.listeners.PlayerEvents(this), this);
		getServer().getPluginManager().registerEvents(new me.ducpro.westworldhorse.listeners.HorseEvents(this), this);
		
		getServer().getPluginManager().registerEvents(new me.ducpro.westworldheist.listeners.PlayerListeners(this), this);
		
		getServer().getPluginManager().registerEvents(new me.ducpro.westworldtrain.listeners.PlayerListeners(this), this);
		
		getServer().getPluginManager().registerEvents(new me.ducpro.westworldcombat.listeners.PlayerEvents(this), this);
		
		getServer().getPluginManager().registerEvents(new me.ducpro.westworldcharacter.listeners.CrimeListeners(this), this);
		getServer().getPluginManager().registerEvents(new me.ducpro.westworldcharacter.listeners.PlayerListeners(this), this);
		
		getServer().getPluginManager().registerEvents(new me.ducpro.westworldranks.listeners.PlayerEvents(this), this);
		
		Bukkit.getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				doorfile.clear();
				for (Coordinates coord: doorinfo.keySet()) {
					DoorInfo door = doorinfo.get(coord);
					doorfile.addDoor(door);
				}
				doorfile.saveFile();
			}
		}, 1200);
		
		wanted = new WantedPosters(this);
	}
	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
		DestroyArmorstands.destroy();
		KillHorses.killHorses(this);
		RemoveAllHeists.removeAllHeists(this);
		
		doorfile.clear();
		for (Coordinates coord: doorinfo.keySet()) {
			DoorInfo door = doorinfo.get(coord);
			doorfile.addDoor(door);
		}
		doorfile.saveFile();
		
		if (train != null) {
			train.stopTrain();
			train = null;
		}
		
		for (Player p: Bukkit.getOnlinePlayers()) {
			getPlayerInfo(p).endCountPlaytime(p);
		}
		
		SpectatorReset.resetSpectators(this);
		Bukkit.getScheduler().cancelTasks(this);
	}
}
