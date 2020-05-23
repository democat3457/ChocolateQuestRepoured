package com.teamcqr.chocolatequestrepoured.objects.items;

import java.util.List;

import javax.annotation.Nullable;

import net.java.games.input.Keyboard;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.relauncher.Side;

public class ItemTeleportStone extends Item {
	private String X = "x";
	private String Y = "y";
	private String Z = "z";

	public ItemTeleportStone() {
		this.setMaxDamage(100);
		
		setMaxStackSize(1);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		playerIn.getCooldownTracker().setCooldown(stack.getItem(), 30);
		playerIn.setActiveHand(handIn);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
		if (entityLiving instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entityLiving;

			if (stack.hasTagCompound() && !player.isSneaking()) {
				if (stack.getTagCompound().hasKey(this.X) && stack.getTagCompound().hasKey(this.Y) && stack.getTagCompound().hasKey(this.Z) && this.getMaxItemUseDuration(stack) - timeLeft >= 30) {
					player.setPosition(stack.getTagCompound().getDouble(this.X), stack.getTagCompound().getDouble(this.Y), stack.getTagCompound().getDouble(this.Z));
					for (int i = 0; i < 30; i++) {
						worldIn.spawnParticle(EnumParticleTypes.PORTAL, player.posX + worldIn.rand.nextDouble() - 0.5D, player.posY + 0.5D, player.posZ + worldIn.rand.nextDouble() - 0.5D, 0D, 0D, 0D);
					}
					worldIn.playSound(player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.AMBIENT, 1.0F, 1.0F, false);

					if (!player.capabilities.isCreativeMode) {
						stack.damageItem(1, entityLiving);
					}
				}
			}

			if (this.getPoint(stack) == null && this.getMaxItemUseDuration(stack) - timeLeft >= 30) {
				this.setPoint(stack, player);
				for (int i = 0; i < 10; i++) {
					worldIn.spawnParticle(EnumParticleTypes.FLAME, player.posX + worldIn.rand.nextDouble() - 0.5D, player.posY + 0.5D, player.posZ + worldIn.rand.nextDouble() - 0.5D, 0D, 0D, 0D);
				}
				worldIn.playSound(player.posX, player.posY, player.posZ, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.AMBIENT, 1.0F, 1.0F, false);
			}

			if (player.isSneaking() && stack.hasTagCompound() && this.getMaxItemUseDuration(stack) - timeLeft >= 30) {
				stack.getTagCompound().removeTag(this.X);
				stack.getTagCompound().removeTag(this.Y);
				stack.getTagCompound().removeTag(this.Z);
				worldIn.playSound(player.posX, player.posY, player.posZ, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.AMBIENT, 1.0F, 1.0F, false);
				for (int i = 0; i < 10; i++) {
					worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, player.posX + worldIn.rand.nextDouble() - 0.5D, player.posY + 0.5D, player.posZ + worldIn.rand.nextDouble() - 0.5D, 0D, 0D, 0D);
				}
			}
		}
	}

	@Override
	@OnlyIn(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			tooltip.add(TextFormatting.BLUE + I18n.format("description.teleport_stone.name"));

			if (stack.hasTagCompound()) {
				if (stack.getTagCompound().hasKey(this.X) && stack.getTagCompound().hasKey(this.Y) && stack.getTagCompound().hasKey(this.Z)) {
					tooltip.add(TextFormatting.BLUE + I18n.format("description.teleport_stone_position.name"));
					tooltip.add(TextFormatting.BLUE + I18n.format("X: " + (int) stack.getTagCompound().getDouble(this.X)));
					tooltip.add(TextFormatting.BLUE + I18n.format("Y: " + (int) stack.getTagCompound().getDouble(this.Y)));
					tooltip.add(TextFormatting.BLUE + I18n.format("Z: " + (int) stack.getTagCompound().getDouble(this.Z)));
				}
			}
		} else {
			tooltip.add(TextFormatting.BLUE + I18n.format("description.click_shift.name"));
		}
	}

	private void setPoint(ItemStack stack, PlayerEntity player) {
		CompoundNBT stone = stack.getTagCompound();

		if (stone == null) {
			stone = new CompoundNBT();
			stack.setTagCompound(stone);
		}

		if (!stone.hasKey(this.X)) {
			stone.setDouble(this.X, player.posX);
		}

		if (!stone.hasKey(this.Y)) {
			stone.setDouble(this.Y, player.posY);
		}

		if (!stone.hasKey(this.Z)) {
			stone.setDouble(this.Z, player.posZ);
		}
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return getPoint(stack) != null;
	}
	
	@Nullable
	private BlockPos getPoint(ItemStack stack) {
		if (stack.hasTagCompound()) {
			if (stack.getTagCompound().hasKey(this.X) && stack.getTagCompound().hasKey(this.Y) && stack.getTagCompound().hasKey(this.Z)) {
				CompoundNBT stone = stack.getTagCompound();

				double x = stone.getDouble(this.X);
				double y = stone.getDouble(this.Y);
				double z = stone.getDouble(this.Z);

				return new BlockPos(x, y, z);
			}
		}
		return null;
	}
}