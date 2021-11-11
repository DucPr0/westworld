package me.ducpro.westworldcore.main;

import java.util.Set;
import java.util.HashSet;
import java.util.UUID;
import java.util.Queue;
import java.util.LinkedList;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import me.ducpro.nametagutils.ChangeName;
import me.ducpro.nametagutils.SetPrefix;
import me.ducpro.westworldcore.main.Main.Coordinates;
import me.ducpro.westworldhorse.info.HorseInfo;
import me.ducpro.westworldhouse.info.DoorInfo;

public class PlayerInfo {
	private Main plugin;
	private int stage = 1, friendspage = 1, doors = 0;
	private Coordinates curdoor; //Variable representing the GUI, not the doors the player own.
	private boolean rotate = false, removestand = false;
	public boolean askingname, askingage, requestingnod;
	private boolean hatsentfirst, allowedhatchoose, issitting;
	private int chatmode;
	private String hatid = null, name = null;
	private Integer age = null;
	private Queue<UUID> horses;
	private UUID horseUUID;
	private String horseConfigID;
	private int horsepage = 1;
	private boolean requestingHorseName = false;
	private HashSet<String> robbedBanks = new HashSet<String>();
	private boolean exitChair = false;
	private String rank;
	private PermissionAttachment permAttachment;
	private boolean listenOOC = true;
	private UUID lastMessaged = null;
	private BossBar recoveryBar = null;
	private int recoveryTime;
	private int combatTime = 0;
	private int playtime = 0;
	private int lockpickTime = -1;
	private boolean inJail = false;
	private UUID duelling = null;
	private HashSet<UUID> duelRequests = new HashSet<UUID>();
	private boolean wasWanted = false;
	public PlayerInfo(Player p, Main plugin) {
		this.plugin = plugin;
		askingname = false;
		askingage = false;
		requestingnod = false;
		hatsentfirst = false;
		allowedhatchoose = false;
		issitting = false;
		rotate = false;
		removestand = false;
		name = plugin.getData().getName(p.getUniqueId());
		age = plugin.getData().getAge(p.getUniqueId());
		stage = plugin.getData().getStage(p.getUniqueId());
		chatmode = 0;
		doors = 0;
		Set<Coordinates> set = plugin.doorinfo.keySet();
		for (Coordinates coord: set) {
			DoorInfo door = plugin.doorinfo.get(coord);
			if (door.getOwner() == p.getUniqueId()) doors++;
		}
		horses = new LinkedList<UUID>();
		horseUUID = null;
		rank = plugin.getData().getRank(p.getUniqueId());
		if (stage > Main.INTRO_STAGES) {
//			NickPlugin.getPlugin().getAPI().addNameToScoreboard(name, rank, getPrefix());
			SetPrefix.setPrefix(plugin, rank, name, getPrefix());
		}
		permAttachment = p.addAttachment(plugin);
		
		recoveryBar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);
		long untilRecovery = plugin.getData().getLong(p.getUniqueId(), plugin.getData().RECOVERY, -1);
		int tmpRecoveryTime = (int) ((untilRecovery - System.currentTimeMillis()) / 1000);
		if (tmpRecoveryTime < 0) tmpRecoveryTime = 0;
		if (untilRecovery != -1) startRecoveryCountdown(p, tmpRecoveryTime, plugin.getData().getBoolean(p.getUniqueId(), plugin.getData().JAIL, false));
		//If it's -1 the player isn't recovering on shutdown or quitting.
	}
	public int getStage() {
		return stage;
	}
	public void setStage(int stage) {
		this.stage = stage;
	}
	public String getName() {
		return name;
	}
	public Integer getAge() {
		return age;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public void setChatMode(int chatmode) {
		this.chatmode = chatmode;
	}
	public int getChatMode() {
		return chatmode;
	}
	public boolean getHatSentFirst() {
		return hatsentfirst;
	}
	public void setHatSentFirst(boolean b) {
		hatsentfirst = b;
	}
	public boolean isHatAllowed() {
		return allowedhatchoose;
	}
	public void setHatAllowed(boolean b) {
		allowedhatchoose = b;
	}
	public String getHatID() {
		return hatid;
	}
	public void setHatID(String hatid) {
		this.hatid = hatid;
	}
	public boolean getRotate() {
		return rotate;
	}
	public void setRotate(boolean b) {
		rotate = b;
	}
	public boolean getSitting() {
		return issitting;
	}
	public void setSitting(boolean b) {
		issitting = b;
	}
	public boolean isRemoving() {
		return removestand;
	}
	public boolean toggleRemoveStand() {
		if (removestand) removestand = false;
		else removestand = true;
		return removestand;
	}
	public void decreaseDoors() {
		doors--;
//		Bukkit.broadcastMessage("New door count = " + doors);
	}
	public void increaseDoors() {
		doors++;
//		Bukkit.broadcastMessage("New door count = " + doors);
	}
	public void setFriendsPage(int friendspage) {
		this.friendspage = friendspage;
	}
	public int getFriendsPage() {
		return friendspage;
	}
	public Coordinates getDoor() {
		return curdoor;
	}
	public void setDoor(Coordinates coords) {
		curdoor = coords;
	}
	public boolean canRent() {
		int MAX_DOORS = Main.MAX_DOORS;
		if (rank.equals("donator") || rank.equals("mod")) MAX_DOORS = Main.DONATOR_MAX_DOORS;
		if (rank.equals("admin")) return true;
		return doors < MAX_DOORS;
	}
	public void spawnHorse(Horse h) {
		if (horses.size() == Main.MAX_HORSES) {
			UUID firsthorse = horses.remove();
			HorseInfo horse = plugin.horseinfo.get(firsthorse);
			horse.getHorse().remove();
			plugin.horseinfo.remove(firsthorse);
		}
		horses.add(h.getUniqueId());
	}
	public UUID isSpawned(String configID) {
		for (UUID uuid: horses) {
			HorseInfo horse = plugin.horseinfo.get(uuid);
			if (horse.getConfigID().equals(configID)) return uuid;
		}
		return null;
	}
	public void removeHorse(String configID) {
		Queue<UUID> newqueue = new LinkedList<UUID>();
		for (UUID uuid: horses) {
			HorseInfo horse = plugin.horseinfo.get(uuid);
			if (horse.getConfigID().equals(configID)) {
				horse.getHorse().remove();
				plugin.horseinfo.remove(uuid);
			} else {
				newqueue.add(uuid);
			}
		}
		horses = new LinkedList<>(newqueue);
	}
	public void clearAllHorses() {
		for (UUID uuid: horses) {
			HorseInfo horse = plugin.horseinfo.get(uuid);
			horse.getHorse().remove();
			plugin.horseinfo.remove(uuid);
		}
	}
	public void setHorseUUID(UUID horseUUID) {
		this.horseUUID = horseUUID;
	}
	public UUID getHorseUUID() {
		return horseUUID;
	}
	public void setHorseConfigID(String id) {
		horseConfigID = id;
	}
	public String getHorseConfigID() {
		return horseConfigID;
	}
	public void setHorsePage(int horsepage) {
		this.horsepage = horsepage;
	}
	public int getHorsePage() {
		return horsepage;
	}
	public void setRequestingHorseName(boolean b) {
		requestingHorseName = b;
	}
	public boolean isRequestingHorseName() {
		return requestingHorseName;
	}
	public void addRobbedBank(String bankname) {
		robbedBanks.add(bankname);
	}
	public void delRobbedBank(String bankname) {
		robbedBanks.remove(bankname);
	}
	public boolean getRobStatus() {
		if (robbedBanks.size() == 0) return false;
		return true;
	}
	public Set<String> getRobbedBanks() {
		return robbedBanks;
	}
	public void setExitChair(boolean b) {
		exitChair = b;
	}
	public boolean getExitChair() {
		return exitChair;
	}
	private String lasttown = null;
	public void setLastTown(String lasttown) {
		this.lasttown = lasttown;
	}
	public String getLastTown() {
		return lasttown;
	}
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	public String getPrefix() {
		if (rank.equals("donator")) return ChatColor.translateAlternateColorCodes('&', "&e&lⒹ ");
		if (rank.equals("mod")) return ChatColor.translateAlternateColorCodes('&', "&a&lⓂ ");
		if (rank.equals("admin")) return ChatColor.translateAlternateColorCodes('&', "&c&lⒶ ");
		return "";
	}
	private boolean isOpeningBankVault = false;
	public void setOpeningBankVault(boolean b) {
		isOpeningBankVault = b;
	}
	public boolean getOpeningBankVault() {
		return isOpeningBankVault;
	}
	public void addPerm(String perm) {
		permAttachment.setPermission(perm, true);
	}
	public void removePerm(String perm) {
		permAttachment.unsetPermission(perm);
	}
	public void removeAttachment(Player p) {
		p.removeAttachment(permAttachment);
	}
	public boolean getListenOOC() {
		return listenOOC;
	}
	public void setListenOOC(boolean b) {
		listenOOC = b;
	}
	public UUID getLastMessaged() {
		return lastMessaged;
	}
	public void setLastMessaged(UUID uuid) {
		lastMessaged = uuid;
	}
	public void setBarName(String name) {
		recoveryBar.setTitle(name);
	}
	public void setBarProgress(double progress) {
		recoveryBar.setProgress(progress);
	}
	public void setBarStyle(BarStyle style) {
		recoveryBar.setStyle(style);
	}
	public boolean getInJail() {
		return inJail;
	}
	public int getRecoveryTime() {
		return recoveryTime;
	}
	public void decreaseRecoveryTime() {
		recoveryTime--;
	}
	public void startRecoveryCountdown(final Player p, int secondsLeft, final boolean inJail) {
		if (secondsLeft == 0) {
			endRecoveryCountdown(p);
			return;
		}
		plugin.getData().setLong(p.getUniqueId(), plugin.getData().RECOVERY, System.currentTimeMillis() + secondsLeft * 1000);
		recoveryTime = secondsLeft;
		recoveryBar.addPlayer(p);
		this.inJail = inJail;
	}
	public void endRecoveryCountdown(final Player p) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				p.teleport(plugin.getConfigFile().getLocation("town-spawn"));
				plugin.getData().setLong(p.getUniqueId(), plugin.getData().LAST_SPAWN, System.currentTimeMillis());
			}
		}, 1);
		recoveryBar.removePlayer(p);
		plugin.getData().setLong(p.getUniqueId(), plugin.getData().RECOVERY, -1);
	}
	public void endCountdownQuit(final Player p) {
		recoveryBar.removePlayer(p);
	}
	public boolean isInRespawn() {
		if (recoveryTime == 0) return false;
		return true;
	}
	private int COMBAT_SECONDS = 300;
	public void decreaseCombatTime() {
		combatTime--;
	}
	public int getCombatTime() {
		return combatTime;
	}
	public void startCombat(final Player p) {
		if (combatTime == 0) p.sendMessage(ChatColor.RED + "You are now in combat. Logging out during this duration will kill you.");
		combatTime = COMBAT_SECONDS;
		plugin.getCombatList().addPlayer(p);
	}
	public void endCombat(Player p) {
		plugin.getCombatList().removePlayer(p);
		plugin.getFightTeam().removePlayer(p);
		if (!plugin.isWanted(p)) {
			p.sendMessage(ChatColor.GREEN + "You have been removed from combat. You can now safely logout.");
		}
	}
	public void increasePlaytime() {
		playtime++;
	}
	public int getPlaytime() {
		return playtime;
	}
	public void endCountPlaytime(Player p) {
		int curPlaytime = plugin.getData().getInt(p.getUniqueId(), plugin.getData().PLAYTIME, 0);
		plugin.getData().setInt(p.getUniqueId(), plugin.getData().PLAYTIME, curPlaytime + playtime);
	}
	private Location lastNoclipped = null;
	public void setLastNoclip(Location loc) {
		lastNoclipped = loc;
	}
	public Location getLastNoclip() {
		return lastNoclipped;
	}
	public void setLockpickTime(int lockpickTime) {
		this.lockpickTime = lockpickTime;
	}
	public int getLockpickTime() {
		return lockpickTime;
	}
	public void decreaseLockpickTime() {
		lockpickTime--;
	}
	public void setDuelling(UUID duelling) {
		this.duelling = duelling;
	}
	public boolean isDuellingAgainst(Player target) {
		if (duelling == null) return false;
		return duelling.equals(target.getUniqueId());
	}
	public void addDuelRequest(UUID uuid) {
		duelRequests.add(uuid);
	}
	public boolean isRequestingDuel(UUID uuid) {
		return duelRequests.contains(uuid);
	}
	public void removeDuelRequest(UUID uuid) {
		duelRequests.remove(uuid);
	}
	public boolean isDuelling() {
		return duelling != null;
	}
	public boolean wasWanted() {
		return wasWanted;
	}
	public void setWasWanted(boolean b) {
		wasWanted = b;
	}
}
