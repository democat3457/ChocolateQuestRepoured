package com.teamcqr.chocolatequestrepoured.objects.enchantments;

import com.teamcqr.chocolatequestrepoured.init.ModEnchantments;
import com.teamcqr.chocolatequestrepoured.util.Reference;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class EnchantmentLightningProtection extends Enchantment {

	public EnchantmentLightningProtection() {
		this(Rarity.RARE, EnumEnchantmentType.ARMOR_HEAD, new EquipmentSlotType[] { EquipmentSlotType.HEAD });
	}

	private EnchantmentLightningProtection(Rarity rarityIn, EnumEnchantmentType typeIn, EquipmentSlotType[] slots) {
		super(rarityIn, typeIn, slots);
		this.setName("lightning_protection");
		this.setRegistryName(Reference.MODID, "lightning_protection");
	}

	@Override
	public int getMaxLevel() {
		return 8;
	}

	@Override
	public boolean isTreasureEnchantment() {
		return true;
	}

	@Override
	public int getMinLevel() {
		return 1;
	}

	@SubscribeEvent
	public static void onStruckByLightning(EntityStruckByLightningEvent event) {
		if (event.getEntity() instanceof LivingEntity) {
			LivingEntity living = (LivingEntity) event.getEntity();
			ItemStack helmet = living.getItemStackFromSlot(EquipmentSlotType.HEAD);

			int lvl = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.LIGHTNING_PROTECTION, helmet);
			if (lvl > 0 && lvl > living.getRNG().nextInt(10)) {
				event.setCanceled(true);
			}
		}
	}

}
