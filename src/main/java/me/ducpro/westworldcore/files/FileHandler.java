package me.ducpro.westworldcore.files;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.*;
import org.bukkit.configuration.ConfigurationSection;

import me.ducpro.westworldcore.main.*;
import me.ducpro.westworldcore.main.Main.*;
import me.ducpro.westworldhouse.info.DoorInfo;

public class FileHandler {
	private Main plugin;
	private File file;
	private FileConfiguration config;
	public FileHandler(Main plugin) {
		this.plugin = plugin;
	}
	public void loadFile(String path) {
		plugin.saveResource(path, false);
		file = new File(plugin.getDataFolder(), path);
		config = YamlConfiguration.loadConfiguration(file);
	}
	public void reloadFile() {
		config = YamlConfiguration.loadConfiguration(file);
	}
	public void saveFile() {
		try {
			config.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public int getInt(String path, int defaultValue) {
		if (!config.contains(path)) setInt(path, defaultValue, true);
		return config.getInt(path);
	}
	public double getDouble(String path) {
		return config.getDouble(path);
	}
	public String getString(String path) {
		return config.getString(path);
	}
	public void setInt(String path, int val, boolean save) {
		config.set(path, val);
		if (save) saveFile();
	}
	public void setDouble(String path, double val, boolean save) {
		config.set(path, val);
		if (save) saveFile();
	}
	public void setString(String path, String s, boolean save) {
		config.set(path, s);
		if (save) saveFile();
	}
	public void setLocation(String path, Location loc) {
		setString(path + ".world", loc.getWorld().getName(), false);
		setDouble(path + ".x", loc.getX(), false);
		setDouble(path + ".y", loc.getY(), false);
		setDouble(path + ".z", loc.getZ(), false);
		setDouble(path + ".yaw", loc.getYaw(), false);
		setDouble(path + ".pitch", loc.getPitch(), false);
		saveFile();
	}
	public Location getLocation(String path) {
		double x = getDouble(path + ".x"), y = getDouble(path + ".y"), z = getDouble(path + ".z");
		World world = Bukkit.getWorld(getString(path + ".world"));
		double yaw = getDouble(path + ".yaw"), pitch = getDouble(path + ".pitch");
		return new Location(world, x, y, z, (float) yaw, (float) pitch);
	}
	private final List<String> emptylist = new ArrayList<String>();
	private List<String> getStringList(String path) {
		if (!config.contains(path)) {
			config.set(path, emptylist);
		}
		return config.getStringList(path);
	}
	public void addItem(String path, String item) {
		List<String> list = getStringList(path);
		list.add(item);
		config.set(path, list);
		saveFile();
	}
	public boolean removeItem(String path, String item) {
		List<String> list = getStringList(path);
		if (list.contains(item)) {
			list.remove(item);
			config.set(path, list);
			saveFile();
			return true;
		}
		return false;
	}
	public boolean isInList(String path, String item) {
		List<String> list = getStringList(path);
		if (list.contains(item)) return true;
		return false;
	}
	//doors.yml exclusive functions
	public void clear() {
		Set<String> keys = config.getKeys(false);
		for (String s: keys) config.set(s, null);
		setInt("door-count", 0, false);
	}
	public void loadDoors() {
		Set<String> keys = config.getKeys(false);
		for (String s: keys) {
			if (s.equals("door-count")) continue;
			DoorInfo door = getDoor(s);
			Coordinates coord = door.getCoords();
			plugin.doorinfo.put(coord, door);
		}
	}
	public void addDoor(DoorInfo doorinfo) {
		int doorid = getInt("door-count", 0);
		doorid++;
		setInt("door-count", doorid, false);
		String path = "door" + doorid;
		setString(path + ".world", doorinfo.getWorld().getName(), false);
		setInt(path + ".x", doorinfo.getX(), false);
		setInt(path + ".y", doorinfo.getY(), false);
		setInt(path + ".z", doorinfo.getZ(), false);
		setString(path + ".type", doorinfo.getType().toString(), false);
		setInt(path + ".secondsleft", doorinfo.getSecondsLeft(), false);
		setString(path + ".owner", doorinfo.getOwner().toString(), false);
		config.set(path + ".allowed", doorinfo.getAllowed());
	}
	public DoorInfo getDoor(String path) {
		World world = Bukkit.getWorld(getString(path + ".world"));
		int x = getInt(path + ".x", -1), y = getInt(path + ".y", -1), z = getInt(path + ".z", -1);
		Material type = Material.getMaterial(getString(path + ".type"));
		int secondsleft = getInt(path + ".secondsleft", -1);
		UUID owner = UUID.fromString(getString(path + ".owner"));
		List<String> allowed = config.getStringList(path + ".allowed");
		HashSet<UUID> set = new HashSet<UUID>();
		for (String uuid: allowed) {
			set.add(UUID.fromString(uuid));
		}
		DoorInfo doorinfo = new DoorInfo(world, x, y, z, type, secondsleft, owner, plugin);
		doorinfo.setAllowed(set);
		return doorinfo;
	}
	
	//horse - spawnpoints exclusive functions
	public ConfigurationSection getHorseSpawnpoints() {
		String path = "horse-spawnpoints";
		if (!config.contains(path)) {
			config.createSection(path);
			config.set(path + ".count", 0);
			saveFile();
		}
		return config.getConfigurationSection(path);
	}
	public void addSpawnpoint(Location loc) {
		String path = "horse-spawnpoints";
		if (!config.contains(path)) {
			config.createSection(path);
			config.set(path + ".count", 0);
		}
		int curcount = getInt(path + ".count", 0);
		curcount++;
		setInt(path + ".count", curcount, false);
		String nextid = "spawn" + curcount;
		setLocation(path + "." + nextid, loc);
	}
	public boolean removeSpawnpoint(String id) {
		String path = "horse-spawnpoints";
		if (!config.contains(path + "." + id)) return false;
		config.set(path + "." + id, null);
		saveFile();
		return true;
	}
	
	//banks exclusive location
	public void addBank(String bankname) {
		if (!config.contains("banks")) {
			config.createSection("banks");
		}
		config.getConfigurationSection("banks").createSection(bankname);
		config.set("banks." + bankname + ".chestcount", 0);
		config.set("banks." + bankname + ".lastrobbed", 0);
		saveFile();
	}
	public boolean bankExist(String bankname) {
		if (!config.contains("banks")) {
			config.createSection("banks");
			saveFile();
		}
		return config.getConfigurationSection("banks").contains(bankname);
	}
	public void removeBank(String bankname) {
		config.set("banks." + bankname, null);
		saveFile();
	}
	public void addChest(String bankname, int x, int y, int z, String worldname, int amount) {
		int curcount = config.getInt("banks." + bankname + ".chestcount");
		curcount++;
		config.set("banks." + bankname + ".chestcount", curcount);
		String chestpath = "banks." + bankname + ".chest" + curcount;
		config.set(chestpath + ".world", worldname);
		config.set(chestpath + ".x", x);
		config.set(chestpath + ".y", y);
		config.set(chestpath + ".z", z);
		config.set(chestpath + ".amount", amount);
		saveFile();
	}
	public ConfigurationSection getBanks() {
		if (!config.contains("banks")) {
			config.createSection("banks");
			saveFile();
		}
		return config.getConfigurationSection("banks");
	}
	public ConfigurationSection getChests(String bankname) {
		if (!bankExist(bankname)) return null;
		return config.getConfigurationSection("banks").getConfigurationSection(bankname);
	}
	public boolean removeChest(String bankname, String configID) {
		if (!bankExist(bankname)) return false;
		if (!config.getConfigurationSection("banks").getConfigurationSection(bankname).contains(configID)) return false;
		config.set("banks." + bankname + "." + configID, null);
		saveFile();
		return true;
	}
	public Coordinates getChest(String bankname, String configID) {
		int x = config.getInt("banks." + bankname + "." + configID + ".x");
		int y = config.getInt("banks." + bankname + "." + configID + ".y");
		int z = config.getInt("banks." + bankname + "." + configID + ".z");
		World world = Bukkit.getWorld(config.getString("banks." + bankname + "." + configID + ".world"));
		return new Coordinates(world, x, y, z);
	}
	public void setLastHeist(String bankname, long last) {
		config.set("banks." + bankname + ".lastrobbed", last);
		saveFile();
	}
	public long getLastHeist(String bankname) {
		return config.getLong("banks." + bankname + ".lastrobbed");
	}
	//Trains - exclusive functions
	public void addTrainDefault() {
		config.createSection("trains");
	}
	public void addTown(String townname, Location loc) {
		if (!config.contains("trains")) {
			addTrainDefault();
		}
		config.set("trains." + townname + ".order", -1);
		setLocation("trains." + townname, loc);
		saveFile();
	}
	public ConfigurationSection listTowns() {
		if (!config.contains("trains")) {
			addTrainDefault();
			saveFile();
		}
		return config.getConfigurationSection("trains");
	}
	public boolean removeTown(String townname) {
		if (!config.contains("trains")) {
			addTrainDefault();
			saveFile();
		}
		if (config.contains("trains." + townname)) {
			config.set("trains." + townname, null);
			saveFile();
			return true;
		}
		return false;
	}
	public boolean setOrder(String townname, int order) {
		if (!config.contains("trains")) {
			addTrainDefault();
			saveFile();
		}
		if (config.contains("trains." + townname)) {
			config.set("trains." + townname + ".order", order);
			saveFile();
			return true;
		}
		return false;
	}
	public int getOrder(String townname) {
		if (townname == null) return -1;
		if (!config.contains("trains")) {
			addTrainDefault();
			saveFile();
		}
		if (config.contains("trains." + townname)) {
			return config.getInt("trains." + townname + ".order");
		}
		return -1;
	}
	public boolean getTrainStart() {
		if (!config.contains("trainstart")) {
			config.set("trainstart", false);
			saveFile();
		}
		return config.getBoolean("trainstart");
	}
	public void setTrainStart(boolean b) {
		config.set("trainstart", b);
		saveFile();
	}
	//Death locations
	public void createSection(String path) {
		config.createSection(path);
		config.set(path + ".count", 0);
		saveFile();
	}
	public void addLocation(String path, Location loc) {
		if (!config.contains(path)) createSection(path);
		int curcnt = config.getInt(path + ".count");
		curcnt++;
		config.set(path + ".count", curcnt);
		setLocation(path + ".loc" + curcnt, loc);
		saveFile();
	}
	public List<Location> getLocations(String path) {
		List<Location> list = new ArrayList<Location>();
		if (!config.contains(path)) createSection(path);
		for (String key: config.getConfigurationSection(path).getKeys(false)) {
			if (key.equals("count")) continue;
			list.add(getLocation(path + "." + key));
		}
		return list;
	}
	public boolean removeLocation(String path) {
		if (!config.contains(path)) return false;
		config.set(path, null);
		return true;
	}
	public ConfigurationSection getSection(String path) {
		return config.getConfigurationSection(path);
	}
}
