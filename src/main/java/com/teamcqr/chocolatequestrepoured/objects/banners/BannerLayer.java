package com.teamcqr.chocolatequestrepoured.objects.banners;

import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.BannerPattern;

class BannerLayer {

	private final BannerPattern pattern;
	private final DyeColor color;

	public BannerLayer(BannerPattern pattern, DyeColor color) {

		this.pattern = pattern;
		this.color = color;
	}

	public BannerPattern getPattern() {

		return this.pattern;
	}

	public DyeColor getColor() {

		return this.color;
	}

}
