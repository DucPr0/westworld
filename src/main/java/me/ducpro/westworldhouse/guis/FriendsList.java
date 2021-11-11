package me.ducpro.westworldhouse.guis;

import java.util.HashSet;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldcore.main.Main.Coordinates;
import me.ducpro.westworldhouse.info.DoorInfo;
import me.ducpro.westworldhouse.utils.UtilStacks;
import me.ducpro.westworldhouse.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class FriendsList {
	private static final int HEADS_PER_PAGE = 27;
	public static void openAddPlayerList(Main plugin, Player p, int page) {
		plugin.getPlayerInfo(p).setFriendsPage(page);
		
		Inventory inv = Bukkit.createInventory(null, 36, Utils.colored("&fAdd friends"));
		
		int id = 0, start = HEADS_PER_PAGE * (page - 1), end = HEADS_PER_PAGE * page - 1;
		HashSet<UUID> set = new HashSet<UUID>();
		for (Player player: Bukkit.getOnlinePlayers()) if (!player.getUniqueId().equals(p.getUniqueId())) {
			if (plugin.getPlayerInfo(p).getStage() > Main.INTRO_STAGES) set.add(player.getUniqueId());
		}
		
		for (UUID uuid: set) {
			Player player = Bukkit.getPlayer(uuid);
			if (id >= start && id <= end) {
				ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
				SkullMeta skullmeta = (SkullMeta) head.getItemMeta();
				skullmeta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
				skullmeta.setDisplayName(player.getDisplayName());
				head.setItemMeta(skullmeta);
				inv.addItem(head);
			}
			id++;
		}
		
		if (page > 1) inv.setItem(27, UtilStacks.getUtilStacks().getLeftArrow());
		
		if (HEADS_PER_PAGE * page < Bukkit.getOnlinePlayers().size()) inv.setItem(35, UtilStacks.getUtilStacks().getRightArrow());
		
		p.openInventory(inv);
	}
	public static void openRemovePlayerList(Main plugin, Player p, int page) {
		plugin.getPlayerInfo(p).setFriendsPage(page);
		
		Inventory inv = Bukkit.createInventory(null, 36, Utils.colored("&fRemove friends"));
		
		int id = 0, start = HEADS_PER_PAGE * (page - 1), end = HEADS_PER_PAGE * page - 1;
		Coordinates coords = plugin.getPlayerInfo(p).getDoor();
		DoorInfo door = plugin.doorinfo.get(coords);
		
		List<String> allowedstrings = door.getAllowed();
		List<UUID> allowed = new ArrayList<UUID>();
		for (String s: allowedstrings) {
			allowed.add(UUID.fromString(s));
		}
		
		for (UUID uuid: allowed) {
			if (id >= start && id <= end) {
				ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
				SkullMeta skullmeta = (SkullMeta) head.getItemMeta();
//				skullmeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
				skullmeta.setDisplayName(plugin.getData().getName(uuid));
				head.setItemMeta(skullmeta);
				inv.addItem(head);
			}
			id++;
		}
		
		if (page > 1) inv.setItem(27, UtilStacks.getUtilStacks().getLeftArrow());

		if (HEADS_PER_PAGE * page < Bukkit.getOnlinePlayers().size()) inv.setItem(35, UtilStacks.getUtilStacks().getRightArrow());
		
		p.openInventory(inv);
		
		final List<UUID> finalallowed = allowed;
		final Player finalp = p;
		final Inventory finalinv = inv;
		final int finalstart = start, finalend = end;
		final Main finalplugin = plugin;
		Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
			@Override
			public void run() {
				int curid = 0;
				List<ItemStack> list = new ArrayList<ItemStack>(); 
				for (UUID uuid: finalallowed) {
					if (curid >= finalstart && curid <= finalend) {
						ItemStack skull = finalinv.getItem(curid - finalstart);
						SkullMeta skullmeta = (SkullMeta) skull.getItemMeta();
						skullmeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
						skull.setItemMeta(skullmeta);
						list.add(skull);
					}
					curid++;
				}
				final List<ItemStack> finallist = list;
				Bukkit.getScheduler().runTask(finalplugin, new Runnable() {
					@Override
					public void run() {
						Inventory newinv = finalinv;
						int cur = 0;
						for (ItemStack istack: finallist) {
							newinv.setItem(cur, istack);
							cur++;
						}
						Inventory topinv = finalp.getOpenInventory().getTopInventory();
						if (topinv != null && topinv.getSize() == 36 && topinv.getName().equals(ChatColor.WHITE + "Remove friends")) {
							finalp.openInventory(newinv);
						}
					}
				});
			}
		}, (long) 1);
	}
}
