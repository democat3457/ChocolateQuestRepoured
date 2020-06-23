package com.teamcqr.chocolatequestrepoured.objects.items.staves;

import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import com.teamcqr.chocolatequestrepoured.objects.entity.projectiles.ProjectilePoisonSpell;
import com.teamcqr.chocolatequestrepoured.util.IRangedWeapon;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemStaffPoison extends Item implements IRangedWeapon {

	public ItemStaffPoison() {
		this.setMaxDamage(2048);
		this.setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);
		this.shoot(stack, worldIn, playerIn, handIn);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	public void shoot(ItemStack stack, World worldIn, EntityPlayer player, EnumHand handIn) {
		worldIn.playSound(player.posX, player.posY, player.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.MASTER, 4.0F, (1.0F + (itemRand.nextFloat() - itemRand.nextFloat()) * 0.2F) * 0.7F, false);
		player.swingArm(handIn);

		if (!worldIn.isRemote) {
			ProjectilePoisonSpell spell = new ProjectilePoisonSpell(worldIn, player);
			spell.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 2.0F, 0F);
			worldIn.spawnEntity(spell);
			stack.damageItem(1, player);
			player.getCooldownTracker().setCooldown(stack.getItem(), 20);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			tooltip.add(TextFormatting.BLUE + I18n.format("description.staff_poison.name"));
		} else {
			tooltip.add(TextFormatting.BLUE + I18n.format("description.click_shift.name"));
		}
	}

	@Override
	public void shoot(World worldIn, EntityLivingBase shooter, Entity target, EnumHand handIn) {
		shooter.swingArm(handIn);

		if (!worldIn.isRemote) {
			ProjectilePoisonSpell spell = new ProjectilePoisonSpell(worldIn, shooter);
			Vec3d v = target.getPositionVector().subtract(shooter.getPositionVector());
			v = v.normalize();
			v = v.scale(2D);
			// spell.setVelocity(v.x, v.y, v.z);
			spell.motionX = v.x;
			spell.motionY = v.y;
			spell.motionZ = v.z;
			spell.velocityChanged = true;
			worldIn.spawnEntity(spell);
		}
	}

	@Override
	public SoundEvent getShootSound() {
		return SoundEvents.ENTITY_SNOWBALL_THROW;
	}

	@Override
	public double getRange() {
		return 32.0D;
	}

	@Override
	public int getCooldown() {
		return 20;
	}

	@Override
	public int getChargeTicks() {
		return 0;
	}

}
