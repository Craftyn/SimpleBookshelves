package com.graywolf336.simplebookshelves;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class Settings {
	private int ticksBetweenSaves;
	private int rows;
	private String title, colorizedTitle;
	private boolean onlyBooks;
	private List<String> disabledWorlds;

	public void load(FileConfiguration config) {
		this.ticksBetweenSaves = config.getInt("internal.ticksBetweenSaves", 36000);
		this.rows = config.getInt("inventory.rows", 1);
		this.title = config.getString("inventory.title", "&3&lBookshelf");
		this.colorizedTitle = ChatColor.translateAlternateColorCodes('&', this.title);

		this.disabledWorlds = config.getStringList("disabledWorlds");
	}
	
	public int getTicksBetweenSaves() {
		return this.ticksBetweenSaves;
	}
	
	public int getRows() {
		return this.rows;
	}
	
	public String getTitle(boolean colorized) {
		return colorized ? this.colorizedTitle : this.title;
	}
	
	public boolean onlyAllowedBooks() {
		return this.onlyBooks;
	}
	
	public boolean isDisabledWorld(String worldName) {
		return this.disabledWorlds.contains(worldName);
	}
}
