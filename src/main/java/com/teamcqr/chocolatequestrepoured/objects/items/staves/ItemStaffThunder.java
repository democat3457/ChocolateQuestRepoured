package com.teamcqr.chocolatequestrepoured.objects.items.staves;

import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import com.teamcqr.chocolatequestrepoured.init.ModSounds;
import com.teamcqr.chocolatequestrepoured.objects.entity.misc.EntityColoredLightningBolt;
import com.teamcqr.chocolatequestrepoured.util.IRangedWeapon;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemStaffThunder extends Item implements IRangedWeapon {

	public ItemStaffThunder() {
		this.setMaxDamage(2048);
		this.setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);

		if (this.isNotAirBlock(worldIn, playerIn)) {
			if (!worldIn.isRemote) {
				playerIn.swingArm(handIn);
				this.spawnLightningBolt(playerIn, worldIn);
				stack.damageItem(1, playerIn);
				playerIn.getCooldownTracker().setCooldown(stack.getItem(), 20);
			}
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}

		return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
	}

	public void spawnLightningBolt(EntityPlayer player, World worldIn) {
		if (!worldIn.isRemote) {
			Vec3d start = player.getPositionEyes(1.0F);
			Vec3d end = start.add(player.getLookVec().scale(20.0D));
			RayTraceResult result = worldIn.rayTraceBlocks(start, end);

			if (result != null) {
				EntityColoredLightningBolt entity = new EntityColoredLightningBolt(worldIn, result.hitVec.x, result.hitVec.y, result.hitVec.z, true, false);
				worldIn.spawnEntity(entity);
			}
		}
	}

	public boolean isNotAirBlock(World worldIn, EntityPlayer player) {
		Vec3d start = player.getPositionEyes(1.0F);
		Vec3d end = start.add(player.getLookVec().scale(20.0D));
		RayTraceResult result = worldIn.rayTraceBlocks(start, end);

		return result != null && !worldIn.isAirBlock(result.getBlockPos());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			tooltip.add(TextFormatting.BLUE + I18n.format("description.staff_thunder.name"));
		} else {
			tooltip.add(TextFormatting.BLUE + I18n.format("description.click_shift.name"));
		}
	}

	@Override
	public void shoot(World worldIn, EntityLivingBase shooter, Entity target, EnumHand handIn) {
		Vec3d v = target.getPositionVector().subtract(shooter.getPositionVector());
		Vec3d pos = target.getPositionVector();
		if (v.lengthVector() > 20) {
			v = v.normalize();
			v = v.scale(20D);
			pos = shooter.getPositionVector().add(v);
		}
		EntityColoredLightningBolt entity = new EntityColoredLightningBolt(worldIn, pos.x, pos.y, pos.z, true, false);
		worldIn.spawnEntity(entity);
	}

	@Override
	public SoundEvent getShootSound() {
		return ModSounds.MAGIC;
	}

	@Override
	public double getRange() {
		return 32.0D;
	}

	@Override
	public int getCooldown() {
		return 80;
	}

	@Override
	public int getChargeTicks() {
		return 0;
	}

}
