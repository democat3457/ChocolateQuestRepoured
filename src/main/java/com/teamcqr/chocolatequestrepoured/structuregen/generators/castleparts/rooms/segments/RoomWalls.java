package com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.segments;

import java.util.EnumMap;
import java.util.Random;

import net.minecraft.util.Direction;

public class RoomWalls {
	// The wall settings for this room
	private EnumMap<Direction, WallOptions> walls;

	// A map of adjacent room doors that lead into this room
	private EnumMap<Direction, DoorPlacement> adjacentDoors;

	public RoomWalls() {
		this.walls = new EnumMap<>(Direction.class);
		this.adjacentDoors = new EnumMap<>(Direction.class);
	}

	public void addOuter(Direction side) {
		this.walls.put(side, new WallOptions(true));
	}

	public void addInner(Direction side) {
		this.walls.put(side, new WallOptions(false));
	}

	public DoorPlacement addCenteredDoor(Random random, int wallLength, Direction side, EnumCastleDoorType type) {

		if (type == EnumCastleDoorType.RANDOM) {
			type = EnumCastleDoorType.getRandomRegularType(random);
		}

		int offset = (wallLength - type.getWidth()) / 2;

		return this.addDoorWithOffset(side, offset, type);
	}

	public DoorPlacement addRandomDoor(Random random, int wallLength, Direction side, EnumCastleDoorType type) {
		if (type == EnumCastleDoorType.RANDOM) {
			type = EnumCastleDoorType.getRandomRegularType(random);
		}

		int offset = 1 + random.nextInt(wallLength - type.getWidth() - 1);

		return this.addDoorWithOffset(side, offset, type);
	}

	private DoorPlacement addDoorWithOffset(Direction side, int offset, EnumCastleDoorType type) {
		if (this.walls.containsKey(side)) {
			DoorPlacement door = new DoorPlacement(offset, type);
			this.walls.get(side).addDoor(door);
			return door;
		} else {
			return null;
		}
	}

	public boolean hasWallOnSide(Direction side) {
		return this.walls.containsKey(side);
	}

	public boolean hasDoorOnSide(Direction side) {
		if (this.walls.containsKey(side)) {
			return this.walls.get(side).hasDoor();
		} else {
			return false;
		}
	}

	public DoorPlacement getDoorOnSide(Direction side) {
		if (this.walls.containsKey(side) && this.walls.get(side).hasDoor()) {
			return this.walls.get(side).getDoor();
		} else {
			return null;
		}
	}

	public WallOptions getOptionsForSide(Direction side) {
		return this.walls.get(side);
	}

	public void removeWall(Direction side) {
		this.walls.remove(side);
	}

	public void registerAdjacentDoor(Direction side, DoorPlacement door) {
		this.adjacentDoors.put(side, door);
	}

	public boolean adjacentRoomHasDoorOnSide(Direction side) {
		return this.adjacentDoors.containsKey(side);
	}

	public DoorPlacement getAdjacentDoor(Direction side) {
		return this.adjacentDoors.get(side);
	}
}
