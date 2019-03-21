package com.graywolf336.simplebookshelves.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.graywolf336.simplebookshelves.data.Bookshelf;

public class PreBookshelfOpenEvent extends Event implements Cancellable {
	private static HandlerList handlers = new HandlerList();
	private boolean cancel;

	private Player player;
	private Block block;
	private Bookshelf bookshelf;
	
	public PreBookshelfOpenEvent(Player player, Block block, Bookshelf bookshelf) {
		this.cancel = false;
		this.player = player;
		this.block = block;
		this.bookshelf = bookshelf;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public Block getBlock() {
		return this.block;
	}
	
	public Bookshelf getBookshelf() {
		return this.bookshelf;
	}

	public boolean isCancelled() {
		return this.cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

	public HandlerList getHandlers() {
		return handlers;
	}
}
