package com.graywolf336.simplebookshelves.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.graywolf336.simplebookshelves.enums.BookshelfType;
import com.graywolf336.simplebookshelves.utils.ItemHelper;

@SerializableAs(value = "SimpleBookself")
public class Bookshelf implements ConfigurationSerializable {
	private UUID owner;
	private String ownerName;
	private String title;
	private int rows;
	private BookshelfType type;
	private Location loc;
	
	private Inventory inv;
	
	public Bookshelf() {
		this.rows = 1;
		this.type = BookshelfType.STORAGE;
	}
	
	public Bookshelf(Player player, Location location, String title, int rows) {
		this.owner = player.getUniqueId();
		this.ownerName = player.getName();
		this.title = title;
		this.rows = rows;
		this.loc = location;
		this.type = BookshelfType.STORAGE;
	}
	
	public UUID getOwnerUUID() {
		return this.owner;
	}
	
	public String getOwnerName() {
		return this.ownerName;
	}
	
	public Player getOwner() {
		return Bukkit.getPlayer(this.owner);
	}
	
	public boolean isOwnerOnline() {
		return Bukkit.getPlayer(this.owner) != null;
	}
	
	// Convenience method 
	public boolean isOwner(Player player) {
		return this.owner.equals(player.getUniqueId());
	}
	
	public BookshelfType getType() {
		return this.type;
	}
	
	/** Tries to set the type, will fail if the inventory contains items not allowed in the proposed type. */
	public boolean setType(BookshelfType type) {
		for(ItemStack i : this.getInventory()) {
			if (i == null) continue;

			if (i.getType() != Material.AIR) {
				if (!ItemHelper.isValidBook(i.getType(), type)) {
					return false;
				}
			}
		}

		this.type = type;
		this.loadInventory();

		return true;
	}
	
	public Location getLocation() {
		return this.loc;
	}
	
	public Inventory getInventory() {
		if (this.inv == null) {
			this.loadInventory();
		}

		return this.inv;
	}
	
	private void loadInventory() {
		String suffix = "";
		if (this.type == BookshelfType.ARCHIVE_PAID) {
			suffix = " " + ChatColor.BLACK + "- " + ChatColor.GOLD + "" + ChatColor.BOLD + "PURCHASE";
		}

		Inventory tmpInv = Bukkit.createInventory(null, this.rows * 9, title + suffix);
		
		for(int i = 0; i < tmpInv.getSize(); i++) {
			tmpInv.setItem(i, new ItemStack(Material.AIR));
		}
		
		if (this.inv != null) {
			for(int i = 0; i < this.inv.getSize(); i++) {
				tmpInv.setItem(i, this.inv.getItem(i));
			}
		}
		
		this.inv = tmpInv;
	}

	public Map<String, Object> serialize() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("owner.uuid", this.owner.toString());
		map.put("owner.name", this.ownerName);
		map.put("info.title", this.title);
		map.put("info.rows", this.rows);
		map.put("info.type", this.type.toString());
		map.put("info.location", this.loc);
		
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		
		if (this.inv != null) {
			for(ItemStack i : this.inv.getContents()) {
				if (i == null) {
					items.add(new ItemStack(Material.AIR));
					continue;
				}

				items.add(i);
			}
		}
		
		map.put("inventory", items);
		
		return map;
	}

	public static Bookshelf deserialize(Map<String, Object> map) {
		Bookshelf b = new Bookshelf();

		b.owner = UUID.fromString((String) map.get("owner.uuid"));
		b.ownerName = (String) map.get("owner.name");
		b.title = (String) map.get("info.title");
		b.rows = (int) map.get("info.rows");
		b.type = BookshelfType.valueOf((String) map.get("info.type"));
		b.loc = (Location) map.get("info.location");
		
		b.loadInventory();

		if (map.get("inventory") instanceof List<?>) {
			List<?> items = (List<?>) map.get("inventory");

			for(int i = 0; i < items.size(); i++) {
				if (items.get(i) instanceof ItemStack) {
					b.inv.setItem(i, (ItemStack) items.get(i));
				} else {
					b.inv.setItem(i, new ItemStack(Material.AIR));
				}
			}
		}

		return b;
	}
}
