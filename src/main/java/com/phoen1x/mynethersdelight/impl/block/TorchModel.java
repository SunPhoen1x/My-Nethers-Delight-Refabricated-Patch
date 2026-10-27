package com.phoen1x.mynethersdelight.impl.block;

import eu.pb4.factorytools.api.block.model.generic.BlockStateModel;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

public class TorchModel extends BlockStateModel {
    protected final Random randomSource = Random.create();

    public TorchModel(BlockState state, BlockPos pos) {
        super(state, pos, 100.0F);
    }

    @Override
    protected void onTick() {
        var blockPos = this.blockPos();

        if (this.randomSource.nextInt(16) == 0) {
            double x = (double) blockPos.getX() + 0.5;
            double y = (double) blockPos.getY() + 0.7;
            double z = (double) blockPos.getZ() + 0.5;
            addParticle(ParticleTypes.FLAME, false, x, y, z, 0.0, 0.0, 0.0);
        }
    }

    protected void addParticle(ParticleEffect type, boolean alwaysVisible, double x, double y, double z, double dx, double dy, double dz) {
        if (this.getAttachment() != null) {
            this.getAttachment().getWorld().spawnParticles(type, false, alwaysVisible, x, y, z, 0, dx, dy, dz, 1);
        }
    }

    public static class Wall extends TorchModel {
        public Wall(BlockState state, BlockPos pos) {
            super(state, pos);
        }

        @Override
        protected void onTick() {
            var blockPos = this.blockPos();
            var blockState = this.blockState();

            if (this.randomSource.nextInt(16) == 0) {
                Direction direction = blockState.get(WallTorchBlock.FACING);
                Direction opposite = direction.getOpposite();

                double x = (double) blockPos.getX() + 0.5 + 0.27 * (double) opposite.getOffsetX();
                double y = (double) blockPos.getY() + 0.7 + 0.22;
                double z = (double) blockPos.getZ() + 0.5 + 0.27 * (double) opposite.getOffsetZ();

                addParticle(ParticleTypes.FLAME, false, x, y, z, 0.0, 0.0, 0.0);
            }
        }
    }
}