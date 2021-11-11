package me.ducpro.westworldheist.info;

import me.ducpro.westworldcore.main.Main.Coordinates;

public class ChestInfo {
	Coordinates coord;
	private String bankname;
	private int amount;
	public ChestInfo(String bankname, int amount, Coordinates coord) {
		this.bankname = bankname;
		this.amount = amount;
		this.coord = coord;
	}
	public String getBank() {
		return bankname;
	}
	public int getAmount() {
		return amount;
	}
}
