package me.ducpro.westworldcore.commands;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldcore.utils.SitPlayers;
import me.ducpro.westworldintro.commands.*;
import me.ducpro.westworldchat.commands.*;
import me.ducpro.westworldcharacter.commands.*;

public class PlayerCommands implements CommandExecutor {
	private Main plugin;
	public PlayerCommands(Main plugin) {
		this.plugin = plugin;
	}
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command can only be performed in - game.");
			return true;
		}
		Player p = (Player) sender;
		if (plugin.getPlayerInfo(p).getStage() <= Main.INTRO_STAGES) return true;
		
		if (label.equalsIgnoreCase("westcorereset")) {
			Reset.reset(plugin, p);
		}
		
		if (label.equalsIgnoreCase("ooc") || label.equalsIgnoreCase("o")) {
		 	if (args.length == 0) OOCCommand.process(plugin, p);
		 	else OOCCommand.sendOOCMessage(plugin, p, args);
		}
		
		if (label.equalsIgnoreCase("looc")) {
		 	if (args.length == 0) LOOCCommand.process(plugin, p);
		 	else LOOCCommand.sendLOOCMessage(plugin, p, args);
		}
		
		if (label.equalsIgnoreCase("local")) {
			if (args.length == 0) LocalCommand.process(plugin, p);
			else LocalCommand.sendLocalMessage(plugin, p, args);
		}
		
		if (label.equalsIgnoreCase("suicide")) {
			p.setHealth(0);
		}
		
		if (label.equalsIgnoreCase("sit")) {
			if (plugin.getPlayerInfo(p).getSitting()) return true;
			SitPlayers.sit(p, -1.6);
			plugin.getPlayerInfo(p).setSitting(true);
		}
		
		if (label.equalsIgnoreCase("toggleooc")) {
			boolean currentListen = plugin.getPlayerInfo(p).getListenOOC();
			if (currentListen) {
				currentListen = false;
				p.sendMessage(ChatColor.GREEN + "Successfully disabled all messages from the OOC channel.");
			} else {
				currentListen = true;
				p.sendMessage(ChatColor.GREEN + "Successfully enabled all messages from the OOC channel.");
			}
			plugin.getPlayerInfo(p).setListenOOC(currentListen);
		}
		
		if (label.equalsIgnoreCase("message") || label.equalsIgnoreCase("pm") || label.equalsIgnoreCase("msg") || label.equalsIgnoreCase("whisper") ||
				label.equalsIgnoreCase("tell")) {
			if (args.length == 0) {
				p.sendMessage(ChatColor.RED + "Please enter a player to whisper to.");
				return true;
			}
			String target = args[0];
			String message = "";
			for (int i = 1; i < args.length; i++) {
				message += args[i];
				if (i < args.length - 1) message += " ";
			}
			if (Bukkit.getPlayerExact(target) == null) {
				p.sendMessage(ChatColor.RED + "Target player isn't online.");
				return true;
			}
			Player targetp = Bukkit.getPlayerExact(target);
			String pname = plugin.getPlayerInfo(p).getName(), targetname = plugin.getPlayerInfo(targetp).getName();
			if (plugin.getPlayerInfo(targetp).getStage() <= Main.INTRO_STAGES) {
				p.sendMessage(ChatColor.RED + "Cannot send message to target.");
				return true;
			}
			p.sendMessage(ChatColor.GREEN + "To " + targetname + " ≫ " + message);
			targetp.sendMessage(ChatColor.GREEN + "From " + pname + " ≫ " + message);
			plugin.getPlayerInfo(targetp).setLastMessaged(p.getUniqueId());
		}
		
		if (label.equalsIgnoreCase("reply") || label.equalsIgnoreCase("r")) {
			String message = "";
			for (int i = 0; i < args.length; i++) {
				message += args[i];
				if (i < args.length - 1) message += " ";
			}
			Player lastPlayer = Bukkit.getPlayer(plugin.getPlayerInfo(p).getLastMessaged());
			if (lastPlayer == null) {
				p.sendMessage(ChatColor.RED + "Last messenger isn't online anymore.");
				return true;
			}
			p.performCommand("message " + lastPlayer.getName() + " " + message);
		}
		
		if (label.equalsIgnoreCase("coinflip")) {
			int headsOrTails = new Random().nextInt(2);
			String side = "heads";
			if (headsOrTails == 1) side = "tails";
			p.chat("*flips a coin and it lands on " + side);
//			String msg = "&e&o" + plugin.getPlayerInfo(p).getName() + " flips a coin and it lands on " + side;
//			msg = ChatColor.translateAlternateColorCodes('&', msg);
//			
//			p.sendMessage(msg);
//			for (Entity entity: p.getNearbyEntities(20, 20, 20)) {
//				if (entity instanceof Player) {
//					((Player) entity).sendMessage(msg);
//				}
//			}
		}
		
		if (label.equalsIgnoreCase("roll")) {
			int number = new Random().nextInt(6);
			number++;
			p.chat("*rolls a dice, it lands on " + number);
//			String msg = "&e&o" + plugin.getPlayerInfo(p).getName() + " rolls a dice, it lands on " + number;
//			msg = ChatColor.translateAlternateColorCodes('&', msg);
//
//			p.sendMessage(msg);
//			for (Entity entity: p.getNearbyEntities(20, 20, 20)) {
//				if (entity instanceof Player) {
//					((Player) entity).sendMessage(msg);
//				}
//			}
		}
		
		if (label.equalsIgnoreCase("desc")) {
			DescCommand.setDesc(plugin, p, args);
		}
		
		if (label.equalsIgnoreCase("spit")) {
			p.chat("*spits.");
			Location location = p.getLocation().toVector()
					.add(p.getLocation().getDirection().multiply(0.8D)).toLocation(p.getWorld())
					.add(0.0D, 1.0D, 0.0D);
			Entity spitmonster = p.getWorld().spawnEntity(location, EntityType.LLAMA_SPIT);
			spitmonster.setVelocity(p.getEyeLocation().getDirection().multiply(1));
		}
		return true;
	}
}
