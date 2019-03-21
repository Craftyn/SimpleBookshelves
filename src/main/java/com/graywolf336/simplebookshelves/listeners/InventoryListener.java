package com.graywolf336.simplebookshelves.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import com.graywolf336.simplebookshelves.BookshelvesMain;
import com.graywolf336.simplebookshelves.data.Bookshelf;
import com.graywolf336.simplebookshelves.enums.BookshelfType;
import com.graywolf336.simplebookshelves.events.PreBookshelfInventoryClickEvent;
import com.graywolf336.simplebookshelves.utils.ItemHelper;
import com.graywolf336.simplebookshelves.utils.MetaHelper;

import net.milkbowl.vault.economy.EconomyResponse;

public class InventoryListener implements Listener {
	private BookshelvesMain pl;
	
	public InventoryListener(BookshelvesMain plugin) {
		this.pl = plugin;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
	public void onInventoryClick(InventoryClickEvent event) {
		HumanEntity p = event.getWhoClicked();
		if (this.pl.getSettings().isDisabledWorld(p.getWorld().getName())) {
			return;
		}

		if (!StringUtil.startsWithIgnoreCase(event.getView().getTitle(), pl.getSettings().getTitle(true))) {
			return;
		}
		
		if (!p.hasMetadata("SIMPLE_BOOKSHELVES_OPENED_BOOKSHELF")) {
			return;
		}
		
		Location l = (Location) MetaHelper.getMetaValue(p, "SIMPLE_BOOKSHELVES_OPENED_BOOKSHELF", Location.class);
		Bookshelf b = this.pl.getBookshelfManager().getBookshelf(l);
		
		// As a general rule of thumb, anyone can interact with storage bookshelves
		// and we will let listeners to "PreBookshelfInventoryClickEvent" cancel
		// the event if a player isn't allowed to
		PreBookshelfInventoryClickEvent ev = new PreBookshelfInventoryClickEvent(event, p, b);
		this.pl.getServer().getPluginManager().callEvent(ev);

		if (ev.isCancelled()) {
			event.setCancelled(true);
			return;
		}

		ItemStack i = event.getCurrentItem();
		if (i == null || i.getType() == Material.AIR) {
			return;
		}
		
		// If the player is the owner, they can do what they want
		if (b.isOwner((Player) p) || p.hasPermission("simplebookshelves.modifyothers")) {
			if (!ItemHelper.isValidBook(i.getType(), b.getType())) {
				event.setCancelled(true);
			}
			
			return;
		}
		
		// Non-owners can't interact with storage bookshelvse
		if (b.getType() == BookshelfType.STORAGE) {
			event.setCancelled(true);
			return;
		}

		// If they clicked in our inventory, set it as cancelled and we'll overwrite it
		if (StringUtil.startsWithIgnoreCase(event.getClickedInventory().getTitle(), pl.getSettings().getTitle(true))) {
			event.setCancelled(true);
		}

		if (b.getType() == BookshelfType.ARCHIVE_PAID) {
			Player player = (Player) p;

			EconomyResponse res = this.pl.getEconomy().withdrawPlayer(player, 5);
			if (!res.transactionSuccess()) {
				p.sendMessage(ChatColor.DARK_RED + "Not enough funds for getting a copy.");
				return;
			}
			
			p.sendMessage(ChatColor.GREEN + "You were charged " + this.pl.getEconomy().format(5) + " for a copy");
			this.pl.getEconomy().depositPlayer(this.pl.getServer().getOfflinePlayer(b.getOwnerUUID()), 5);
			
			if (b.isOwnerOnline()) {
				b.getOwner().sendMessage(ChatColor.GREEN + player.getDisplayName() + " just bought a copy of items from a bookshelf.");
			}
		}

		p.getInventory().addItem(i);
	}
	
	@EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
	public void onInventoryExit(InventoryCloseEvent event) {
		HumanEntity p = event.getPlayer();
		if (this.pl.getSettings().isDisabledWorld(p.getWorld().getName())) {
			return;
		}

		if (!StringUtil.startsWithIgnoreCase(event.getView().getTitle(), pl.getSettings().getTitle(true))) {
			return;
		}
		
		if (!p.hasMetadata("SIMPLE_BOOKSHELVES_OPENED_BOOKSHELF")) {
			return;
		}
		
		p.removeMetadata("SIMPLE_BOOKSHELVES_OPENED_BOOKSHELF", this.pl);
	}
}
