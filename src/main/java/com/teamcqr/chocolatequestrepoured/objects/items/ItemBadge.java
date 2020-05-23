package com.teamcqr.chocolatequestrepoured.objects.items;

import java.util.List;

import javax.annotation.Nullable;

import com.teamcqr.chocolatequestrepoured.CQRMain;
import com.teamcqr.chocolatequestrepoured.capability.itemhandler.item.CapabilityItemHandlerItemProvider;
import com.teamcqr.chocolatequestrepoured.objects.entity.EntityEquipmentExtraSlot;
import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;
import com.teamcqr.chocolatequestrepoured.util.Reference;

import net.java.games.input.Keyboard;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ItemBadge extends Item {

	public ItemBadge() {
		this.setMaxStackSize(1);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
		if (player.isCreative()) {
			if (!player.world.isRemote) {
				if (entity instanceof AbstractEntityCQR) {
					((AbstractEntityCQR) entity).setItemStackToExtraSlot(EntityEquipmentExtraSlot.BADGE, stack.copy());
					((ServerWorld) player.world).spawnParticle((ServerPlayerEntity) player, EnumParticleTypes.SPELL_WITCH, false, entity.posX, entity.posY + 0.5D, entity.posZ, 8, 0.5D, 0.5D, 0.5D, 0.1D);
				} else {
					IItemHandler capability = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
					ListNBT itemList = new ListNBT();
					for (int i = 0; i < capability.getSlots(); i++) {
						ItemStack stack1 = capability.getStackInSlot(i);
						CompoundNBT slotTag = new CompoundNBT();
						slotTag.setInteger("Index", 0);
						stack1.writeToNBT(slotTag);
						itemList.appendTag(slotTag);
					}
					if (!itemList.hasNoTags()) {
						entity.getEntityData().setTag("Items", itemList);
						((ServerWorld) player.world).spawnParticle((ServerPlayerEntity) player, EnumParticleTypes.SPELL_WITCH, false, entity.posX, entity.posY + 0.5D, entity.posZ, 8, 0.5D, 0.5D, 0.5D, 0.1D);
					}
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		if (playerIn.isCreative()) {
			if (!worldIn.isRemote) {
				playerIn.openGui(CQRMain.INSTANCE, Reference.BADGE_GUI_ID, worldIn, handIn.ordinal(), 0, 0);
			}
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
		}
		return new ActionResult<ItemStack>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
	}

	@Override
	@OnlyIn(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			tooltip.add(TextFormatting.BLUE + I18n.format("description.badge.name"));
		} else {
			tooltip.add(TextFormatting.BLUE + I18n.format("description.click_shift.name"));
		}

		IItemHandler capability = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		for (int i = 0; i < capability.getSlots(); i++) {
			ItemStack stack1 = capability.getStackInSlot(i);
			if (!stack1.isEmpty()) {
				tooltip.add(stack1.getDisplayName() + " x" + stack1.getCount());
			}
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		return CapabilityItemHandlerItemProvider.createProvider(stack, 9);
	}

}
