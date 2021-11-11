package me.ducpro.westworldcore.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.Inventory;

import me.ducpro.nametagutils.ChangeName;
import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldcore.main.PlayerInfo;

import net.minecraft.server.v1_13_R2.EntityPlayer;

import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;

public class PlayerEvents implements Listener {
	private Main plugin;
	public PlayerEvents(Main plugin) {
		this.plugin = plugin;
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.setJoinMessage(null);
		
		plugin.addPlayerInfo(e.getPlayer(), new PlayerInfo(e.getPlayer(), plugin));
		final PlayerInfo pinfo = plugin.getPlayerInfo(e.getPlayer());
		
		String texture, sig;
		EntityPlayer pNMS = ((CraftPlayer) e.getPlayer()).getHandle();
		GameProfile profile = pNMS.getProfile();
		Property skinProperty = profile.getProperties().get("textures").iterator().next();
		texture = skinProperty.getValue();
		sig = skinProperty.getSignature();
		
		if (pinfo.getStage() > Main.INTRO_STAGES) {
			e.getPlayer().setDisplayName(pinfo.getName());
//			NickPlugin.getPlugin().getAPI().nick(e.getPlayer(), pinfo.getName(), true, texture, sig);
			ChangeName.changeName(e.getPlayer(), pinfo.getName(), true, texture, sig);
		}
		
		String rank = plugin.getPlayerInfo(e.getPlayer()).getRank();
		List<String> memberPerms = plugin.getPermHandler().getPerms("member");
		for (String perm: memberPerms) plugin.getPlayerInfo(e.getPlayer()).addPerm(perm);
		if (!rank.equals("member")) {
			List<String> donatorPerms = plugin.getPermHandler().getPerms("donator");
			for (String perm: donatorPerms) plugin.getPlayerInfo(e.getPlayer()).addPerm(perm);
		}
		
		plugin.getData().setLong(e.getPlayer().getUniqueId(), plugin.getData().LAST_SPAWN, System.currentTimeMillis());
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent e) {
		e.setQuitMessage(null);
		
		if (plugin.getCombatList().isInCombat(e.getPlayer())) {
			e.getPlayer().setHealth(0);
			e.getPlayer().spigot().respawn();
			plugin.getCombatList().removePlayer(e.getPlayer());
			plugin.getFightTeam().removePlayer(e.getPlayer());
		}
		
		plugin.getPlayerInfo(e.getPlayer()).endCountPlaytime(e.getPlayer());
		
		plugin.getPlayerInfo(e.getPlayer()).removeAttachment(e.getPlayer());
		if (plugin.getPlayerInfo(e.getPlayer()).isInRespawn()) plugin.getPlayerInfo(e.getPlayer()).endCountdownQuit(e.getPlayer());
		plugin.removePlayerInfo(e.getPlayer());
	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player died = e.getEntity();
		Player killer = died.getKiller();
		
		if (killer != null) {
			killer.sendMessage(ChatColor.RED + "You killed " + died.getDisplayName() + ".");
			died.sendMessage(ChatColor.RED + "You were killed by " + killer.getDisplayName() + ".");
		} else {
			died.sendMessage(ChatColor.RED + "You died.");
		}
		
		e.setDeathMessage(null);
	}
	private final int HELMET_SLOT_ID = 5;
	@EventHandler
	public void onHatEquip(InventoryClickEvent e) {
		ItemStack istack = e.getCursor();
		if (istack.hasItemMeta() && istack.getItemMeta().hasDisplayName()) {
			String name = istack.getItemMeta().getDisplayName();
			if (e.getRawSlot() == HELMET_SLOT_ID) {
				if (plugin.getConfigFile().isInList("allowed-hats", name)) {
					ItemStack curhat = e.getWhoClicked().getInventory().getHelmet();
					e.setCancelled(true);
					e.getWhoClicked().getInventory().setHelmet(istack);
					if (curhat == null) e.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR, 1));
					else e.getWhoClicked().setItemOnCursor(curhat);
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onRespawn(PlayerRespawnEvent e) {
		final Player p = e.getPlayer();
		if (plugin.getPlayerInfo(p).isInRespawn()) {
			final boolean inJail = plugin.getData().getBoolean(p.getUniqueId(), plugin.getData().JAIL, false);
			//Player already dead. Just teleport them to a new location.
			String path = "normal-respawn";
			if (inJail) path = "jail-respawn";
			List<Location> list = plugin.getConfigFile().getLocations(path);
			if (list.size() == 0) e.setRespawnLocation(plugin.getConfigFile().getLocation("town-spawn"));
			else {
				Location randomLoc = list.get(new Random().nextInt(list.size()));
				e.setRespawnLocation(randomLoc);
			}
		} else {
			boolean tempInJail = plugin.isWanted(p);
			final boolean inJail = tempInJail;
			String path = "normal-respawn";
			if (inJail) path = "jail-respawn";
			List<Location> list = plugin.getConfigFile().getLocations(path);
			if (list.size() == 0) e.setRespawnLocation(plugin.getConfigFile().getLocation("town-spawn"));
			else {
				Location randomLoc = list.get(new Random().nextInt(list.size()));
				e.setRespawnLocation(randomLoc);
				int secondsLeft = Main.RESPAWN_SECONDS; //240
				if (inJail) secondsLeft = Main.JAIL_SECONDS; //600
				plugin.getPlayerInfo(p).startRecoveryCountdown(p, secondsLeft, inJail);
				plugin.getData().setBoolean(p.getUniqueId(), plugin.getData().JAIL, inJail);
			}
		}
	}
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		List<ItemStack> list = e.getDrops();
		List<ItemStack> newdrops = new ArrayList<ItemStack>();
		List<ItemStack> savedGold = new ArrayList<ItemStack>();
		for (ItemStack istack: list) {
			if (istack == null) continue;
			if (istack.hasItemMeta() && istack.getItemMeta().hasLore() && istack.getItemMeta().getLore().contains(ChatColor.AQUA + "Soulbound")) {
				continue;
			}
			if (istack.getType() == Material.GOLD_NUGGET || istack.getType() == Material.GOLD_INGOT) {
				int half = (istack.getAmount() + 1) / 2;
				int kept = half / 2, dropped = half - kept;
				newdrops.add(new ItemStack(istack.getType(), dropped));
				savedGold.add(new ItemStack(istack.getType(), kept));
			} else newdrops.add(istack);
		}
		e.setKeepInventory(true);
		
		for (int i = 0; i < e.getEntity().getInventory().getSize(); i++) {
			ItemStack istack = e.getEntity().getInventory().getItem(i);
			if (istack == null) continue;
			boolean soulbound = false;
			if (istack.hasItemMeta() && istack.getItemMeta().hasLore() && istack.getItemMeta().getLore().contains(ChatColor.AQUA + "Soulbound")) soulbound = true;
			if (!soulbound) e.getEntity().getInventory().setItem(i, new ItemStack(Material.AIR, 1));
		}
		for (ItemStack gold: savedGold) e.getEntity().getInventory().addItem(gold);
		
		for (ItemStack istack: newdrops) {
			e.getEntity().getWorld().dropItem(e.getEntity().getLocation(), istack);
		}
	}
	@EventHandler
	public void exitChairSuffocation(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player && e.getCause() == DamageCause.SUFFOCATION) {
			Player p = (Player) e.getEntity();
			if (plugin.getPlayerInfo(p).getExitChair()) e.setCancelled(true);
		}
	}
	@EventHandler
	public void onHorseInventoryOpen(InventoryOpenEvent e) {
		if (e.getInventory() instanceof HorseInventory) e.setCancelled(true);
	}
	@EventHandler
	public void onInventoryCraft(InventoryClickEvent e) {
//		Bukkit.broadcastMessage(e.getClickedInventory().getType().toString());
		if (e.getClickedInventory() instanceof CraftingInventory) {
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onCropBreak(BlockBreakEvent e) {
		if (e.getPlayer().isOp()) return;
		if (e.getBlock().getType() == Material.WHEAT) e.setCancelled(true);
	}
	@EventHandler
	public void onJumpOnCrop(PlayerInteractEvent e) {
		if (e.getClickedBlock() == null) return;
		if (e.getClickedBlock().getType() == Material.FARMLAND && e.getAction() == Action.PHYSICAL) {
			if (e.getPlayer().isOp()) return;
			e.setCancelled(true);
		}
	}
	private boolean isGun(ItemStack istack) {
		if (istack == null) return false;
		if (istack.hasItemMeta() && istack.getItemMeta().hasLore()) {
			for (String lore: istack.getItemMeta().getLore()) {
				if (lore.equals(ChatColor.RED + "Firearm")) {
					return true;
				}
			}
		}
		return false;
	}
//	@EventHandler
//	public void onPutGunInHotbar(final InventoryClickEvent e) {
//		if (e.getSlotType() == SlotType.QUICKBAR) {
//			final ItemStack istack = e.getCursor();
//			if (isGun(istack)) {
//				int cntGuns = 0;
//				for (int slotID = 0; slotID < 9; slotID++) {
//					if (slotID == e.getSlot()) continue;
//					if (isGun(e.getClickedInventory().getItem(slotID))) cntGuns++;
//				}
//				if (cntGuns >= 2) {
//					e.getWhoClicked().sendMessage(ChatColor.RED + "You can only hold two guns in your hotbar at a time!");
//				}
//			}
//		}
//	}
}
