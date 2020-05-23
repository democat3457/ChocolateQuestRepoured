package com.teamcqr.chocolatequestrepoured;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.teamcqr.chocolatequestrepoured.command.CommandExport;
import com.teamcqr.chocolatequestrepoured.factions.FactionRegistry;
import com.teamcqr.chocolatequestrepoured.init.ModBlocks;
import com.teamcqr.chocolatequestrepoured.init.ModCapabilities;
import com.teamcqr.chocolatequestrepoured.init.ModDispenseBehaviors;
import com.teamcqr.chocolatequestrepoured.init.ModItems;
import com.teamcqr.chocolatequestrepoured.init.ModMaterials;
import com.teamcqr.chocolatequestrepoured.init.ModMessages;
import com.teamcqr.chocolatequestrepoured.init.ModSerializers;
import com.teamcqr.chocolatequestrepoured.objects.banners.BannerHelper;
import com.teamcqr.chocolatequestrepoured.objects.banners.EBannerPatternsCQ;
import com.teamcqr.chocolatequestrepoured.objects.banners.EBanners;
import com.teamcqr.chocolatequestrepoured.objects.entity.boss.EntityCQRNetherDragon;
import com.teamcqr.chocolatequestrepoured.proxy.IProxy;
import com.teamcqr.chocolatequestrepoured.structuregen.DungeonRegistry;
import com.teamcqr.chocolatequestrepoured.structuregen.WorldDungeonGenerator;
import com.teamcqr.chocolatequestrepoured.structuregen.structurefile.CQStructurePart;
import com.teamcqr.chocolatequestrepoured.structuregen.thewall.WorldWallGenerator;
import com.teamcqr.chocolatequestrepoured.structureprot.ProtectedRegionEventHandler;
import com.teamcqr.chocolatequestrepoured.util.CQRConfig;
import com.teamcqr.chocolatequestrepoured.util.CopyHelper;
import com.teamcqr.chocolatequestrepoured.util.Reference;
import com.teamcqr.chocolatequestrepoured.util.handlers.GuiHandler;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.network.NetworkRegistry;

//@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, dependencies = "required-after:llibrary@[1.7.19]; required:forge@14.23.5.2847")
@Mod(value = Reference.MODID)
public class CQRMain {

	public static CQRMain INSTANCE;

