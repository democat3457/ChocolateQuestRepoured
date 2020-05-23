package com.teamcqr.chocolatequestrepoured.objects.items.armor;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Multimap;
import com.teamcqr.chocolatequestrepoured.client.init.ModArmorModels;
import com.teamcqr.chocolatequestrepoured.util.ItemUtil;

import net.java.games.input.Keyboard;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.relauncher.Side;

public class ItemArmorBull extends ItemArmor {

	private AttributeModifier strength;

	public ItemArmorBull(ArmorMaterial materialIn, int renderIndexIn, EquipmentSlotType equipmentSlotIn) {
		super(materialIn, renderIndexIn, equipmentSlotIn);

		this.strength = new AttributeModifier("BullArmorModifier", 1D, 0);
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

		if (slot == EntityLiving.getSlotForItemStack(stack)) {
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), this.strength);
		}

		return multimap;
	}

	@Override
	@OnlyIn(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			tooltip.add(TextFormatting.BLUE + I18n.format("description.bull_armor.name"));
		} else {
			tooltip.add(TextFormatting.BLUE + I18n.format("description.click_shift.name"));
		}
	}

	@Override
	public void onArmorTick(World worldIn, PlayerEntity player, ItemStack stack) {
		if (ItemUtil.hasFullSet(player, ItemArmorBull.class)) {
			if (player.isSprinting()) {
				player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 0, 1, false, false));
			}
		}
	}

	@Override
	@OnlyIn(Side.CLIENT)
	@Nullable
	public ModelBiped getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, ModelBiped _default) {
		return armorSlot == EquipmentSlotType.LEGS ? ModArmorModels.bullArmorLegs : ModArmorModels.bullArmor;
	}

}
