package com.graywolf336.simplebookshelves.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.graywolf336.simplebookshelves.BookshelvesMain;
import com.graywolf336.simplebookshelves.data.Bookshelf;
import com.graywolf336.simplebookshelves.enums.BookshelfType;
import com.graywolf336.simplebookshelves.events.PreBookshelfCreateEvent;
import com.graywolf336.simplebookshelves.events.PreBookshelfOpenEvent;

public class PlayerListener implements Listener {
	private BookshelvesMain pl;
	
	public PlayerListener(BookshelvesMain plugin) {
		this.pl = plugin;
	}
	
	@EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getHand() != EquipmentSlot.HEAD) {
			return;
		}
		
		Block b = event.getClickedBlock();
		
		if (this.pl.getSettings().isDisabledWorld(b.getWorld().getName())) {
			return;
		}
		
		if (b == null || b.getType() != Material.BOOKSHELF) {
			return;
		}

		Player p = event.getPlayer();
		if (p.isSneaking()) {
			return;
		}

		if (!p.hasPermission("simplebookshelves.open")) {
			p.sendMessage(ChatColor.DARK_RED + "You don't have permission to open that bookshelf.");
			return;
		}

		Bookshelf bs = null;
		boolean freshlyCreated = false;
		if (this.pl.getBookshelfManager().hasBookshelf(b)) {
			bs = this.pl.getBookshelfManager().getBookshelf(b.getLocation());
		} else {
			if (!p.hasPermission("simplebookshelves.create")) {
				return;
			}

			PreBookshelfCreateEvent ev = new PreBookshelfCreateEvent(p, b);
			this.pl.getServer().getPluginManager().callEvent(ev);

			if (ev.isCancelled()) {
				return;
			}
			
			bs = this.pl.getBookshelfManager().createNewBookshelf(p, b);
			freshlyCreated = true;
		}

		PreBookshelfOpenEvent ev = new PreBookshelfOpenEvent(p, b, bs);
		this.pl.getServer().getPluginManager().callEvent(ev);

		if (ev.isCancelled()) {
			return;
		}
		
		// If the player is the owner
		// If their in main hand item is one we care about
		// And the bookshelf was not freshly created
		// try to change the type
		ItemStack i = p.getInventory().getItemInMainHand();
		if (bs.isOwner(p) && isChangingType(i) && !freshlyCreated) {
			BookshelfType toType = BookshelfType.STORAGE;
			
			if (i.getType() == BookshelfType.ARCHIVE.getConversionType()) {
				toType = BookshelfType.ARCHIVE;
			} else if (i.getType() == BookshelfType.ARCHIVE_PAID.getConversionType()) {
				toType = BookshelfType.ARCHIVE_PAID;
			} else if (i.getType() == BookshelfType.STORAGE.getConversionType()) {
				toType = BookshelfType.STORAGE;
			}
			
			if (toType == bs.getType()) {
				p.sendMessage(ChatColor.RED + "The bookshelf is already that type.");
				return;
			}
			
			boolean result = bs.setType(toType);
			if (result) {
				p.sendMessage(ChatColor.GREEN + "Successfully changed the Bookshelf type to " + toType.toString() + "!");
				i.setAmount(0);
				i.setType(Material.AIR);
			} else {
				p.sendMessage(ChatColor.RED + "Failed to change the type, ensure the items in the bookshelf are valid.");
				return;
			}

			event.setCancelled(true);
			return;
		}
		
		if (!bs.isOwner(p)) {
			p.sendMessage(ChatColor.GREEN + "Opening a Bookshelf owned by " + bs.getOwnerName() + "!");
		}

		p.openInventory(bs.getInventory());
		p.setMetadata("SIMPLE_BOOKSHELVES_OPENED_BOOKSHELF", new FixedMetadataValue(this.pl, b.getLocation()));
	}
	
	private boolean isChangingType(ItemStack i) {
		if (i.getType() == BookshelfType.ARCHIVE.getConversionType()) {
			return true;
		} else if (i.getType() == BookshelfType.ARCHIVE_PAID.getConversionType()) {
			return true;
		} else if (i.getType() == BookshelfType.STORAGE.getConversionType()) {
			return true;
		}
		
		return false;
	}
}
