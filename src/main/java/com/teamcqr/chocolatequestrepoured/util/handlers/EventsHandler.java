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
import com.teamcqr.chocolatequestrepoured.structuregen.structurefile.CQStructure;
import com.teamcqr.chocolatequestrepoured.util.CQRConfig;
import com.teamcqr.chocolatequestrepoured.util.Reference;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntityMP;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
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
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EventsHandler {

	@SubscribeEvent
	public static void onLootTableLoad(LootTableLoadEvent event) {
		if (event.getName().getNamespace().equals(Reference.MODID)) {
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

		if (event.getEntityLiving() instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) event.getEntityLiving();
			Entity attacker = event.getSource().getTrueSource();
			@SuppressWarnings("unused")
			float amount = event.getAmount();
			World world = player.world;

			if (player.getActiveItemStack().getItem() != ModItems.SHIELD_WALKER_KING || player.getHeldItemMainhand().getItem() != ModItems.SWORD_WALKER || player.getRidingEntity() != null || attacker == null) {
				return;
			}

			double d = attacker.getPosX() + (attacker.world.rand.nextDouble() - 0.5D) * 4.0D;
			double d1 = attacker.getPosY();
			double d2 = attacker.getPosZ() + (attacker.world.rand.nextDouble() - 0.5D) * 4.0D;

			int i = MathHelper.floor(d);
			int j = MathHelper.floor(d1);
			int k = MathHelper.floor(d2);

			BlockPos ep = new BlockPos(i, j, k);
			BlockPos ep1 = new BlockPos(i, j + 1, k);

			if (world.getCollisionShapes(player, player.getBoundingBox()).count() == 0 && !world.containsAnyLiquid(attacker.getBoundingBox()) && player.isActiveItemStackBlocking() && player.getDistanceSq(attacker) >= 25.0D) {
				if (world.getBlockState(ep).getBlock().isPassable(world, ep) && world.getBlockState(ep1).getBlock().isPassable(world, ep1)) {
					tep = true;
				} else {
					tep = false;
					if (!world.isRemote) {
						((WorldServer) world).spawnParticle(EnumParticleTypes.SMOKE_LARGE, player.getPosX(), player.getPosY() + player.getHeight() * 0.5D, player.getPosZ(), 12, 0.25D, 0.25D, 0.25D, 0.0D);
					}
				}
			}

			if (tep) {
				if (world.getBlockState(ep).getBlock().isPassable(world, ep) && world.getBlockState(ep1).getBlock().isPassable(world, ep1)) {
					if (player instanceof PlayerEntityMP) {
						PlayerEntityMP playerMP = (PlayerEntityMP) player;

						playerMP.connection.setPlayerLocation(d, d1, d2, playerMP.rotationYaw, playerMP.rotationPitch);
						if (!world.isRemote) {
							((WorldServer) world).spawnParticle(EnumParticleTypes.PORTAL, player.getPosX(), player.getPosY() + player.getHeight() * 0.5D, player.getPosZ(), 12, 0.25D, 0.25D, 0.25D, 0.0D);
						}
						world.playSound(null, d, d1, d2, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.MASTER, 1.0F, 1.0F);
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
		CompoundNBT tag = entity.serializeNBT();

		if (tag.contains("Items")) {
			ListNBT itemList = tag.getList("Items", Constants.NBT.TAG_COMPOUND);

			if (itemList == null) {
				return;
			}

			for (int i = 0; i < itemList.size(); i++) {
				CompoundNBT entry = itemList.getCompound(i);
				ItemStack stack = ItemStack.read(entry);

				if (stack != null) {
					if (!entity.world.isRemote) {
						entity.world.addEntity(new ItemEntity(entity.world, entity.getPosX() + rand.nextDouble(), entity.getPosY(), entity.getPosZ() + rand.nextDouble(), stack));
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load e) {
		DungeonDataManager.handleWorldLoad((World) e.getWorld());
	}

	@SubscribeEvent
	public static void onWorldCreateSpawnpoint(WorldEvent.CreateSpawnPosition e) {
		DungeonDataManager.handleWorldLoad((World) e.getWorld());
	}

	@SubscribeEvent
	public static void onWorldSave(WorldEvent.Save e) {
		DungeonDataManager.handleWorldSave((World) e.getWorld());
	}

	@SubscribeEvent
	public static void onRecipeRegister(RegistryEvent.Register<IRecipe> event) {
		event.getRegistry().register(new RecipesArmorDyes());
		event.getRegistry().register(new RecipeArmorDyableRainbow());
		event.getRegistry().register(new RecipeArmorDyableBreathing());
		event.getRegistry().register(new RecipeDynamicCrown());
	}

	@SuppressWarnings("deprecation")
	@SubscribeEvent
	public static void onWorldUnload(WorldEvent.Unload e) {
		if (!e.getWorld().isRemote()) {
			DungeonDataManager.handleWorldUnload((World) e.getWorld());
			// Stop export threads
			if (!CQStructure.RUNNING_EXPORT_THREADS.isEmpty()) {
				for (Thread t : CQStructure.RUNNING_EXPORT_THREADS) {
					try {
						t.stop();
					} catch (Exception ex) {

					}
				}
				CQStructure.RUNNING_EXPORT_THREADS.clear();
			}
		}
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
			PlayerEntity player = event.getPlayer();
			World world = player.world;

			if (!world.isRemote && event.getTarget() instanceof AbstractEntityCQR) {
				AbstractEntityCQR targetCQR = (AbstractEntityCQR) event.getTarget();

				if (targetCQR.canBlockDamageSource(DamageSource.causePlayerDamage(player)) && player.getHeldItemMainhand().getItem() instanceof AxeItem && player.getCooledAttackStrength(0) == 1.0F) {
					targetCQR.setLastTimeHitByAxeWhileBlocking(targetCQR.ticksExisted);
				}
			}
		}
	}

}
