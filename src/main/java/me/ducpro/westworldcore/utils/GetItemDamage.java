package me.ducpro.westworldcore.utils;

import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_13_R2.NBTTagCompound;

public class GetItemDamage {
	public static int getDamage(ItemStack istack) {
		net.minecraft.server.v1_13_R2.ItemStack nmsstack = CraftItemStack.asNMSCopy(istack);
		if (!nmsstack.hasTag()) return 0;
		NBTTagCompound nmscompound = nmsstack.getTag();
		return nmscompound.getInt("Damage");
	}
}
