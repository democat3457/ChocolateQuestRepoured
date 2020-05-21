package com.teamcqr.chocolatequestrepoured.factions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;

import com.teamcqr.chocolatequestrepoured.CQRMain;
import com.teamcqr.chocolatequestrepoured.factions.EReputationState.EReputationStateRough;
import com.teamcqr.chocolatequestrepoured.objects.entity.bases.AbstractEntityCQR;
import com.teamcqr.chocolatequestrepoured.objects.entity.mobs.EntityCQRNPC;
import com.teamcqr.chocolatequestrepoured.util.PropertyFileHelper;
import com.teamcqr.chocolatequestrepoured.util.data.FileIOUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;

public class FactionRegistry {

	private static FactionRegistry instance;

	private Map<String, CQRFaction> factions = new HashMap<>();
	private volatile List<UUID> uuidsBeingLoaded = Collections.synchronizedList(new ArrayList<UUID>());
	private Map<UUID, Map<String, Integer>> playerFactionRepuMap = new ConcurrentHashMap<>();
	private Map<ResourceLocation, CQRFaction> entityFactionMap = new ConcurrentHashMap<>();

	public static final int LOWEST_REPU = EReputationState.ARCH_ENEMY.getValue();
	public static final int HIGHEST_REPU = EReputationState.MEMBER.getValue();

	public FactionRegistry() {
		instance = this;
	}

	public void loadFactions() {
		this.loadFactionsInConfigFolder();
		this.loadDefaultFactions();
		
		this.loadEntityFactionRelations();
	}

	private void loadEntityFactionRelations() {
		File file = new File(CQRMain.CQ_CONFIG_FOLDER, "entityFactionRelation.properties");
		if(file.exists()) {
			Properties prop = new Properties();
			boolean flag = true;
			try (InputStream inputStream = new FileInputStream(file)) {
				prop.load(inputStream);
				flag = true;
			} catch (IOException e) {
				CQRMain.logger.error("Failed to load file" + file.getName(), e);
				flag = false;
			}
			if(flag) {
				for(String key : prop.stringPropertyNames()) {
					if(key.startsWith("#")) {
						continue;
					}
					String rlkey = key.replace('.', ':');
					ResourceLocation resLoc = new ResourceLocation(rlkey);
					//if(EntityList.isRegistered(resLoc)) {
						String faction = prop.getProperty(key, null);
						if(faction != null && factions.containsKey(faction)) {
							entityFactionMap.put(resLoc, factions.get(faction));
						}
					//}
				}
			}
		}
	}

