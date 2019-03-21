package com.graywolf336.simplebookshelves.utils;

import org.bukkit.Material;

import com.graywolf336.simplebookshelves.enums.BookshelfType;

public class ItemHelper {
	public static boolean isValidBook(Material itemType, BookshelfType shelfType) {		
		if (itemType == Material.WRITABLE_BOOK) {
			return true;
		}
		
		if (itemType == Material.WRITTEN_BOOK) {
			return true;
		}
		
		if (shelfType == BookshelfType.STORAGE) {
			if (itemType == Material.ENCHANTED_BOOK) {
				return true;
			}

			if (itemType == Material.PAPER) {
				return true;
			}
			
			if (itemType == Material.MAP) {
				return true;
			}
			
			if (itemType == Material.FILLED_MAP) {
				return true;
			}
		}

		return false;
	}
}
