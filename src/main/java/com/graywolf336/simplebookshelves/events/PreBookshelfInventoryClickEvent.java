package com.graywolf336.simplebookshelves.events;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.graywolf336.simplebookshelves.data.Bookshelf;

public class PreBookshelfInventoryClickEvent extends Event implements Cancellable {
	private static HandlerList handlers = new HandlerList();
	private boolean cancel;

	private InventoryClickEvent event;
	private HumanEntity he;
	private Bookshelf bookshelf;
	
	public PreBookshelfInventoryClickEvent(InventoryClickEvent event, HumanEntity he, Bookshelf bookshelf) {
		this.cancel = false;
		this.he = he;
		this.bookshelf = bookshelf;
	}
	
	public InventoryClickEvent getInventoryClickEvent() {
		return this.event;
	}
	
	public HumanEntity getHumanEntity() {
		return this.he;
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
