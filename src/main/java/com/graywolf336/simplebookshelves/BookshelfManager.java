package com.graywolf336.simplebookshelves;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.graywolf336.simplebookshelves.data.Bookshelf;
import com.graywolf336.simplebookshelves.data.SimpleLocation;

public class BookshelfManager {
	private BookshelvesMain pl;
	private HashMap<String, Bookshelf> bookshelfs;
	
	public BookshelfManager(BookshelvesMain plugin) {
		this.pl = plugin;
		this.bookshelfs = new HashMap<String, Bookshelf>();
	}

	public boolean hasBookshelf(Block b) {
		return this.bookshelfs.containsKey(SimpleLocation.getBlockStringFromLocation(b.getLocation()));
	}
	
	public Bookshelf getBookshelf(Location l) {
		return this.bookshelfs.get(SimpleLocation.getBlockStringFromLocation(l));
	}
	
	public Bookshelf createNewBookshelf(Player p, Block b) {
		String l = SimpleLocation.getBlockStringFromLocation(b.getLocation());
		if (this.hasBookshelf(b)) {
			return this.bookshelfs.get(l);
		}

		String title = this.pl.getSettings().getTitle(true);
		int rows = this.pl.getSettings().getRows();
		
		this.bookshelfs.put(l, new Bookshelf(p, b.getLocation(), title, rows));
		
		return this.bookshelfs.get(l);
	}
	
	public void removeBookshelf(Location l) {
		this.bookshelfs.remove(SimpleLocation.getBlockStringFromLocation(l));
	}
	
	protected void saveBookshelves() throws IOException {
		File f = new File(this.pl.getDataFolder(), "bookshelves.yml");
		FileConfiguration sf = YamlConfiguration.loadConfiguration(f);
		
		ArrayList<Bookshelf> shelfs = new ArrayList<Bookshelf>();
		
		// Only save the bookshelves that contain things other than air
		for(Bookshelf b : this.bookshelfs.values()) {
			boolean hasOtherThanAir = false;
			for (ItemStack i : b.getInventory().getContents()) {
				if (i == null) continue;

				if (i.getType() != Material.AIR) {
					hasOtherThanAir = true;
				}
			}
			
			if (hasOtherThanAir) {
				shelfs.add(b);
			}
		}

        sf.set("bookshelves", shelfs);
        
        sf.save(f);
	}
	
	protected void loadBookshelves() {
		File f = new File(this.pl.getDataFolder(), "bookshelves.yml");
		FileConfiguration sf = YamlConfiguration.loadConfiguration(f);
		
		List<?> shelves = sf.getList("bookshelves");
		if (shelves == null) {
			return;
		}

		for(int i = 0; i < shelves.size(); i++) {
			if (shelves.get(i) instanceof Bookshelf) {
				Bookshelf b = (Bookshelf) shelves.get(i);
				
				this.bookshelfs.put(b.getLocation().toBlockString(), b);
			}
		}
		
		this.pl.getLogger().info("Loaded " + this.bookshelfs.size() + " bookshelves!");
	}
}
