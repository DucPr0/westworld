package me.ducpro.westworldheist.utils;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_13_R2.NBTTagCompound;

public class VaultDoors {
	private ItemStack normaldoor, brokendoor;
	private ItemStack placeexplosives;
	public VaultDoors() {
		normaldoor = new ItemStack(Material.DIAMOND_HOE, 1);
		net.minecraft.server.v1_13_R2.ItemStack nmsstack = CraftItemStack.asNMSCopy(normaldoor);
		NBTTagCompound nmscompound = nmsstack.hasTag() ? nmsstack.getTag() : new NBTTagCompound();
		nmscompound.setInt("Damage", 68);
		nmscompound.setInt("Unbreakable", 1);
		nmsstack.setTag(nmscompound);
		normaldoor = CraftItemStack.asBukkitCopy(nmsstack);
		
		brokendoor = new ItemStack(Material.DIAMOND_HOE, 1);
		nmsstack = CraftItemStack.asNMSCopy(brokendoor);
		nmscompound = nmsstack.hasTag() ? nmsstack.getTag() : new NBTTagCompound();
		nmscompound.setInt("Damage", 69);
		nmscompound.setInt("Unbreakable", 1);
		nmsstack.setTag(nmscompound);
		brokendoor = CraftItemStack.asBukkitCopy(nmsstack);
		
		placeexplosives = new ItemStack(Material.STONE_PICKAXE, 1);
		nmsstack = CraftItemStack.asNMSCopy(placeexplosives);
		nmscompound = nmsstack.hasTag() ? nmsstack.getTag() : new NBTTagCompound();
		nmscompound.setInt("Damage", 10);
		nmscompound.setInt("Unbreakable", 1);
		nmsstack.setTag(nmscompound);
		placeexplosives = CraftItemStack.asBukkitCopy(nmsstack);
		ItemMeta placemeta = placeexplosives.getItemMeta();
		placemeta.setDisplayName(ChatColor.RED + "Place Explosives");
		placeexplosives.setItemMeta(placemeta);
	}
	public ItemStack getNormalDoor() {
		return normaldoor;
	}
	public ItemStack getBrokenDoor() {
		return brokendoor;
	}
	public ItemStack getPlaceExplosives() {
		return placeexplosives;
	}
}
