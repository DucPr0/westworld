package me.ducpro.westworldheist.load;

import java.util.Set;
import java.util.HashSet;

import me.ducpro.westworldcore.main.Main;
import me.ducpro.westworldheist.info.HeistInfo;

public class RemoveAllHeists {
	public static void removeAllHeists(Main plugin) {
		Set<String> keys = new HashSet<String>(plugin.heistinfo.keySet());
		for (String s: keys) {
			HeistInfo hinfo = plugin.heistinfo.get(s);
			hinfo.heistEnd();
		}
	}
}
