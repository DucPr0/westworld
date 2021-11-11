package me.ducpro.westworldcore.utils;

import org.bukkit.Rotation;

public class GetYawFromRotation {
	public static int getYaw(Rotation rot) {
		int ans = 0;
		if (rot == Rotation.NONE) ans = 0;
		if (rot == Rotation.CLOCKWISE_45) ans = 45;
		if (rot == Rotation.CLOCKWISE) ans = 90;
		if (rot == Rotation.CLOCKWISE_135) ans = 135;
		if (rot == Rotation.FLIPPED) ans = 180;
		if (rot == Rotation.FLIPPED_45) ans = 225;
		if (rot == Rotation.COUNTER_CLOCKWISE) ans = 270;
		if (rot == Rotation.COUNTER_CLOCKWISE_45) ans = 315;
		return ans;
	}
}
