package com.graywolf336.simplebookshelves.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.graywolf336.simplebookshelves.BookshelvesMain;

public class BlockListener implements Listener {
	private BookshelvesMain pl;
	
	public BlockListener(BookshelvesMain plugin) {
		this.pl = plugin;
	}

	@EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		Block b = event.getBlock();
		if (b == null) {
			return;
		}

		if (this.pl.getSettings().isDisabledWorld(b.getWorld().getName())) {
			return;
		}
		
		if (b.getType() != Material.BOOKSHELF) {
			return;
		}

		if (!this.pl.getBookshelfManager().hasBookshelf(b)) {
			return;
		}
		
		//TODO: this
	}
}
