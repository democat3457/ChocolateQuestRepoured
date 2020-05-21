package com.teamcqr.chocolatequestrepoured.objects.banners;

import java.util.ArrayList;
import java.util.List;

import com.teamcqr.chocolatequestrepoured.util.ReflectionHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.tileentity.TileEntityBanner;

public class BannerHelper {

	public BannerHelper() {
	}

	public static List<ItemStack> addBannersToTabs() {
		List<ItemStack> itemList = new ArrayList<>();
		for (EBanners banner : EBanners.values()) {
			itemList.add(banner.getBanner());
		}
		return itemList;
	}

	public static boolean isCQBanner(BannerTileEntity bannerTile) {
		List<BannerPattern> patterns = ReflectionHelper.getPrivateValue(BannerTileEntity.class, bannerTile, 4);
		if (patterns != null && !patterns.isEmpty()) {
			for (EBannerPatternsCQ cqPattern : EBannerPatternsCQ.values()) {
				if (patterns.contains(cqPattern.getPattern())) {
					return true;
				}
			}
		}
		return false;
	}

}
