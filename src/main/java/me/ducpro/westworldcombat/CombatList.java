package me.ducpro.westworldcombat;

import java.util.UUID;

import org.bukkit.entity.Player;

import java.util.HashSet;

public class CombatList {
	private HashSet<UUID> inCombat = new HashSet<UUID>();
	public void addPlayer(Player p) {
		inCombat.add(p.getUniqueId());
	}
	public void removePlayer(Player p) {
		inCombat.remove(p.getUniqueId());
	}
	public boolean isInCombat(Player p) {
		return inCombat.contains(p.getUniqueId());
	}
}
