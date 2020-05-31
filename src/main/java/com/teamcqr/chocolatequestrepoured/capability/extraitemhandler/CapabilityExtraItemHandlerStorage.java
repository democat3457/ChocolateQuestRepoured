package com.teamcqr.chocolatequestrepoured.capability.extraitemhandler;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.items.IItemHandlerModifiable;

public class CapabilityExtraItemHandlerStorage implements IStorage<CapabilityExtraItemHandler> {

	@Override
	public INBT writeNBT(Capability<CapabilityExtraItemHandler> capability, CapabilityExtraItemHandler instance, Direction side) {
		ListNBT nbtTagList = new ListNBT();
		int size = instance.getSlots();
		for (int i = 0; i < size; i++) {
			ItemStack stack = instance.getStackInSlot(i);
			if (!stack.isEmpty()) {
				CompoundNBT itemTag = new CompoundNBT();
				itemTag.putInt("Slot", i);
				stack.write(itemTag);
				nbtTagList.add(itemTag);
			}
		}
		return nbtTagList;
	}

	@Override
	public void readNBT(Capability<CapabilityExtraItemHandler> capability, CapabilityExtraItemHandler instance, Direction side, INBT base) {
		if (!(instance instanceof IItemHandlerModifiable)) {
			throw new RuntimeException("IItemHandler instance does not implement IItemHandlerModifiable");
		}
		IItemHandlerModifiable itemHandlerModifiable = (IItemHandlerModifiable) instance;
		ListNBT tagList = (ListNBT) base;
		for (int i = 0; i < tagList.size(); i++) {
			CompoundNBT itemTags = tagList.getCompound(i);
			int j = itemTags.getInt("Slot");

			if (j >= 0 && j < instance.getSlots()) {
				itemHandlerModifiable.setStackInSlot(j, ItemStack.read(itemTags));
			}
		}
	}

}
