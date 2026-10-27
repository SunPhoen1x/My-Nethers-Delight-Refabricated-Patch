package com.phoen1x.mynethersdelight.mixin;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "net.minecraft.network.packet.s2c.play.ChunkData$BlockEntityData")
public class MNDBlockEntityDataMixin {
    @ModifyArg(
            method = "of",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/packet/s2c/play/ChunkData$BlockEntityData;<init>(IILnet/minecraft/block/entity/BlockEntityType;Lnet/minecraft/nbt/NbtCompound;)V"
            )
    )
    private static BlockEntityType<?> swapBlockEntityType(BlockEntityType<?> type) {
        Identifier id = Registries.BLOCK_ENTITY_TYPE.getId(type);
        if (id != null && id.getNamespace().equals("mynethersdelight")) {
            if (id.getPath().contains("hanging")) {
                return BlockEntityType.HANGING_SIGN;
            } else if (id.getPath().contains("sign")) {
                return BlockEntityType.SIGN;
            }
        }
        return type;
    }
}