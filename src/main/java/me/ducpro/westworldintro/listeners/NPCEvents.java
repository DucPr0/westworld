package me.ducpro.westworldintro.listeners;

import org.bukkit.entity.Player;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldintro.FinishIntro;
import me.ducpro.westworldintro.npc.HatNPC;
import me.ducpro.westworldintro.npc.InfoNPC;
import me.ducpro.westworldintro.npc.ItemNPC;
import me.ducpro.westworldintro.npc.NameAgeNPC;

public class NPCEvents implements Listener {
	private Main plugin;
	public NPCEvents(Main plugin) {
		this.plugin = plugin;
	}
	//NameAgeNPC
	@EventHandler
	public void onNameAgeNPCInteract(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof Player) {
			Player p = (Player) e.getRightClicked();
			if (ChatColor.stripColor(p.getName()).equals("WESTWORLD HO")) {
				if (plugin.getPlayerInfo(e.getPlayer()).getStage() == 1) {
					plugin.getPlayerInfo(e.getPlayer()).setStage(-1);
					NameAgeNPC npc = new NameAgeNPC(plugin);
					npc.sendMessage(e.getPlayer(), 0);
				}
			}
		}
	}
	private boolean validCharacter(char c) {
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}
	private String convertToName(String name) {
//		name = name.toLowerCase();
		char upper = Character.toUpperCase(name.charAt(0));
		name = upper + name.substring(1);
		return name;
	}
	@EventHandler
	public void onPlayerAnswerQuestion(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (!plugin.containsPlayer(p.getUniqueId())) return;
		
		int asking = -1;
		if (plugin.getPlayerInfo(p).askingname) asking = 1;
		else if (plugin.getPlayerInfo(p).askingage) asking = 2;
		if (asking == -1) return;
		
		if (asking == 1) {
			String s = e.getMessage();
			String[] parts = s.split(" ");
			if (parts.length != 2) {
				p.sendMessage(ChatColor.RED + "Correct name format: <firstname> <lastname>.");
				return;
			}
			
			String firstname = parts[0], lastname = parts[1];
			for (int i = 0; i < firstname.length(); i++) if (!validCharacter(firstname.charAt(i))) {
				p.sendMessage(ChatColor.RED + "Your name contains forbidden characters.");
				return;
			}
			for (int i = 0; i < lastname.length(); i++) if (!validCharacter(lastname.charAt(i))) {
				p.sendMessage(ChatColor.RED + "Your name contains forbidden characters.");
				return;
			}
			firstname = convertToName(firstname);
			lastname = convertToName(lastname);
			
			s = firstname + " " + lastname;
			if (s.length() > 16) {
				p.sendMessage(ChatColor.RED + "Your name is too long.");
				return;
			}
			plugin.getPlayerInfo(p).setName(s);
			
			NameAgeNPC npc = new NameAgeNPC(plugin);
			plugin.getPlayerInfo(p).askingname = false;
			npc.sendMessage(e.getPlayer(), 2);
		}
		if (asking == 2) {
			String s = e.getMessage();
			
			int age;
			try {
				age = Integer.parseInt(s);
			} catch (NumberFormatException exception) {
				p.sendMessage(ChatColor.RED + "Please enter a valid age.");
				return;
			}
			if (age < 21 || age > 40) {
				p.sendMessage(ChatColor.RED + "Age must be in the range of 21 - 40.");
				return;
			}
			
			plugin.getPlayerInfo(p).setAge(age);
			
			NameAgeNPC npc = new NameAgeNPC(plugin);
			npc.sendMessage(e.getPlayer(), 4);
		}
	}
	//ItemNPC
	@EventHandler
	public void onItemNPCInteract(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof Player) {
			Player p = (Player) e.getRightClicked();
			if (ChatColor.stripColor(p.getName()).equals("QUIPMENT MANAG")) {
				if (plugin.getPlayerInfo(e.getPlayer()).getStage() == 2) {
					plugin.getPlayerInfo(e.getPlayer()).setStage(-1);
					
					ItemNPC npc = new ItemNPC(plugin);
					npc.sendMessage(e.getPlayer());
				}
			}
		}
	}
	//InfoNPC
	@EventHandler
	public void onInfoNPCInteract(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof Player) {
			Player p = (Player) e.getRightClicked();
			if (ChatColor.stripColor(p.getName()).equals("INSTRUCTOR")) {
				if (plugin.getPlayerInfo(e.getPlayer()).getStage() == 3) {
					plugin.getPlayerInfo(e.getPlayer()).setStage(-1);
					
					InfoNPC npc = new InfoNPC(plugin);
					npc.sendMessage(e.getPlayer());
				}
			}
		}
	}
	@EventHandler
	public void onPlayerNod(AsyncPlayerChatEvent e) {
		if (e.getMessage().equals("*nods their head")) {
			if (plugin.getPlayerInfo(e.getPlayer()).requestingnod) {
				plugin.getPlayerInfo(e.getPlayer()).requestingnod = false;
				
				String name = plugin.getPlayerInfo(e.getPlayer()).getName();
				String action = e.getMessage();
				action = action.replace('*', ' ');
				e.getPlayer().sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + name + action);
				
				InfoNPC npc = new InfoNPC(plugin);
				npc.sendLastMessage(e.getPlayer());
			}
		}
	}
	//HatNPC
	@EventHandler
	public void onHatNPCInteract(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof Player) {
			Player p = (Player) e.getRightClicked();
			if (ChatColor.stripColor(p.getName()).equals("PARK GUIDE")) {
				if (e.getHand() == EquipmentSlot.OFF_HAND) return;
				if (plugin.getPlayerInfo(e.getPlayer()).getStage() == 4) {
					HatNPC npc = new HatNPC(plugin);
					if (!plugin.getPlayerInfo(e.getPlayer()).getHatSentFirst()) {
						npc.sendFirstMessage(e.getPlayer());
						
						plugin.getPlayerInfo(e.getPlayer()).setHatSentFirst(true);
						plugin.getPlayerInfo(e.getPlayer()).setHatAllowed(true);
					} else {
						plugin.getPlayerInfo(e.getPlayer()).setHatAllowed(false);
						plugin.getPlayerInfo(e.getPlayer()).setStage(-1);
						
						String hatid = plugin.getPlayerInfo(e.getPlayer()).getHatID();
						if (hatid != null) {
							String command = "mi armor %hatid% %player%";
							command = command.replaceAll("%hatid%", hatid).replaceAll("%player%", e.getPlayer().getName());
							Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), command);
						}
						
						npc.sendMessage(e.getPlayer());
					}
				}
			}
		}
	}
	//Train door
	@EventHandler(priority = EventPriority.NORMAL)
	public void onDoorClick(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof ItemFrame) {
			ItemFrame iframe = (ItemFrame) e.getRightClicked();
			ItemStack istack = iframe.getItem();
			if (istack.getType() == Material.DIAMOND_HOE && istack.hasItemMeta() && istack.getItemMeta().hasDisplayName() &&
					ChatColor.stripColor(istack.getItemMeta().getDisplayName()).equals("Right-click")) {
				if (!plugin.getPlayerInfo(e.getPlayer()).getRotate()) {
					e.setCancelled(true);
					
					Player p = e.getPlayer();
					if (plugin.getPlayerInfo(p).getStage() == 5) {
						plugin.getPlayerInfo(p).setStage(6);
						plugin.getData().setStage(p.getUniqueId(), 6);
						
						FinishIntro fin = new FinishIntro(plugin);
						fin.finishIntro(p);
					} else {
						if (plugin.getPlayerInfo(p).getStage() < Main.INTRO_STAGES) {
							p.sendMessage(ChatColor.RED + "You must complete the character creation process! Go back and finish talking with the guides!");
						}
					}
				}
			}
		}
	}
	@EventHandler
	public void onHatClick(PlayerInteractEntityEvent e) {
		if (e.getRightClicked() instanceof ItemFrame) {
			ItemFrame iframe = (ItemFrame) e.getRightClicked();
			ItemStack istack = iframe.getItem();
			if (istack.getType() == Material.DIAMOND_AXE && istack.hasItemMeta() && istack.getItemMeta().hasDisplayName()) {
				if (!plugin.getPlayerInfo(e.getPlayer()).getRotate()) {
					e.setCancelled(true);
					
					String hatid = null;
					String hatname = ChatColor.stripColor(istack.getItemMeta().getDisplayName());
					if (hatname.equals("Brown Homberg Hat")) hatid = "hat1";
					else if (hatname.equals("Black Top Hat")) hatid = "hat2";
					else if (hatname.equals("Green Gambler Hat")) hatid = "hat3";
					else if (hatname.equals("Straw Flat Crown")) hatid = "hat4";
					else if (hatname.equals("Red Flat Crown")) hatid = "hat5";
					else if (hatname.equals("White Gambler Hat")) hatid = "hat6";
					
					if (plugin.getPlayerInfo(e.getPlayer()).isHatAllowed()) {
						if (hatid != null) {
							plugin.getPlayerInfo(e.getPlayer()).setHatID(hatid);
							e.getPlayer().sendMessage(ChatColor.GREEN + "You have selected " + hatname);
						}
					}
				}
			}
		}
	}
}
