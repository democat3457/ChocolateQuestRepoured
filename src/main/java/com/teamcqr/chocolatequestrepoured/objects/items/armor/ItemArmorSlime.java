package com.teamcqr.chocolatequestrepoured.objects.items.armor;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Multimap;
import com.teamcqr.chocolatequestrepoured.capability.armor.CapabilityCooldownHandlerHelper;
import com.teamcqr.chocolatequestrepoured.client.init.ModArmorModels;
import com.teamcqr.chocolatequestrepoured.init.ModItems;
import com.teamcqr.chocolatequestrepoured.objects.entity.EntitySlimePart;
import com.teamcqr.chocolatequestrepoured.util.ItemUtil;
import com.teamcqr.chocolatequestrepoured.util.Reference;

import net.java.games.input.Keyboard;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.relauncher.Side;

public class ItemArmorSlime extends ItemArmor {

	private AttributeModifier health;
	private AttributeModifier knockBack;

	public ItemArmorSlime(ArmorMaterial materialIn, int renderIndexIn, EquipmentSlotType equipmentSlotIn) {
		super(materialIn, renderIndexIn, equipmentSlotIn);

		this.health = new AttributeModifier("SlimeHealthModifier", 2D, 0);
		this.knockBack = new AttributeModifier("SlimeKnockbackModifier", -0.25D, 0);
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
		Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);

		if (slot == EntityLiving.getSlotForItemStack(stack)) {
			multimap.put(SharedMonsterAttributes.MAX_HEALTH.getName(), this.health);
			multimap.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), this.knockBack);
		}

		return multimap;
	}

	@Override
	@OnlyIn(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			tooltip.add(TextFormatting.BLUE + I18n.format("description.slime_armor.name"));
		} else {
			tooltip.add(TextFormatting.BLUE + I18n.format("description.click_shift.name"));
		}
	}

	@Override
	@OnlyIn(Side.CLIENT)
	@Nullable
	public ModelBiped getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, ModelBiped _default) {
		return armorSlot == EquipmentSlotType.LEGS ? ModArmorModels.slimeArmorLegs : ModArmorModels.slimeArmor;
	}

	@EventBusSubscriber(modid = Reference.MODID)
	public static class EventHandler {

		@SubscribeEvent
		public static void onLivingHurtEvent(LivingHurtEvent event) {
			LivingEntity entity = event.getEntityLiving();

			if (ItemUtil.hasFullSet(entity, ItemArmorSlime.class) && !CapabilityCooldownHandlerHelper.onCooldown(entity, ModItems.CHESTPLATE_SLIME)) {
				if (!entity.world.isRemote) {
					EntitySlimePart slime = new EntitySlimePart(entity.world, entity);
					double x = entity.posX - 5.0D + 2.5D * slime.getRNG().nextDouble();
					double y = entity.posY;
					double z = entity.posZ - 5.0D + 2.5D * slime.getRNG().nextDouble();
					slime.setPosition(x, y, z);
					entity.world.spawnEntity(slime);
				}
				CapabilityCooldownHandlerHelper.setCooldown(entity, ModItems.CHESTPLATE_SLIME, 160);
			}
		}

	}

}
