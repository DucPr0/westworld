package me.ducpro.westworldcore.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityDismountEvent;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldcore.utils.GetItemDamage;
import me.ducpro.westworldcore.utils.SitPlayers;

public class ChairEvents implements Listener {
	private Main plugin;
	public ChairEvents(Main plugin) {
		this.plugin = plugin;
	}
	@EventHandler
	public void onPlayerStopSit(EntityDismountEvent e) {
		if (e.getEntity() instanceof Player) {
			if (e.getDismounted() instanceof ArmorStand) {
				ArmorStand as = (ArmorStand) e.getDismounted();
				if (as.getCustomName() != null && as.getCustomName().equals(ChatColor.RED + "Chair")) {
					e.getDismounted().remove();
					e.getEntity().teleport(e.getEntity().getLocation());
					plugin.getPlayerInfo((Player) e.getEntity()).setSitting(false);
					final Player p = (Player) e.getEntity();
					plugin.getPlayerInfo(p).setExitChair(true);
					Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
						@Override
						public void run() {
							plugin.getPlayerInfo(p).setExitChair(false);
						}
					}, 20);
				}
			}
		}
	}
	@EventHandler
	public void onClickChair(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof ItemFrame) {
			ItemFrame iframe = (ItemFrame) e.getRightClicked();
			ItemStack istack = iframe.getItem();
			if (istack.getType() == Material.DIAMOND_HOE) {
				int damage = GetItemDamage.getDamage(istack);
				
				double ydiff = 100;
				boolean validChair = false;
				if (damage >= 4 && damage <= 12 && damage != 11) {
					validChair = true;
					ydiff = plugin.getConfigFile().getDouble("ydiff1");
				} else if (damage >= 21 && damage <= 25) {
					validChair = true;
					ydiff = plugin.getConfigFile().getDouble("ydiff2");
				}
				
				if (!plugin.getPlayerInfo(e.getPlayer()).getRotate()) {
					if (validChair) {
						e.setCancelled(true);
						
						Location loc = iframe.getLocation();
						loc.setYaw((float) me.ducpro.westworldcore.utils.GetYawFromRotation.getYaw(iframe.getRotation()));
						loc.setPitch(0);
						e.getPlayer().teleport(loc);
						
						final Player finalp = e.getPlayer();
						final double finalydiff = ydiff;
						Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
							@Override
							public void run() {
								SitPlayers.sit(finalp, finalydiff);
							}
						}, 1);
					}
				}
			}
		}
	}
	@EventHandler
	public void onPlayerClickTrainChair(PlayerInteractAtEntityEvent e) {
		if (e.getRightClicked() instanceof ArmorStand) {
			ArmorStand as = (ArmorStand) e.getRightClicked();
			if (as.getCustomName() != null && as.getCustomName().equals(ChatColor.RED + "TRAIN CHAIR")) {
				if (!plugin.getPlayerInfo(e.getPlayer()).isRemoving()) as.addPassenger(e.getPlayer());
				else {
					as.remove();
					e.getPlayer().sendMessage(ChatColor.GREEN + "Removed armor stand.");
				}
			}
		}
	}
	@EventHandler
	public void onPlayerStopSitTrainChair(EntityDismountEvent e) {
		if (e.getEntity() instanceof Player) {
			if (e.getDismounted() instanceof ArmorStand) {
				ArmorStand as = (ArmorStand) e.getDismounted();
				if (as.getCustomName() != null && as.getCustomName().equals(ChatColor.RED + "TRAIN CHAIR")) {
					e.getEntity().teleport(e.getEntity().getLocation());
					plugin.getPlayerInfo((Player) e.getEntity()).setSitting(false);
					final Player p = (Player) e.getEntity();
					plugin.getPlayerInfo(p).setExitChair(true);
//					Bukkit.broadcastMessage("Set exitChair to true");
					Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
						@Override
						public void run() {
							plugin.getPlayerInfo(p).setExitChair(false);
//							Bukkit.broadcastMessage("Set exitChair to false");
						}
					}, 20);
				}
			}
		}
	}
}
