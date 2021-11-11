package me.ducpro.westworldranks.utils;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;

import me.ducpro.westworldcore.main.Main;

public class GetUUID {
	//Made to get UUID of offline players, for mute or ban requests (displaying character name).
	public static UUID getUUID(Main plugin, String name) {
		if (name.contains(" ")) {
			return getUUIDFromCharacterName(plugin, name);
		} else {
			return getUUIDFromNormalName(name);
		}
	}
	public static UUID getUUIDFromNormalName(String normalName) {
		return Bukkit.getOfflinePlayer(normalName).getUniqueId();
	}
	public static UUID getUUIDFromCharacterName(Main plugin, String characterName) {
		File dataFolder = new File(plugin.getDataFolder(), "playerdata");
		for (File dataFile: dataFolder.listFiles()) {
			String uuid = dataFile.getName();
			uuid = uuid.substring(0, uuid.length() - 4);
			if (plugin.getData().getName(UUID.fromString(uuid)).equals(characterName)) {
				return UUID.fromString(uuid);
			}
		}
		return null;
	}
}
