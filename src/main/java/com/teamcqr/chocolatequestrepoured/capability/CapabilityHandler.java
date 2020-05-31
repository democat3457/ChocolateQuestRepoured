package com.teamcqr.chocolatequestrepoured.capability;

import com.teamcqr.chocolatequestrepoured.capability.armor.CapabilityCooldownHandlerProvider;
import com.teamcqr.chocolatequestrepoured.capability.extraitemhandler.CapabilityExtraItemHandlerProvider;
import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;
import com.teamcqr.chocolatequestrepoured.util.Reference;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Reference.MODID)
public class CapabilityHandler {

	@SubscribeEvent
	public static void attachCapabilitiesEvent(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof LivingEntity) {
			event.addCapability(CapabilityCooldownHandlerProvider.REGISTRY_NAME, CapabilityCooldownHandlerProvider.createProvider());
		}
		if (event.getObject() instanceof AbstractEntityCQR) {
			event.addCapability(CapabilityExtraItemHandlerProvider.REGISTRY_NAME, CapabilityExtraItemHandlerProvider.createProvider(3));
		}
	}

	public static void writeToItemStackNBT(ItemStack stack, String key, CompoundNBT compound) {
		CompoundNBT stackCompound = stack.getTag();

		if (stackCompound == null) {
			stackCompound = new CompoundNBT();
			stack.setTag(stackCompound);
		}

		stackCompound.put(key, compound);
	}

	public static CompoundNBT readFromItemStackNBT(ItemStack stack, String key) {
		CompoundNBT stackCompound = stack.getTag();
		return stackCompound != null ? stackCompound.getCompound(key) : new CompoundNBT();
	}

}
