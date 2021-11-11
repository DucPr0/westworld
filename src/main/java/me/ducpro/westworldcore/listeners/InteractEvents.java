package me.ducpro.westworldcore.listeners;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.ducpro.westworldcore.main.Main;

public class InteractEvents implements Listener {
	private Main plugin;
	public InteractEvents(Main plugin) {
		this.plugin = plugin;
	}
	private static List<Material> interactables = Arrays.asList(
            Material.ACACIA_DOOR,
            Material.ACACIA_FENCE_GATE,
            Material.ANVIL,
            Material.BEACON,
            Material.WHITE_BED,
            Material.BLACK_BED,
            Material.BLUE_BED,
            Material.RED_BED,
            Material.BROWN_BED,
            Material.CYAN_BED,
            Material.GRAY_BED,
            Material.GREEN_BED,
            Material.LIGHT_BLUE_BED,
            Material.LIGHT_GRAY_BED,
            Material.LIME_BED,
            Material.MAGENTA_BED,
            Material.ORANGE_BED,
            Material.PINK_BED,
            Material.PURPLE_BED,
            Material.YELLOW_BED,
            Material.BIRCH_DOOR,
            Material.BIRCH_FENCE_GATE,
            Material.OAK_BOAT,
            Material.ACACIA_BOAT,
            Material.BIRCH_BOAT,
            Material.DARK_OAK_BOAT,
            Material.JUNGLE_BOAT,
            Material.SPRUCE_BOAT,
            Material.BREWING_STAND,
            Material.CHEST,
            Material.DARK_OAK_DOOR,
            Material.DARK_OAK_FENCE_GATE,
            Material.DAYLIGHT_DETECTOR,
            Material.DISPENSER,
            Material.DROPPER,
            Material.ENCHANTING_TABLE,
            Material.ENDER_CHEST,
            Material.ACACIA_FENCE_GATE,
            Material.BIRCH_FENCE_GATE,
            Material.DARK_OAK_FENCE_GATE,
            Material.SPRUCE_FENCE_GATE,
            Material.JUNGLE_FENCE_GATE,
            Material.OAK_FENCE_GATE,
            Material.FURNACE,
            Material.HOPPER,
            Material.HOPPER_MINECART,
            Material.ITEM_FRAME,
            Material.JUNGLE_DOOR,
            Material.JUNGLE_FENCE_GATE,
            Material.LEVER,
            Material.MINECART,
            Material.CHEST_MINECART,
            Material.NOTE_BLOCK,
            Material.SIGN,
            Material.ACACIA_TRAPDOOR,
            Material.BIRCH_TRAPDOOR,
            Material.DARK_OAK_TRAPDOOR,
            Material.JUNGLE_TRAPDOOR,
            Material.OAK_TRAPDOOR,
            Material.SPRUCE_TRAPDOOR,
            Material.TRAPPED_CHEST,
            Material.WALL_SIGN,
            Material.ACACIA_BUTTON,
            Material.BIRCH_BUTTON,
            Material.DARK_OAK_BUTTON,
            Material.JUNGLE_BUTTON,
            Material.OAK_BUTTON,
            Material.SPRUCE_BUTTON,
            Material.OAK_DOOR,
            Material.ACACIA_DOOR,
            Material.BIRCH_DOOR,
            Material.DARK_OAK_DOOR,
            Material.JUNGLE_DOOR,
            Material.SPRUCE_DOOR,
            Material.SHULKER_BOX,
            Material.BLACK_SHULKER_BOX,
            Material.BLUE_SHULKER_BOX,
            Material.BROWN_SHULKER_BOX,
            Material.CYAN_SHULKER_BOX,
            Material.GRAY_SHULKER_BOX,
            Material.GREEN_SHULKER_BOX,
            Material.LIGHT_BLUE_SHULKER_BOX,
            Material.LIGHT_GRAY_SHULKER_BOX,
            Material.LIME_SHULKER_BOX,
            Material.MAGENTA_SHULKER_BOX,
            Material.ORANGE_SHULKER_BOX,
            Material.PINK_SHULKER_BOX,
			Material.PURPLE_SHULKER_BOX,
			Material.RED_SHULKER_BOX,
			Material.WHITE_SHULKER_BOX,
			Material.YELLOW_SHULKER_BOX);
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (p.isOp()) return;
		
		Block b = e.getClickedBlock();
		if (b == null) return;
		
		if (interactables.contains(b.getType())) {
			if (!plugin.isAllowed(b.getType())) {
				e.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof Player || e.getRightClicked() instanceof Villager) return;
		Player p = e.getPlayer();
		if (p.isOp()) return;
		e.setCancelled(true);
	}
	@EventHandler
	public void onPaintingBreak(HangingBreakByEntityEvent e) {
		if (e.getEntity() instanceof Painting && e.getRemover() != null && e.getRemover() instanceof Player) {
			Player p = (Player) e.getRemover();
			if (p.isOp()) return;
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onItemFrameBreak(HangingBreakByEntityEvent e) {
		if (e.getEntity() instanceof ItemFrame) {
			if (e.getRemover() instanceof Player) {
				Player p = (Player) e.getRemover();
				if (p.isOp()) return;
			}
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onItemFrameItemBreak(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof ItemFrame) {
			if (e.getDamager() instanceof Player) {
				Player p = (Player) e.getDamager();
				if (p.isOp()) return;
			}
			e.setCancelled(true);
		}
	}
}