	private void loadFactionsInConfigFolder() {
		// DONE: Load factions from files
		List<File> files = new ArrayList<>(FileUtils.listFiles(CQRMain.CQ_FACTION_FOLDER, new String[] {"cfg", "prop", "properties"}, true));
		int fileCount = files.size();
		if (fileCount > 0) {
			ArrayList<String> fIDs = new ArrayList<>(fileCount +1);
			ArrayList<List<String>> allyTmp = new ArrayList<>();
			ArrayList<List<String>> enemyTmp = new ArrayList<>();
			boolean flag = true;
			for (int i = 0; i < fileCount; i++) {
				File file = files.get(i);
				Properties prop = new Properties();
				try (InputStream inputStream = new FileInputStream(file)) {
					prop.load(inputStream);
					flag = true;
				} catch (IOException e) {
					CQRMain.logger.error("Failed to load file" + file.getName(), e);
					flag = false;
					continue;
				}
				if (flag) {
					List<String> fAlly = new ArrayList<>();
					List<String> fEnemy = new ArrayList<>();
					// CQRFaction fTmp =
					String fName = prop.getProperty(ConfigKeys.FACTION_NAME_KEY, "FACTION_NAME");
					int repuChangeAlly = PropertyFileHelper.getIntProperty(prop, ConfigKeys.FACTION_REPU_CHANGE_KILL_ALLY, 2);
					int repuChangeEnemy = PropertyFileHelper.getIntProperty(prop, ConfigKeys.FACTION_REPU_CHANGE_KILL_ENEMY, 1);
					int repuChangeMember = PropertyFileHelper.getIntProperty(prop, ConfigKeys.FACTION_REPU_CHANGE_KILL_MEMBER, 5);
					EReputationState defRepu = EReputationState.valueOf(prop.getProperty(ConfigKeys.FACTION_REPU_DEFAULT, EReputationState.NEUTRAL.toString()));
					boolean staticRepu = PropertyFileHelper.getBooleanProperty(prop, ConfigKeys.FACTION_STATIC_REPUTATION_KEY, false);
					// Reputation lists
					for(String ally : PropertyFileHelper.getStringArrayProperty(prop, ConfigKeys.FACTION_ALLIES_KEY, new String[] {})) {
						fAlly.add(ally);
					}
					for(String enemy : PropertyFileHelper.getStringArrayProperty(prop, ConfigKeys.FACTION_ENEMIES_KEY, new String[] {})) {
						fEnemy.add(enemy);
					}
					fIDs.add(fName);
					allyTmp.add(fAlly);
					enemyTmp.add(fEnemy);

					Optional<Integer> optionMember = Optional.of(repuChangeMember);
					Optional<Integer> optionAlly = Optional.of(repuChangeAlly);
					Optional<Integer> optionEnemy = Optional.of(repuChangeEnemy);

					CQRFaction f = new CQRFaction(fName, defRepu, !staticRepu, optionMember, optionAlly, optionEnemy);
					this.factions.put(fName, f);
				}
			}
			for (int i = 0; i < fIDs.size(); i++) {
				String name = fIDs.get(i);
				CQRFaction fac = this.factions.get(name);
				for (String s : allyTmp.get(i)) {
					fac.addAlly(this.factions.get(s));
				}
				for (String s : enemyTmp.get(i)) {
					fac.addEnemy(this.factions.get(s));
				}
			}
		}
	}

	private void loadDefaultFactions() {
		String[][] allies = new String[EDefaultFaction.values().length][];
		String[][] enemies = new String[EDefaultFaction.values().length][];
		List<Integer> indices = new ArrayList<>();
		for (int i = 0; i < EDefaultFaction.values().length; i++) {
			EDefaultFaction edf = EDefaultFaction.values()[i];
			if (this.factions.containsKey(edf.name())) {
				continue;
			}
			indices.add(i);
			allies[i] = edf.getAllies();
			enemies[i] = edf.getEnemies();

			Optional<Integer> optionMember = Optional.empty();
			Optional<Integer> optionAlly = Optional.empty();
			Optional<Integer> optionEnemy = Optional.empty();

			CQRFaction fac = new CQRFaction(edf.name(), edf.getDefaultReputation(), false, edf.canRepuChange(), optionMember, optionAlly, optionEnemy);
			this.factions.put(edf.name(), fac);
		}

		for (int i : indices) {
			String name = EDefaultFaction.values()[i].name();
			CQRFaction fac = this.factions.get(name);
			for (int j = 0; j < allies[i].length; j++) {
				fac.addAlly(this.factions.get(allies[i][j]));
			}
			for (int j = 0; j < enemies[i].length; j++) {
				fac.addEnemy(this.factions.get(enemies[i][j]));
			}
		}

		CQRMain.logger.info("Default factions loaded and initialized!");
	}

	@Nullable
	public CQRFaction getFactionOf(Entity entity) {
		if(entity == null) {
			return null;
		}
		if (entity instanceof MultiPartEntityPart) {
			return this.getFactionOf((Entity) ((MultiPartEntityPart)entity).parent);
		}
		if (entity.getControllingPassenger() != null) {
			return this.getFactionOf(entity.getControllingPassenger());
		}
		if (entity instanceof TameableEntity && ((TameableEntity) entity).getOwner() != null) {
			return this.getFactionOf(((TameableEntity) entity).getOwner());
		} 
		
		if (entity instanceof AbstractEntityCQR) {
			return ((AbstractEntityCQR) entity).getFaction();
		}
		
		//Faction overriding
		if(EntityList.getKey(entity) != null && entityFactionMap.containsKey(EntityList.getKey(entity))) {
			return entityFactionMap.get(EntityList.getKey(entity));
		}
		//Overriding end
		
		if (entity instanceof ArmorStandEntity) {
			return this.factions.get(EDefaultFaction.ALL_ALLY.name());
		}

		if (entity instanceof VillagerEntity || entity instanceof GolemEntity || entity instanceof EntityCQRNPC) {
			return this.factions.get(EDefaultFaction.VILLAGERS.name());
		}

		if (entity instanceof AbstractIllagerEntity || entity instanceof VexEntity) {
			return this.factions.get(EDefaultFaction.ILLAGERS.name());
		}

		if (entity instanceof EndermanEntity || entity instanceof EndermiteEntity || entity instanceof EnderDragonEntity) {
			return this.factions.get(EDefaultFaction.ENDERMEN.name());
		}

		if (entity instanceof AnimalEntity) {
			return this.factions.get(EDefaultFaction.NEUTRAL.name());
		}

		if (entity instanceof MobEntity) {
			return this.factions.get(EDefaultFaction.UNDEAD.name());
		}

		if (entity instanceof WaterMobEntity) {
			return this.factions.get(EDefaultFaction.TRITONS.name());
		}

		return null;
	}

