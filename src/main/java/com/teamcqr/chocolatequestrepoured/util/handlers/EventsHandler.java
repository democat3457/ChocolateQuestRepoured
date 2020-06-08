package com.teamcqr.chocolatequestrepoured.util.handlers;

import java.util.Random;

import com.teamcqr.chocolatequestrepoured.CQRMain;
import com.teamcqr.chocolatequestrepoured.crafting.RecipeArmorDyableBreathing;
import com.teamcqr.chocolatequestrepoured.crafting.RecipeArmorDyableRainbow;
import com.teamcqr.chocolatequestrepoured.crafting.RecipeDynamicCrown;
import com.teamcqr.chocolatequestrepoured.crafting.RecipesArmorDyes;
import com.teamcqr.chocolatequestrepoured.factions.FactionRegistry;
import com.teamcqr.chocolatequestrepoured.init.ModItems;
import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;
import com.teamcqr.chocolatequestrepoured.structuregen.DungeonDataManager;
import com.teamcqr.chocolatequestrepoured.structuregen.lootchests.LootTableLoader;
import com.teamcqr.chocolatequestrepoured.util.CQRConfig;
import com.teamcqr.chocolatequestrepoured.util.Reference;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

@EventBusSubscriber
public class EventsHandler {

	@SubscribeEvent
	public static void onLootTableLoad(LootTableLoadEvent event) {
		if (event.getName().getResourceDomain().equals(Reference.MODID)) {
			try {
				event.setTable(LootTableLoader.fillLootTable(event.getName(), event.getTable()));
			} catch (Exception e) {
				CQRMain.logger.error("Unable to fill loot table {}", event.getName());
			}
		}
	}

	@SubscribeEvent
	public static void onDefense(LivingAttackEvent event) {
		boolean tep = false;

		if (event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			Entity attacker = event.getSource().getTrueSource();
			@SuppressWarnings("unused")
			float amount = event.getAmount();
			World world = player.world;

			if (player.getActiveItemStack().getItem() != ModItems.SHIELD_WALKER_KING || player.getHeldItemMainhand().getItem() != ModItems.SWORD_WALKER || player.getRidingEntity() != null || attacker == null) {
				return;
			}

			double d = attacker.posX + (attacker.world.rand.nextDouble() - 0.5D) * 4.0D;
			double d1 = attacker.posY;
			double d2 = attacker.posZ + (attacker.world.rand.nextDouble() - 0.5D) * 4.0D;

			@SuppressWarnings("unused")
			double d3 = player.posX;
			@SuppressWarnings("unused")
			double d4 = player.posY;
			@SuppressWarnings("unused")
			double d5 = player.posZ;

			int i = MathHelper.floor(d);
			int j = MathHelper.floor(d1);
			int k = MathHelper.floor(d2);

			BlockPos ep = new BlockPos(i, j, k);
			BlockPos ep1 = new BlockPos(i, j + 1, k);

			if (world.getCollisionBoxes(player, player.getEntityBoundingBox()).size() == 0 && !world.containsAnyLiquid(attacker.getEntityBoundingBox()) && player.isActiveItemStackBlocking() && player.getDistanceSq(attacker) >= 25.0D) {
				if (world.getBlockState(ep).getBlock().isPassable(world, ep) && world.getBlockState(ep1).getBlock().isPassable(world, ep1)) {
					tep = true;
				} else {
					tep = false;
					if (!world.isRemote) {
						((WorldServer) world).spawnParticle(EnumParticleTypes.SMOKE_LARGE, player.posX, player.posY + player.height * 0.5D, player.posZ, 12, 0.25D, 0.25D, 0.25D, 0.0D);
					}
				}
			}

			if (tep) {
				if (world.getBlockState(ep).getBlock().isPassable(world, ep) && world.getBlockState(ep1).getBlock().isPassable(world, ep1)) {
					if (player instanceof EntityPlayerMP) {
						EntityPlayerMP playerMP = (EntityPlayerMP) player;

						playerMP.connection.setPlayerLocation(d, d1, d2, playerMP.rotationYaw, playerMP.rotationPitch);
						if (!world.isRemote) {
							((WorldServer) world).spawnParticle(EnumParticleTypes.PORTAL, player.posX, player.posY + player.height * 0.5D, player.posZ, 12, 0.25D, 0.25D, 0.25D, 0.0D);
						}
						world.playSound(null, d, d1, d2, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.MASTER, 1.0F, 1.0F);
					}
					event.setCanceled(true);
					tep = false;
				}
			}
		}
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {
		Random rand = new Random();
		Entity entity = event.getEntity();
		NBTTagCompound tag = entity.getEntityData();

		if (tag.hasKey("Items")) {
			NBTTagList itemList = tag.getTagList("Items", Constants.NBT.TAG_COMPOUND);

			if (itemList == null) {
				return;
			}

			for (int i = 0; i < itemList.tagCount(); i++) {
				NBTTagCompound entry = itemList.getCompoundTagAt(i);
				ItemStack stack = new ItemStack(entry);

				if (stack != null) {
					if (!entity.world.isRemote) {
						entity.world.spawnEntity(new EntityItem(entity.world, entity.posX + rand.nextDouble(), entity.posY, entity.posZ + rand.nextDouble(), stack));
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load e) {
		DungeonDataManager.handleWorldLoad(e.getWorld());
	}

	@SubscribeEvent
	public static void onWorldCreateSpawnpoint(WorldEvent.CreateSpawnPosition e) {
		DungeonDataManager.handleWorldLoad(e.getWorld());
	}

	@SubscribeEvent
	public static void onWorldSave(WorldEvent.Save e) {
		DungeonDataManager.handleWorldSave(e.getWorld());
	}

	@SubscribeEvent
	public static void onRecipeRegister(RegistryEvent.Register<IRecipe> event) {
		event.getRegistry().register(new RecipesArmorDyes().setRegistryName(Reference.MODID, "armor_coloring"));
		event.getRegistry().register(new RecipeArmorDyableRainbow());
		event.getRegistry().register(new RecipeArmorDyableBreathing());
		event.getRegistry().register(new RecipeDynamicCrown().setRegistryName(Reference.MODID, "dynamic_king_crown"));
	}

	@SuppressWarnings("deprecation")
	@SubscribeEvent
	public static void onWorldUnload(WorldEvent.Unload e) {
	}

	@SubscribeEvent
	public static void onPlayerLogin(PlayerLoggedInEvent event) {
		if (event.isCanceled()) {
			return;
		}
		FactionRegistry.instance().handlePlayerLogin(event);
	}

	@SubscribeEvent
	public static void onPlayerLogout(PlayerLoggedOutEvent event) {
		if (event.isCanceled()) {
			return;
		}
		FactionRegistry.instance().handlePlayerLogout(event);
	}

	@SubscribeEvent
	public static void onAttackEntityEvent(AttackEntityEvent event) {
		if (CQRConfig.mobs.blockCancelledByAxe) {
			EntityPlayer player = event.getEntityPlayer();
			World world = player.world;

			if (!world.isRemote && event.getTarget() instanceof AbstractEntityCQR) {
				AbstractEntityCQR targetCQR = (AbstractEntityCQR) event.getTarget();

				if (targetCQR.canBlockDamageSource(DamageSource.causePlayerDamage(player)) && player.getHeldItemMainhand().getItem() instanceof ItemAxe && player.getCooledAttackStrength(0) == 1.0F) {
					targetCQR.setLastTimeHitByAxeWhileBlocking(targetCQR.ticksExisted);
				}
			}
		}
	}

}
