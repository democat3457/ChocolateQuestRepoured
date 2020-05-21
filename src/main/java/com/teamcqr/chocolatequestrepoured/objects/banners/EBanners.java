package com.teamcqr.chocolatequestrepoured.objects.banners;

import net.minecraft.item.DyeColor;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;

public enum EBanners {

	// TODO: Move banner name to lang files

	// DONE: Add the cq-blank design to all!!
	PIRATE_BANNER(DyeColor.BLACK, new BannerPattern[] { EBannerPatternsCQ.CQ_BLANK.getPattern(), BannerPattern.SKULL }, new DyeColor[] { DyeColor.WHITE, DyeColor.WHITE }, "Flag Of Piracy"),
	WALKER_BANNER(DyeColor.LIGHT_GRAY,
			new BannerPattern[] { EBannerPatternsCQ.CQ_BLANK.getPattern(), BannerPattern.BRICKS, EBannerPatternsCQ.WITHER_SKULL_EYES.getPattern(), EBannerPatternsCQ.WITHER_SKULL.getPattern(), EBannerPatternsCQ.WITHER_SKULL.getPattern() },
			new DyeColor[] { DyeColor.WHITE, DyeColor.GRAY, DyeColor.CYAN, DyeColor.BLACK, DyeColor.BLACK }, "Nightwatch"),
	PIGMAN_BANNER(DyeColor.RED, new BannerPattern[] { EBannerPatternsCQ.CQ_BLANK.getPattern(), EBannerPatternsCQ.FIRE.getPattern() }, new DyeColor[] { DyeColor.WHITE, DyeColor.YELLOW }, "Pigman"),
	ENDERMEN_BANNER(DyeColor.MAGENTA, new BannerPattern[] { EBannerPatternsCQ.CQ_BLANK.getPattern(), BannerPattern.TRIANGLE_BOTTOM, BannerPattern.TRIANGLE_TOP },
			new DyeColor[] { DyeColor.WHITE, DyeColor.BLACK, DyeColor.BLACK }, "Enderman"),
	ENDERMEN_BANNER2(DyeColor.MAGENTA, new BannerPattern[] { EBannerPatternsCQ.CQ_BLANK.getPattern(), BannerPattern.SQUARE_TOP_RIGHT, BannerPattern.SQUARE_BOTTOM_LEFT },
			new DyeColor[] { DyeColor.WHITE, DyeColor.BLACK, DyeColor.BLACK }, "Enderman"),
	NPC_BANNER(DyeColor.RED, new BannerPattern[] { EBannerPatternsCQ.CQ_BLANK.getPattern(), BannerPattern.FLOWER, BannerPattern.STRIPE_CENTER, EBannerPatternsCQ.EMERALD.getPattern() },
			new DyeColor[] { DyeColor.WHITE, DyeColor.YELLOW, DyeColor.RED, DyeColor.LIME }, "Merchants"),
	SKELETON_BANNER(DyeColor.BLACK, new BannerPattern[] { EBannerPatternsCQ.CQ_BLANK.getPattern(), BannerPattern.CROSS, EBannerPatternsCQ.BONES.getPattern() },
			new DyeColor[] { DyeColor.WHITE, DyeColor.RED, DyeColor.WHITE }, "Undead Bones"),
	// GREMLIN_BANNER(DyeColor.LIGHT_GRAY, new BannerPattern[] { }, new DyeColor[] { }),
	// WINGS_OF_FREEDOM(DyeColor.WHITE, new BannerPattern[] {BannerPattern.DIAGONAL_LEFT_MIRROR, BannerPattern.BRICKS, BannerPattern.TRIANGLE_TOP, BannerPattern.GRADIENT, BannerPattern.BORDER}, new DyeColor[] {DyeColor.BLUE,
	// DyeColor.LIGHT_GRAY, DyeColor.LIGHT_GRAY, DyeColor.LIGHT_GRAY, DyeColor.LIGHT_GRAY}),
	// GERMANY(DyeColor.RED, new BannerPattern[] {BannerPattern.STRIPE_MIDDLE, BannerPattern.STRIPE_CENTER, BannerPattern.STRAIGHT_CROSS, BannerPattern.CIRCLE_MIDDLE, BannerPattern.STRAIGHT_CROSS}, new DyeColor[]
	// {DyeColor.WHITE, DyeColor.WHITE, DyeColor.BLACK, DyeColor.WHITE, DyeColor.BLACK}, "Germany"),
	ILLAGER_BANNER(DyeColor.WHITE,
			new BannerPattern[] {
					EBannerPatternsCQ.CQ_BLANK.getPattern(),
					BannerPattern.RHOMBUS_MIDDLE,
					BannerPattern.STRIPE_BOTTOM,
					BannerPattern.STRIPE_CENTER,
					BannerPattern.BORDER,
					BannerPattern.STRIPE_MIDDLE,
					BannerPattern.HALF_HORIZONTAL },
			new DyeColor[] { DyeColor.WHITE, DyeColor.RED, DyeColor.LIGHT_GRAY, DyeColor.GRAY, DyeColor.LIGHT_GRAY, DyeColor.BLACK, DyeColor.LIGHT_GRAY }, "Pillagers"),