	public static FactionRegistry instance() {
		if (instance == null) {
			instance = new FactionRegistry();
		}
		return instance;
	}

	public CQRFaction getFactionInstance(String factionName) {
		if (this.factions.containsKey(factionName)) {
			return this.factions.get(factionName);
		}
		return this.factions.get(EDefaultFaction.UNDEAD.name());
	}

	public EReputationStateRough getReputationOf(UUID playerID, CQRFaction faction) {
		return EReputationStateRough.getByRepuScore(getExactReputationOf(playerID, faction));
	}
	
	public int getExactReputationOf(UUID playerID, CQRFaction faction) {
		if (faction.isRepuStatic()) {
			return faction.getDefaultReputation().getValue();
		}
		if (playerID != null && this.playerFactionRepuMap.containsKey(playerID)) {
			if (this.playerFactionRepuMap.get(playerID).containsKey(faction.getName())) {
				return this.playerFactionRepuMap.get(playerID).get(faction.getName());
			}
		}
		return faction.getDefaultReputation().getValue();
	}

	public void incrementRepuOf(PlayerEntity player, String faction, int score) {
		this.changeRepuOf(player, faction, Math.abs(score));
	}

	public void decrementRepuOf(PlayerEntity player, String faction, int score) {
		this.changeRepuOf(player, faction, -Math.abs(score));
	}

	public void changeRepuOf(PlayerEntity player, String faction, int score) {
		boolean flag = false;
		if (this.canRepuChange(player)) {
			if (score < 0) {
				flag = this.canDecrementRepu(player, faction);
			} else {
				flag = this.canIncrementRepu(player, faction);
			}
		}
		if (flag) {
			Map<String, Integer> factionsOfPlayer = this.playerFactionRepuMap.getOrDefault(player.getUniqueID(), new ConcurrentHashMap<>());
			int oldScore = factionsOfPlayer.getOrDefault(faction, this.factions.get(faction).getDefaultReputation().getValue());
			factionsOfPlayer.put(faction, oldScore + score);
			this.playerFactionRepuMap.put(player.getUniqueID(), factionsOfPlayer);
			CQRMain.logger.info("Repu changed!");
		}
	}

	public boolean canDecrementRepu(PlayerEntity player, String faction) {
		if (this.canRepuChange(player)) {
			Map<String, Integer> factionsOfPlayer = this.playerFactionRepuMap.getOrDefault(player.getUniqueID(), new ConcurrentHashMap<>());
			if (factionsOfPlayer != null) {
				if (factionsOfPlayer.containsKey(faction)) {
					return factionsOfPlayer.get(faction) >= LOWEST_REPU;
				} else {
					return true;
				}
			} else {
				return true;
			}
		}
		return false;
	}

	public boolean canIncrementRepu(PlayerEntity player, String faction) {
		if (this.canRepuChange(player)) {
			Map<String, Integer> factionsOfPlayer = this.playerFactionRepuMap.getOrDefault(player.getUniqueID(), new ConcurrentHashMap<>());
			if (factionsOfPlayer != null) {
				if (factionsOfPlayer.containsKey(faction)) {
					return factionsOfPlayer.get(faction) <= HIGHEST_REPU;
				} else {
					return true;
				}
			} else {
				return true;
			}
		}
		return false;
	}

