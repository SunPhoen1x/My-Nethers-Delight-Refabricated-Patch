package com.phoen1x.mynethersdelight.impl.block;

import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.block.model.generic.BSMMParticleBlock;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModel;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.function.BiFunction;

public record CampfireFactoryBlock(boolean tick, BiFunction<BlockState, BlockPos, BlockModel> modelFunction) implements FactoryBlock, PolymerTexturedBlock, BSMMParticleBlock {
    public static final CampfireFactoryBlock INSTANCE = new CampfireFactoryBlock(true, Model::new);

    private static final BlockState REGULAR = PolymerBlockResourceUtils.requestEmpty(BlockModelType.CAMPFIRE);
    private static final BlockState REGULAR_WATERLOGGED = PolymerBlockResourceUtils.requestEmpty(BlockModelType.CAMPFIRE_WATERLOGGED);

    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
        boolean isWaterlogged = state.contains(CampfireBlock.WATERLOGGED) && state.get(CampfireBlock.WATERLOGGED);
        return isWaterlogged ? REGULAR_WATERLOGGED : REGULAR;
    }

    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return this.modelFunction.apply(initialBlockState, pos);
    }

    @Override
    public boolean tickElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return this.tick;
    }

    public CampfireFactoryBlock withModel(BiFunction<BlockState, BlockPos, BlockModel> modelFunction) {
        return new CampfireFactoryBlock(this.tick, modelFunction);
    }

    public CampfireFactoryBlock withTick(boolean tick) {
        return new CampfireFactoryBlock(tick, this.modelFunction);
    }

    @Override
    public boolean isIgnoringBlockInteractionPlaySoundExceptedEntity(BlockState state, ServerPlayerEntity player, Hand hand, ItemStack stack, ServerWorld world, BlockHitResult blockHitResult) {
        return true;
    }

    public static class Model extends BlockStateModel {
        private final Random randomSource = Random.create();

        public Model(BlockState state, BlockPos pos) {
            super(state, pos, 100.0F);
        }

        @Override
        protected void onTick() {
            var state = this.blockState();
            var blockPos = this.blockPos();

            boolean isLit = !state.contains(CampfireBlock.LIT) || state.get(CampfireBlock.LIT);

            if (isLit) {
                if (randomSource.nextFloat() < 0.11F) {
                    boolean isSignal = state.contains(CampfireBlock.SIGNAL_FIRE) && state.get(CampfireBlock.SIGNAL_FIRE);
                    var smoke = isSignal ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;

                    for (int i = 0; i < randomSource.nextInt(2) + 2; ++i) {
                        double offsetX = (randomSource.nextDouble() / 3.0) * (randomSource.nextBoolean() ? 1 : -1);
                        double offsetZ = (randomSource.nextDouble() / 3.0) * (randomSource.nextBoolean() ? 1 : -1);
                        addParticle(
                                smoke,
                                true,
                                (double) blockPos.getX() + 0.5 + offsetX,
                                (double) blockPos.getY() + randomSource.nextDouble() + randomSource.nextDouble(),
                                (double) blockPos.getZ() + 0.5 + offsetZ,
                                0.0,
                                0.07,
                                0.0
                        );
                    }
                }

                if (randomSource.nextInt(64) == 0) {
                    if (randomSource.nextInt(10) == 0) {
                        playLocalSound(
                                (double) blockPos.getX() + 0.5,
                                (double) blockPos.getY() + 0.5,
                                (double) blockPos.getZ() + 0.5,
                                SoundEvents.BLOCK_CAMPFIRE_CRACKLE,
                                SoundCategory.BLOCKS,
                                0.5F + randomSource.nextFloat(),
                                randomSource.nextFloat() * 0.7F + 0.6F,
                                false
                        );
                    }

                    if (randomSource.nextInt(5) == 0) {
                        for (int i = 0; i < randomSource.nextInt(1) + 1; ++i) {
                            addParticle(
                                    ParticleTypes.FLAME,
                                    false,
                                    (double) blockPos.getX() + 0.5,
                                    (double) blockPos.getY() + 0.5,
                                    (double) blockPos.getZ() + 0.5,
                                    randomSource.nextFloat() / 2.0F,
                                    5.0E-5,
                                    randomSource.nextFloat() / 2.0F
                            );
                        }
                    }
                }
            }

            super.onTick();
        }

        private void addParticle(ParticleEffect type, boolean alwaysVisible, double x, double y, double z, double dx, double dy, double dz) {
            if (this.getAttachment() != null) {
                this.getAttachment().getWorld().spawnParticles(type, false, alwaysVisible, x, y, z, 0, dx, dy, dz, 1);
            }
        }

        private void playLocalSound(double x, double y, double z, SoundEvent soundEvent, SoundCategory soundSource, float volume, float pitch, boolean force) {
            if (this.getAttachment() != null) {
                this.getAttachment().getWorld().playSound(null, x, y, z, soundEvent, soundSource, volume, pitch);
            }
        }
    }
}