	WALKER_ORDO(DyeColor.WHITE,
			new BannerPattern[] {
					EBannerPatternsCQ.CQ_BLANK.getPattern(),
					EBannerPatternsCQ.WALKER_BORDER.getPattern(),
					EBannerPatternsCQ.WALKER_BORDER.getPattern(),
					EBannerPatternsCQ.WALKER_BACKGROUND.getPattern(),
					EBannerPatternsCQ.WALKER_INNER_BORDER.getPattern(),
					EBannerPatternsCQ.WALKER_SKULL.getPattern() },
			new DyeColor[] { DyeColor.WHITE, DyeColor.PURPLE, DyeColor.PURPLE, DyeColor.BLACK, DyeColor.GRAY, DyeColor.BLACK, }, "Abyss Walker Flag"),
	
	GREMLIN_BANNER(DyeColor.GRAY,
			new BannerPattern[] {
					EBannerPatternsCQ.CQ_BLANK.getPattern(),
					BannerPattern.STRIPE_SMALL,
					BannerPattern.STRIPE_CENTER,
					BannerPattern.DIAGONAL_RIGHT_MIRROR,
					BannerPattern.FLOWER,
					BannerPattern.GRADIENT_UP,
					BannerPattern.GRADIENT
			},
			new DyeColor[] {
					DyeColor.WHITE,
					DyeColor.RED,
					DyeColor.GRAY,
					DyeColor.GRAY,
					DyeColor.WHITE,
					DyeColor.RED,
					DyeColor.BLACK
			}, "Gremlins"),
	;

	private BannerPattern[] patternList;
	private DyeColor[] colorList;
	private DyeColor mainColor;
	private String name;

	private EBanners(DyeColor mainColor, BannerPattern[] patterns, DyeColor[] colors, String name) {
		this.mainColor = mainColor;
		this.colorList = colors;
		this.patternList = patterns;
		this.name = name;
	}

	public ItemStack getBanner() {
		// System.out.println("Creating banner item for banner: " + this.toString());
		final ListNBT patternList = new ListNBT();

		for (int i = 0; i < this.patternList.length; i++) {
			BannerPattern currPatt = this.patternList[i];
			DyeColor currCol = this.colorList[i];

			final CompoundNBT tag = new CompoundNBT();
			tag.putString("Pattern", currPatt.getHashname());
			tag.putInt("Color", currCol.getColorValue());

			patternList.add(tag);
		}

		ItemStack item = BannerItem.makeBanner(this.mainColor, patternList);
		item = item.setDisplayName(this.name);
		return item;
	}

}