	private boolean canRepuChange(PlayerEntity player) {
		return !(player.getEntityWorld().getDifficulty().equals(Difficulty.PEACEFUL) || player.isCreative() || player.isSpectator() || this.uuidsBeingLoaded.contains(player.getUniqueID()));
	}

	public void handlePlayerLogin(PlayerLoggedInEvent event) {
		String path = FileIOUtil.getAbsoluteWorldPath() + "/data/CQR/reputation/";
		File f = new File(path, event.player.getUniqueID() + ".nbt");
		if (f.exists()) {
			CQRMain.logger.info("Loading player reputation...");
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					final UUID uuid = event.player.getUniqueID();
					CompoundNBT root = FileIOUtil.getRootNBTTagOfFile(f);
					NBTTagList repuDataList = FileIOUtil.getOrCreateTagList(root, "reputationdata", Constants.NBT.TAG_COMPOUND);
					if (!repuDataList.hasNoTags()) {
						while(FactionRegistry.this.uuidsBeingLoaded.contains(uuid)) {
							//Wait until the uuid isnt active	
						}
						FactionRegistry.this.uuidsBeingLoaded.add(uuid);
						try {
							Map<String, Integer> mapping = FactionRegistry.this.playerFactionRepuMap.get(event.player.getUniqueID());
							repuDataList.forEach(new Consumer<NBTBase>() {

								@Override
								public void accept(NBTBase t) {
									CompoundNBT tag = (CompoundNBT) t;
									String fac = tag.getString("factionName");
									if (FactionRegistry.this.factions.containsKey(fac)) {
										int reputation = tag.getInteger("reputation");
										mapping.put(fac, reputation);
									}
								}
							});
						} finally {
							FactionRegistry.this.uuidsBeingLoaded.remove(uuid);
						}
					}
				}
			});
			t.setDaemon(true);
			t.start();
		}
	}

	public void handlePlayerLogout(PlayerLoggedOutEvent event) {
		if (this.playerFactionRepuMap.containsKey(event.player.getUniqueID())) {
			CQRMain.logger.info("Saving player reputation...");
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					Map<String, Integer> mapping = FactionRegistry.this.playerFactionRepuMap.get(event.player.getUniqueID());
					Map<String, Integer> entryMapping = new HashMap<>();
					final UUID uuid = event.player.getUniqueID();
					String path = FileIOUtil.getAbsoluteWorldPath() + "/data/CQR/reputation/";
					File f = FileIOUtil.getOrCreateFile(path, uuid + ".nbt");
					if (f != null) {
						while(FactionRegistry.this.uuidsBeingLoaded.contains(uuid)) {
							//Wait until the uuid isnt active	
						}
						FactionRegistry.this.uuidsBeingLoaded.add(uuid);
						try {
							CompoundNBT root = FileIOUtil.getRootNBTTagOfFile(f);
							NBTTagList repuDataList = FileIOUtil.getOrCreateTagList(root, "reputationdata", Constants.NBT.TAG_COMPOUND);
							for (int i = 0; i < repuDataList.tagCount(); i++) {
								CompoundNBT tag = repuDataList.getCompoundTagAt(i);
								if (mapping.containsKey(tag.getString("factionName"))) {
									entryMapping.put(tag.getString("factionName"), i);
								}
							}
							for (Map.Entry<String, Integer> entry : mapping.entrySet()) {
								if (entryMapping.containsKey(entry.getKey())) {
									repuDataList.removeTag(entryMapping.get(entry.getKey()));
								}
								CompoundNBT tag = new CompoundNBT();
								tag.setString("factionName", entry.getKey());
								tag.setInteger("reputation", entry.getValue());
								repuDataList.appendTag(tag);
							}
							root.removeTag("reputationdata");
							root.setTag("reputationdata", repuDataList);

							FileIOUtil.saveNBTCompoundToFile(root, f);
						} finally {
							FactionRegistry.this.uuidsBeingLoaded.remove(uuid);
						}
					}
				}
			});
			t.setDaemon(true);
			t.start();
		}
	}

	public List<CQRFaction> getLoadedFactions() {
		return new ArrayList<CQRFaction>(factions.values());
	}

}
