package com.teamcqr.chocolatequestrepoured.objects.banners;

import java.util.ArrayList;
import java.util.List;

import com.teamcqr.chocolatequestrepoured.util.ReflectionHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.BannerTileEntity;

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
		@SuppressWarnings("unchecked")
		List<BannerPattern> patterns = (List<BannerPattern>) ReflectionHelper.reflectGetFieldValue(bannerTile, BannerTileEntity.class.getFields()[4]);
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