	public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MODID);

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
	public static IProxy proxy;

	public static Logger logger = null;

	public static File CQ_CONFIG_FOLDER = null;
	public static File CQ_DUNGEON_FOLDER = null;
	public static File CQ_STRUCTURE_FILES_FOLDER = null;
	public static File CQ_EXPORT_FILES_FOLDER = null;
	public static File CQ_CHEST_FOLDER = null;
	public static File CQ_FACTION_FOLDER = null;
	public static File CQ_ITEM_FOLDER = null;

	public static final ItemGroup CQR_ITEMS_TAB = new ItemGroup("ChocolateQuestRepouredItemsTab") {

		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModItems.BOOTS_CLOUD);
		}
	};
	public static final ItemGroup CQR_BLOCKS_TAB = new ItemGroup("ChocolateQuestRepouredBlocksTab") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModBlocks.TABLE_OAK);
		}
	};
	public static final ItemGroup CQR_BANNERS_TAB = new ItemGroup("ChocolateQuestRepouredBannerTab") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Items.RED_BANNER);
		}
		
		
		
		@Override
		public void fill(NonNullList<ItemStack> itemList) {
			super.fill(itemList);
			List<ItemStack> banners = BannerHelper.addBannersToTabs();
			for (ItemStack stack : banners) {
				itemList.add(stack);
			}
		}
	};
	public static final ItemGroup CQR_DUNGEON_PLACER_TAB = new ItemGroup("ChocolateQuestRepouredDungeonPlacers") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.STONE_BRICKS);
		}
	};
	public static final ItemGroup CQR_EXPORTER_CHEST_TAB = new ItemGroup("ChocolateQuestRepouredExporterChests") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.CHEST);
		}
	};
	public static final ItemGroup CQR_SPAWN_EGG_TAB = new ItemGroup("CQR Spawn Eggs") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Items.GHAST_SPAWN_EGG);
		}
	};

	@SubscribeEvent
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		// Important: This has to be the F I R S T statement
		this.initConfigFolder(event);

		proxy.preInit();

		// Faction system
		FactionRegistry.instance().loadFactions();

		// Enables Dungeon generation in worlds, do not change the number (!) and do NOT
		// remove this line, moving it somewhere else is fine, but it must be called in
		// pre initialization (!)
		GameRegistry.registerWorldGenerator(new WorldDungeonGenerator(), 100);
		if (CQRConfig.wall.enabled) {
			GameRegistry.registerWorldGenerator(new WorldWallGenerator(), 101);
		}

		// Instantiating enums
		EBannerPatternsCQ.values();
		EBanners.values();

		// Register event handling for dungeon protection system
		// MinecraftForge.EVENT_BUS.register(ProtectedRegionManager.getInstance());
		MinecraftForge.EVENT_BUS.register(ModSerializers.class);

		ModMessages.registerMessages();
		ModCapabilities.registerCapabilities();
	}

	private void initConfigFolder(FMLPreInitializationEvent event) {
		boolean installCQ = false;

		CQ_CONFIG_FOLDER = new File(event.getModConfigurationDirectory(), "CQR");
		CQ_DUNGEON_FOLDER = new File(CQ_CONFIG_FOLDER, "dungeons");
		CQ_CHEST_FOLDER = new File(CQ_CONFIG_FOLDER, "lootconfigs");
		CQ_STRUCTURE_FILES_FOLDER = new File(CQ_CONFIG_FOLDER, "structures");
		CQ_EXPORT_FILES_FOLDER = new File(CQ_CONFIG_FOLDER, "exporter_output");
		CQ_FACTION_FOLDER = new File(CQ_CONFIG_FOLDER, "factions");
		CQ_ITEM_FOLDER = new File(CQ_CONFIG_FOLDER, "items");

		if (!CQ_CONFIG_FOLDER.exists()) {
			CQ_CONFIG_FOLDER.mkdir();

			installCQ = true;
		} else if (CQRConfig.general.reinstallDefaultConfigs) {
			installCQ = true;
		}
		if (!CQ_DUNGEON_FOLDER.exists()) {
			CQ_DUNGEON_FOLDER.mkdir();
			installCQ = true;
		}
		if (!CQ_CHEST_FOLDER.exists()) {
			CQ_CHEST_FOLDER.mkdir();
			installCQ = true;
		}
		if (!CQ_STRUCTURE_FILES_FOLDER.exists()) {
			CQ_STRUCTURE_FILES_FOLDER.mkdir();
			installCQ = true;
		}
		if (!CQ_EXPORT_FILES_FOLDER.exists()) {
			CQ_EXPORT_FILES_FOLDER.mkdir();
		}
		if (!CQ_FACTION_FOLDER.exists()) {
			CQ_FACTION_FOLDER.mkdir();
			installCQ = true;
		}
		if (!CQ_ITEM_FOLDER.exists()) {
			CQ_ITEM_FOLDER.mkdir();
			installCQ = true;
		}

		if (installCQ) {
			try {
				CopyHelper.copyFromJar("/assets/cqrepoured/defaultConfigs", CQ_CONFIG_FOLDER.toPath());
			} catch (URISyntaxException | IOException e) {
				logger.error("Failed to copy config files", e);
			}
		}
	}

	@SubscribeEvent
	public void init(FMLInitializationEvent event) {
		proxy.init();

		NetworkRegistry.INSTANCE.registerGuiHandler(CQRMain.INSTANCE, new GuiHandler());
		ModMaterials.setRepairItemsForMaterials();
		// SmeltingHandler.init();
		Blocks.FIRE.init();
	}

	@SubscribeEvent
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();

		DungeonRegistry.getInstance().loadDungeons();
		CQStructurePart.updateSpecialBlocks();
		CQStructurePart.updateSpecialEntities();
		ProtectedRegionEventHandler.updateBreakableBlockWhitelist();
		ProtectedRegionEventHandler.updatePlaceableBlockWhitelist();
		ModDispenseBehaviors.registerDispenseBehaviors();
		EntityCQRNetherDragon.reloadBreakableBlocks();
	}

	@SubscribeEvent
	public static void registerCommands(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandExport());
	}

}
