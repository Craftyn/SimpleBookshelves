package com.graywolf336.simplebookshelves.enums;

import org.bukkit.Material;

public enum BookshelfType {
	/** Allows getting a copy of a book for free. */
	ARCHIVE(Material.LILY_PAD),
	/** Allows getting a copy of a book, but at a price. */
	ARCHIVE_PAID(Material.GOLD_NUGGET),
	/** Generic storage for books. */
	STORAGE(Material.GHAST_TEAR);
	
	private Material converterType;
	private BookshelfType(Material type) {
		this.converterType = type;
	}

	public Material getConversionType() {
		return this.converterType;
	}
}
