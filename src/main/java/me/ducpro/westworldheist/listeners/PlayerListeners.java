package me.ducpro.westworldheist.listeners;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldcore.main.Main.Coordinates;
import me.ducpro.westworldcore.utils.GetItemDamage;
import me.ducpro.westworldcore.main.PlayerInfo;
import me.ducpro.westworldheist.info.ChestInfo;
import me.ducpro.westworldheist.utils.GetBank;
import me.ducpro.westworldheist.utils.VaultExplosion;
import net.md_5.bungee.api.ChatColor;

public class PlayerListeners implements Listener {
	private Main plugin;
	public PlayerListeners(Main plugin) {
		this.plugin = plugin;
	}
	@EventHandler
	public void chestOpen(PlayerInteractEvent e) {
		if (!e.hasBlock()) return;
		if (e.getClickedBlock().getType() == Material.CHEST) {
			Coordinates coord = new Coordinates(e.getClickedBlock());
			if (!plugin.chestinfo.containsKey(coord)) {
				plugin.getPlayerInfo(e.getPlayer()).setOpeningBankVault(false);
				return;
			}
			//Opened a bank chest.
			ChestInfo chestinfo = plugin.chestinfo.get(coord);
			String bankname = chestinfo.getBank();
			if (!plugin.heistinfo.containsKey(bankname)) return;
			plugin.getPlayerInfo(e.getPlayer()).addRobbedBank(chestinfo.getBank());
			plugin.getPlayerInfo(e.getPlayer()).setOpeningBankVault(true);
			plugin.heistinfo.get(bankname).addCriminal(e.getPlayer());
			if (!plugin.getHeistTeam().isInTeam(e.getPlayer())) {
				plugin.getHeistTeam().addPlayer(e.getPlayer());
				
				//Add heist to list of crimes
				int curHeists = plugin.getData().getInt(e.getPlayer().getUniqueId(), plugin.getData().HEISTS, 0);
				plugin.getData().setInt(e.getPlayer().getUniqueId(), plugin.getData().HEISTS, curHeists + 1);
				
				for (Entity entity: e.getPlayer().getNearbyEntities(300, 100, 300)) {
					if (entity instanceof Player) {
						Player p = (Player) entity;
						p.sendTitle(ChatColor.RED + "Another outlaw", ChatColor.RED + "is robbing the bank!", 10, 70, 20);
					}
				}
				e.getPlayer().sendMessage(ChatColor.RED + "You've attempted to rob the bank. You are now wanted.");
			}
		}
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if (plugin.getHeistTeam().isInTeam(p)) {
			//Criminals quitting will die.
			p.setHealth(0);
			p.spigot().respawn();
		}
	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		PlayerInfo pinfo = plugin.getPlayerInfo(p);
		Set<String> robbedBanks = pinfo.getRobbedBanks();
		for (String bankname: robbedBanks) {
			pinfo.delRobbedBank(bankname);
			plugin.heistinfo.get(bankname).removeCriminal(p);
		}
		if (plugin.getHeistTeam().isInTeam(p)) {
			for (Entity entity: p.getNearbyEntities(300, 100, 300)) {
				if (entity instanceof Player) {
					if (((Player) entity).getUniqueId() != p.getUniqueId())
						entity.sendMessage(ChatColor.YELLOW + "A bank robber has been taken out!");
				}
			}
//			plugin.getHeistTeam().removePlayer(p);
//			p.sendMessage(ChatColor.GREEN + "You are no longer wanted.");
		}
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		final Player p = e.getPlayer();
		final UUID uuid = p.getUniqueId();
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				if (p == null) {
					plugin.getHeistTeam().removeUUIDFromList(uuid);
					return;
				}
				if (plugin.getHeistTeam().isInTeam(p)) {
					plugin.getHeistTeam().removePlayer(p);
					if (!plugin.isWanted(p) && plugin.getPlayerInfo(p).wasWanted()) p.sendMessage(ChatColor.GREEN + "You are no longer wanted.");
					if (!plugin.isWanted(p)) plugin.getPlayerInfo(p).setWasWanted(false);
				}
			}
		}, 1);
	}
	@EventHandler
	public void onPickupNugget(InventoryClickEvent e) {
		Player p = Bukkit.getPlayer(e.getWhoClicked().getUniqueId());
		if (!plugin.getPlayerInfo(p).getOpeningBankVault()) return; //Not opening bank vault.
		if (plugin.getHeistTeam().isInTeam(p)) {
			if (e.getRawSlot() >= e.getInventory().getSize()) {
				if (e.getClick() == ClickType.DOUBLE_CLICK) {
					e.setCancelled(true);
					return;
				}
			}
		}
		if (plugin.getHeistTeam().isInTeam(p) && e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.GOLD_NUGGET) {
			//Player is wanted. Only let him pick up 1 gold nugget at a time.
			if (e.getRawSlot() >= e.getInventory().getSize()) return; //Placing nuggets.
			e.setCancelled(true);
			p.setItemOnCursor(new ItemStack(Material.GOLD_NUGGET, 1));
			int curamount = e.getCurrentItem().getAmount();
			if (curamount > 1) {
				e.setCurrentItem(new ItemStack(Material.GOLD_NUGGET, curamount - 1));
			} else {
				e.setCurrentItem(new ItemStack(Material.AIR, 1));
			}
		}
		if (plugin.getHeistTeam().isInTeam(p) && e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.GOLD_INGOT) {
			//Player is wanted. Only let him pick up 1 gold nugget at a time.
			if (e.getRawSlot() >= e.getInventory().getSize()) return; //Placing nuggets.
			e.setCancelled(true);
			p.setItemOnCursor(new ItemStack(Material.GOLD_INGOT, 1));
			int curamount = e.getCurrentItem().getAmount();
			if (curamount > 1) {
				e.setCurrentItem(new ItemStack(Material.GOLD_INGOT, curamount - 1));
			} else {
				e.setCurrentItem(new ItemStack(Material.AIR, 1));
			}
		}
	}
	private long HEIST_DIFFERENCE = 3600000;
	@EventHandler
	public void onPlaceExplosives(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof ItemFrame) {
			ItemFrame iframe = (ItemFrame) e.getRightClicked();
			ItemStack placeexplosives = iframe.getItem();
			if (placeexplosives == null) return;
			if (placeexplosives.getType() == Material.STONE_PICKAXE && GetItemDamage.getDamage(placeexplosives) == 10) {
				ItemStack explosives = e.getPlayer().getInventory().getItemInMainHand();
				if (explosives.getType() == Material.DIAMOND_PICKAXE && GetItemDamage.getDamage(explosives) == 1) {
					final String bankname = GetBank.getBank(plugin, iframe.getLocation());
					if (bankname == null) return;
					long lastheist = plugin.getConfigFile().getLastHeist(bankname);
					long diff = System.currentTimeMillis() - lastheist;
					if (!e.getPlayer().isOp() && diff < HEIST_DIFFERENCE) {
						e.getPlayer().sendMessage(ChatColor.RED + "The bank cannot be robbed now!");
						return;
					}
					if (!e.getPlayer().isOp() && Bukkit.getOnlinePlayers().size() < 10) {
						e.getPlayer().sendMessage(ChatColor.RED + "Robbing a bank requires at least 10 online players.");
						return;
					}
					e.setCancelled(true);
					e.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR, 1));
					iframe.setItem(explosives);
					e.getPlayer().playSound(e.getPlayer().getLocation(), "minecraft:ww.e.boom", SoundCategory.BLOCKS, 5, 1);
					for (Entity entity: e.getPlayer().getNearbyEntities(3, 3, 3)) {
						if (entity instanceof Player)
							((Player) entity).playSound(entity.getLocation(), "minecraft:ww.e.boom", SoundCategory.BLOCKS, 5, 1);
					}
					final ItemFrame finalexplosives = iframe;
					final Player finalp = e.getPlayer();
					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
						@Override
						public void run() {
							VaultExplosion.createExplosion(plugin, finalexplosives.getLocation(), finalp, bankname);
						}
					}, 60);
				}
			}
		}
	}
	//NOT A PLAYER EVENT.
	@EventHandler
	public void onDoorFrameBreak(HangingBreakEvent e) {
		if (e.getCause() == RemoveCause.EXPLOSION) {
			if (e.getEntity().getType() == EntityType.ITEM_FRAME) {
				e.setCancelled(true);
				ItemStack istack = ((ItemFrame) e.getEntity()).getItem();
				if (istack.getType() == Material.STONE_PICKAXE && GetItemDamage.getDamage(istack) == 10) {
					((ItemFrame) e.getEntity()).setItem(new ItemStack(Material.AIR, 1));
					e.getEntity().remove();
				}
				if (istack.getType() == Material.DIAMOND_PICKAXE && GetItemDamage.getDamage(istack) == 1) {
					((ItemFrame) e.getEntity()).setItem(new ItemStack(Material.AIR, 1));
					e.getEntity().remove();
				}
			}
		}
	}
}
