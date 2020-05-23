package com.teamcqr.chocolatequestrepoured.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerBadge extends Container {

	private final ItemStack stack;
	private final Hand hand;

	public ContainerBadge(InventoryPlayer playerInv, ItemStack stack, Hand hand) {
		this.stack = stack;
		this.hand = hand;
		IItemHandler inventory = stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		int currentItemIndex = playerInv.currentItem;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for (int k = 0; k < 9; k++) {
			if (k != currentItemIndex) {
				this.addSlotToContainer(new Slot(playerInv, k, 8 + k * 18, 142));
			} else {
				this.addSlotToContainer(new Slot(playerInv, k, 8 + k * 18, 142) {
					@Override
					public boolean canTakeStack(PlayerEntity playerIn) {
						return false;
					}
				});
			}
		}

		for (int l = 0; l < 3; l++) {
			for (int m = 0; m < 3; m++) {
				this.addSlotToContainer(new SlotItemHandler(inventory, m + l * 3, 62 + m * 18, 17 + l * 18));
			}
		}
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return playerIn.isCreative();
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			ItemStack itemstack = itemstack1.copy();

			if (index > 35) {
				if (this.mergeItemStack(itemstack1, 0, 36, false)) {
					return itemstack;
				}
			} else {
				if (this.mergeItemStack(itemstack1, 36, this.inventorySlots.size(), false)) {
					return itemstack;
				}
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public void onContainerClosed(PlayerEntity playerIn) {
		super.onContainerClosed(playerIn);
		playerIn.setHeldItem(this.hand, this.stack);
	}

}
