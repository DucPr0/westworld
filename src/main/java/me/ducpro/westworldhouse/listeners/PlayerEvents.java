package me.ducpro.westworldhouse.listeners;

import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Openable;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldcore.main.Main.Coordinates;
import me.ducpro.westworldcore.utils.GetItemDamage;
import me.ducpro.westworldhouse.guis.OtherDoor;
import me.ducpro.westworldhouse.guis.OwnDoor;
import me.ducpro.westworldhouse.guis.PublicDoor;
import me.ducpro.westworldhouse.info.DoorInfo;
import me.ducpro.westworldhouse.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class PlayerEvents implements Listener {
	private Main plugin;
	public PlayerEvents(Main plugin) {
		this.plugin = plugin;
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void onJoin(PlayerJoinEvent e) {
		Set<Coordinates> set = plugin.doorinfo.keySet();
		for (Coordinates coord: set) {
			if (plugin.doorinfo.get(coord).getOwner().equals(e.getPlayer().getUniqueId())) {
				plugin.getPlayerInfo(e.getPlayer()).increaseDoors();
			}
		}
	}
	@EventHandler
	public void onLeftClickDoor(PlayerAnimationEvent e) {
		Player p = e.getPlayer();
		if (p.getGameMode() == GameMode.ADVENTURE) {
			Block b = p.getTargetBlock(null, 4);
			if (!Utils.isDoor(b.getType())) return;
			
			Coordinates coord = Utils.getLowerDoor(b);
			if (plugin.interacteddoor.contains(coord)) {
				plugin.interacteddoor.remove(coord);
				return;
			}
			
			if (plugin.doorinfo.containsKey(coord) == false) {
				PublicDoor.publicDoorLeftClick(plugin, p, b);
			} else {
				DoorInfo door = plugin.doorinfo.get(coord);
				if (door.getOwner().equals(e.getPlayer().getUniqueId())) OwnDoor.ownDoorLeftClick(plugin, p, door);
				else {
					boolean islockpick = false;
					ItemStack istack = p.getInventory().getItemInMainHand();
					if (istack.getType() == Material.DIAMOND_PICKAXE && istack.hasItemMeta() && istack.getItemMeta().hasDisplayName()) {
						String name = ChatColor.stripColor(istack.getItemMeta().getDisplayName());
						if (name.equals("Lockpick") && GetItemDamage.getDamage(istack) == 2) {
							islockpick = true;
						}
					}
					if (islockpick) OtherDoor.otherDoorLeftClick(plugin, p, door);
					else OtherDoor.otherDoorNoLockpick(plugin, p, door);
				}
			}
		}
	}
	@EventHandler
	public void onClickDoor(PlayerInteractEvent e) {
		if (e.getClickedBlock() == null) return;
		if (Utils.isDoor(e.getClickedBlock().getType())) {
			if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
				Coordinates coord = Utils.getLowerDoor(e.getClickedBlock());
				if (plugin.doorinfo.containsKey(coord) == false) {
					PublicDoor.publicDoorLeftClick(plugin, e.getPlayer(), e.getClickedBlock());
				} else {
					DoorInfo door = plugin.doorinfo.get(coord);
					if (door.getOwner().equals(e.getPlayer().getUniqueId())) OwnDoor.ownDoorLeftClick(plugin, e.getPlayer(), door);
					else OtherDoor.otherDoorLeftClick(plugin, e.getPlayer(), door); 
				}
			} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				//InteractEvent is called before AnimationEvent.
				Coordinates coord = Utils.getLowerDoor(e.getClickedBlock());
				plugin.interacteddoor.add(coord);
				if (plugin.doorinfo.containsKey(coord)) {
					DoorInfo door = plugin.doorinfo.get(coord);
					if (!door.isAllowed(e.getPlayer().getUniqueId())) {
						e.getPlayer().sendMessage(ChatColor.RED + "You can't open this door!");
						e.setCancelled(true);
						return;
					} else {
						if (!door.canInteract(System.currentTimeMillis())) {
							e.setCancelled(true);
							e.getPlayer().sendMessage(ChatColor.RED + "You cannot close the door now! It was recently lockpicked.");
						}
					}
				}
			}
		}
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (plugin.getLockpickTeam().isInTeam(e.getPlayer())) {
			e.getPlayer().setHealth(0);
			e.getPlayer().spigot().respawn();
			plugin.getLockpickTeam().removePlayer(e.getPlayer());
		}
	}
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		final Player p = e.getPlayer();
		final UUID uuid = p.getUniqueId();
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				if (p == null) {
					plugin.getLockpickTeam().removeUUIDFromList(uuid);
					return;
				}
				plugin.getLockpickTeam().removePlayer(p);
				if (!plugin.isWanted(p) && plugin.getPlayerInfo(p).wasWanted()) p.sendMessage(ChatColor.GREEN + "You are no longer wanted.");
				if (!plugin.isWanted(p)) plugin.getPlayerInfo(p).setWasWanted(false);
			}
		}, 1);
	}
}
