package me.ducpro.westworldtrain.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_13_R2.NBTTagCompound;

public class UtilStacks {
	ItemStack stonepick;
	private static UtilStacks utilstacks;
	public UtilStacks() {
		ItemStack cur = new ItemStack(Material.STONE_PICKAXE, 1);
		net.minecraft.server.v1_13_R2.ItemStack nmsstack = CraftItemStack.asNMSCopy(cur);
		NBTTagCompound nmscompound = nmsstack.hasTag() ? nmsstack.getTag() : new NBTTagCompound();
		nmscompound.setInt("Damage", 1);
		nmscompound.setInt("Unbreakable", 1);
		nmsstack.setTag(nmscompound);
		cur = CraftItemStack.asBukkitCopy(nmsstack);
		ItemMeta meta = cur.getItemMeta();
		meta.setDisplayName(ChatColor.WHITE.toString());
		cur.setItemMeta(meta);
		setStonePick(cur);
	}
	public static void init() {
		utilstacks = new UtilStacks();
	}
	public static UtilStacks getUtilStacks() {
		return utilstacks;
	}
	public void setStonePick(ItemStack istack) {
		stonepick = istack;
	}
	public ItemStack getStonePick() {
		return stonepick;
	}
}
