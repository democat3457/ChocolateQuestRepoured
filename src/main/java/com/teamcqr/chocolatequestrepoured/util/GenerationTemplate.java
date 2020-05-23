package com.teamcqr.chocolatequestrepoured.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class GenerationTemplate {

    private class GenerationRule {
        public Predicate<Vec3i> condition;
        public BlockState block;

        public GenerationRule(Predicate<Vec3i> condition, BlockState blockToBuild)
        {
            this.condition = condition;
            this.block = blockToBuild;
        }

        public Predicate<Vec3i> getCondition() {
            return condition;
        }

        public BlockState getBlock() {
            return block;
        }
    }

    private List<GenerationRule> generationRules;
    private int lengthX;
    private int lengthY;
    private int lengthZ;

    public GenerationTemplate(int lengthX, int lengthY, int lengthZ) {
        this.generationRules = new ArrayList<>();
        this.lengthX = lengthX;
        this.lengthY = lengthY;
        this.lengthZ = lengthZ;
    }

    public GenerationTemplate(Vec3i dimensions) {
        this.generationRules = new ArrayList<>();
        this.lengthX = dimensions.getX();
        this.lengthY = dimensions.getY();
        this.lengthZ = dimensions.getZ();
    }

    public void addRule(Predicate<Vec3i> condition, BlockState blockToBuild) {
        generationRules.add(new GenerationRule(condition, blockToBuild));
    }

    public HashMap<BlockPos, BlockState> GetGenerationMap(BlockPos origin, boolean fillUnusedWithAir) {
        HashMap<BlockPos, BlockState> result = new HashMap<>();

        for (int x = 0; x < lengthX; x++) {
            for (int z = 0; z < lengthZ; z++) {
                for (int y = 0; y < lengthY; y++) {
                    boolean foundRule = false;

                    Vec3i offset = new Vec3i(x, y, z);
                    for (GenerationRule rule : generationRules)
                    {
                        if (rule.getCondition().test(offset))
                        {
                            result.put(origin.add(offset), rule.getBlock());
                            foundRule = true;
                            break; //No need to test other rules
                        }
                    }

                    if (!foundRule && fillUnusedWithAir)
                    {
                        result.put(origin.add(offset), Blocks.AIR.getDefaultState());
                    }
                }
            }
        }

        return result;
    }

    public ArrayList<Map.Entry<BlockPos, BlockState>> GetGenerationList(BlockPos origin, boolean fillUnusedWithAir) {
        return new ArrayList<>(GetGenerationMap(origin, fillUnusedWithAir).entrySet());
    }
}
