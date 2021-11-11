package me.ducpro.westworldhorse.utils;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_13_R2.NBTTagCompound;

public class UtilStacks {
	private static UtilStacks utilstacks;
	private ItemStack leftarrow, rightarrow;
	private ItemStack stonepick;
	public static void init() {
		utilstacks = new UtilStacks();
		ItemStack cur;
		
		cur = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta skullmeta = (SkullMeta) cur.getItemMeta();
		skullmeta.setDisplayName(Utils.colored("&cGo back"));
		cur.setItemMeta(skullmeta);
		String leftarrowhash = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==";
		new SkullProfile(leftarrowhash).applyTextures(cur);
		utilstacks.setLeftArrow(cur);
		
		cur = new ItemStack(Material.PLAYER_HEAD, 1);
		skullmeta = (SkullMeta) cur.getItemMeta();
		skullmeta.setDisplayName(Utils.colored("&aGo forward"));
		cur.setItemMeta(skullmeta);
		String rightarrowhash = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19";
		new SkullProfile(rightarrowhash).applyTextures(cur);
		utilstacks.setRightArrow(cur);
		
		cur = new ItemStack(Material.STONE_PICKAXE, 1);
		net.minecraft.server.v1_13_R2.ItemStack nmsstack = CraftItemStack.asNMSCopy(cur);
		NBTTagCompound nmscompound = nmsstack.hasTag() ? nmsstack.getTag() : new NBTTagCompound();
		nmscompound.setInt("Damage", 1);
		nmscompound.setInt("Unbreakable", 1);
		nmsstack.setTag(nmscompound);
		cur = CraftItemStack.asBukkitCopy(nmsstack);
		ItemMeta meta = cur.getItemMeta();
		meta.setDisplayName(ChatColor.WHITE.toString());
		cur.setItemMeta(meta);
		utilstacks.setStonePick(cur);
	}
	public static UtilStacks getUtilStacks() {
		return utilstacks;
	}
	public ItemStack getLeftArrow() {
		return leftarrow;
	}
	public ItemStack getRightArrow() {
		return rightarrow;
	}
	public ItemStack getStonePick() {
		return stonepick;
	}
	public void setLeftArrow(ItemStack leftarrow) {
		this.leftarrow = leftarrow;
	}
	public void setRightArrow(ItemStack rightarrow) {
		this.rightarrow = rightarrow;
	}
	public void setStonePick(ItemStack stonepick) {
		this.stonepick = stonepick;
	}
}
