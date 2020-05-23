package com.teamcqr.chocolatequestrepoured.objects.items.armor;

import javax.annotation.Nullable;

import com.teamcqr.chocolatequestrepoured.client.init.ModArmorModels;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.relauncher.Side;

public class ItemArmorInquisition extends ItemArmor {

	public ItemArmorInquisition(ArmorMaterial materialIn, int renderIndexIn, EquipmentSlotType equipmentSlotIn) {
		super(materialIn, renderIndexIn, equipmentSlotIn);
	}

	@Override
	@OnlyIn(Side.CLIENT)
	@Nullable
	public ModelBiped getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, ModelBiped _default) {
		return armorSlot == EquipmentSlotType.LEGS ? ModArmorModels.inquisitionArmorLegs : ModArmorModels.inquisitionArmor;
	}

}
