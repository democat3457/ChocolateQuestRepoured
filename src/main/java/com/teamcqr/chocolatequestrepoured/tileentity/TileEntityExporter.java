package com.teamcqr.chocolatequestrepoured.tileentity;

import java.io.File;

import javax.annotation.Nullable;

import com.teamcqr.chocolatequestrepoured.CQRMain;
import com.teamcqr.chocolatequestrepoured.client.gui.GuiExporter;
import com.teamcqr.chocolatequestrepoured.network.packets.toServer.SaveStructureRequestPacket;
import com.teamcqr.chocolatequestrepoured.structuregen.structurefile.CQStructure;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class TileEntityExporter extends TileEntity {

	public int startX = 0;
	public int startY = 0;
	public int startZ = 0;
	public int endX = 0;
	public int endY = 0;
	public int endZ = 0;
	public String structureName = "NoName";
	public boolean relativeMode = false;
	public boolean ignoreEntities = true;

	private BlockPos minPos = new BlockPos(0, 0, 0);
	private BlockPos maxPos = new BlockPos(0, 0, 0);

	private EntityPlayer user = null;

	public NBTTagCompound getExporterData(NBTTagCompound compound) {
		compound.setInteger("StartX", this.startX);
		compound.setInteger("StartY", this.startY);
		compound.setInteger("StartZ", this.startZ);
		compound.setInteger("EndX", this.endX);
		compound.setInteger("EndY", this.endY);
		compound.setInteger("EndZ", this.endZ);
		compound.setString("StructureName", this.structureName);
		compound.setBoolean("RelativeMode", this.relativeMode);
		compound.setBoolean("IgnoreEntities", this.ignoreEntities);
		return compound;
	}

	public void setExporterData(NBTTagCompound compound) {
		this.startX = compound.getInteger("StartX");
		this.startY = compound.getInteger("StartY");
		this.startZ = compound.getInteger("StartZ");
		this.endX = compound.getInteger("EndX");
		this.endY = compound.getInteger("EndY");
		this.endZ = compound.getInteger("EndZ");
		this.structureName = compound.getString("StructureName");
		this.relativeMode = compound.getBoolean("RelativeMode");
		this.ignoreEntities = compound.getBoolean("IgnoreEntities");

		this.onPositionsChanged();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		this.getExporterData(compound);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.setExporterData(compound);
	}

	public void setValues(BlockPos startPos, BlockPos endPos, String structName, boolean useRelativeMode, boolean useSmartMode) {
		this.setValues(startPos.getX(), startPos.getY(), startPos.getZ(), endPos.getX(), endPos.getY(), endPos.getZ(), structName, useRelativeMode, useSmartMode);
	}

	public void setValues(int sX, int sY, int sZ, int eX, int eY, int eZ, String structName, boolean useRelativeMode, boolean useSmartMode) {
		this.startX = sX;
		this.startY = sY;
		this.startZ = sZ;
		this.endX = eX;
		this.endY = eY;
		this.endZ = eZ;
		this.structureName = structName;
		this.relativeMode = useRelativeMode;
		this.ignoreEntities = useSmartMode;

		this.onPositionsChanged();

		this.markDirty();
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, 1, this.getExporterData(new NBTTagCompound()));
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.setExporterData(pkt.getNbtCompound());

		GuiScreen screen = Minecraft.getMinecraft().currentScreen;
		if (screen instanceof GuiExporter) {
			((GuiExporter) screen).sync();
		}
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound data = super.getUpdateTag();
		data.setTag("data", this.getExporterData(new NBTTagCompound()));
		return data;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
		this.setExporterData(tag.getCompoundTag("data"));
	}

	public void setUser(EntityPlayer player) {
		this.user = player;
	}

	public void saveStructure(World world, BlockPos startPos, BlockPos endPos, EntityPlayer author) {
		if (this.relativeMode) {
			startPos = this.pos.add(startPos);
			endPos = this.pos.add(endPos);
		}
		if (!world.isRemote) {
			CQRMain.logger.info("Server is saving structure...");
			CQStructure structure = CQStructure.createFromWorld(world, startPos, endPos, ignoreEntities, author.getName());
			structure.writeToFile(new File(CQRMain.CQ_EXPORT_FILES_FOLDER, this.structureName + ".nbt"));
			new Thread(() -> {
				structure.writeToFile(new File(CQRMain.CQ_EXPORT_FILES_FOLDER, this.structureName + ".nbt"));
				author.sendMessage(new TextComponentString("Successfully exported structure: " + this.structureName));
			}).start();
		} else {
			CQRMain.logger.info("Sending structure save request packet...");
			CQRMain.NETWORK.sendToServer(new SaveStructureRequestPacket(startPos, endPos, author.getName(), this.structureName, this.ignoreEntities));
		}
	}

	public void onPositionsChanged() {
		this.minPos = new BlockPos(Math.min(this.startX, this.endX), Math.min(this.startY, this.endY), Math.min(this.startZ, this.endZ));
		this.maxPos = new BlockPos(Math.max(this.startX, this.endX), Math.max(this.startY, this.endY), Math.max(this.startZ, this.endZ));
	}

	public BlockPos getMinPos() {
		return this.minPos;
	}

	public BlockPos getMaxPos() {
		return this.maxPos;
	}

	public BlockPos getRenderMinPos() {
		return this.relativeMode ? this.minPos : this.minPos.subtract(this.pos);
	}

	public BlockPos getRenderMaxPos() {
		return this.relativeMode ? this.maxPos : this.maxPos.subtract(this.pos);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public double getMaxRenderDistanceSquared() {
		double d = Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16;
		return d * d;
	}

}
