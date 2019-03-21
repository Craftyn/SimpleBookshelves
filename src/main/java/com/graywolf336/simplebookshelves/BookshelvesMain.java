package com.graywolf336.simplebookshelves;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.graywolf336.simplebookshelves.data.Bookshelf;
import com.graywolf336.simplebookshelves.listeners.BlockListener;
import com.graywolf336.simplebookshelves.listeners.InventoryListener;
import com.graywolf336.simplebookshelves.listeners.PlayerListener;

import net.milkbowl.vault.economy.Economy;

public class BookshelvesMain extends JavaPlugin {
	private Settings settings;
	private BookshelfManager bsMgr;
	private int saveTask;
	private Economy economy;
	
	public void onLoad() {
		this.loadSerializableClasses();
	}

	public void onEnable() {
		this.loadConfig();
		
		if (!this.getServer().getPluginManager().isPluginEnabled("Vault")) {
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		if (!this.setupEconomy()) {
			this.getLogger().warning("Failed to setup the economy, it is required.");
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		this.getServer().getPluginManager().registerEvents(new BlockListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		this.getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
		
		this.bsMgr = new BookshelfManager(this);
		this.bsMgr.loadBookshelves();
		
		this.saveTask = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				try {
					bsMgr.saveBookshelves();
				} catch(Exception e) {
					e.printStackTrace();
					getLogger().warning("FAILED TO SAVE THE BOOKSHELVES.");
				}
			}
		}, this.settings.getTicksBetweenSaves() / 2, this.settings.getTicksBetweenSaves());
	}
	
	public void onDisable() {
		try {
			this.bsMgr.saveBookshelves();
		} catch(Exception e) {
			e.printStackTrace();
			this.getLogger().warning("FAILED TO SAVE THE BOOKSHELVES.");
		}
		
		if (saveTask != -1) {
			this.getServer().getScheduler().cancelTask(this.saveTask);
		}
	}
	
	public Settings getSettings() {
		return this.settings;
	}
	
	public BookshelfManager getBookshelfManager() {
		return this.bsMgr;
	}
	
	private void loadSerializableClasses() {
		ConfigurationSerialization.registerClass(Bookshelf.class, "SimpleBookself");
	}
	
    private void loadConfig() {
        //Only create the default config if it doesn't exist
        saveDefaultConfig();

        //Append new key-value pairs to the config
        getConfig().options().copyDefaults(true);

        // Set the header and save
        getConfig().options().header(getHeader());
        saveConfig();
        
        this.settings = new Settings();
        this.settings.load(this.getConfig());
    }

    private String getHeader() {
        String sep = System.getProperty("line.separator");

        return "###################" + sep
                + this.getDescription().getName() + " v" + this.getDescription().getVersion() + " config file" + sep
                + "Note: You -must- use spaces instead of tabs!" + sep +
                "###################";
    }
    
    public Economy getEconomy() {
        return this.economy;
    }

    private boolean setupEconomy() {
        if (economy != null) return true;

        RegisteredServiceProvider<Economy> economyProvider = this.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
}
