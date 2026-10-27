package com.phoen1x.mynethersdelight.mixin;

import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HangingSignBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SignBlockEntity.class)
public abstract class MNDSignBlockEntityMixin extends BlockEntity {

    public MNDSignBlockEntityMixin(BlockEntityType<?> type, net.minecraft.util.math.BlockPos pos, net.minecraft.block.BlockState state) {
        super(type, pos, state);
    }

    @Inject(
            method = "toUpdatePacket()Lnet/minecraft/network/packet/s2c/play/BlockEntityUpdateS2CPacket;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void fixMndSignUpdatePacket(CallbackInfoReturnable<BlockEntityUpdateS2CPacket> cir) {
        Identifier id = Registries.BLOCK_ENTITY_TYPE.getId(this.getType());

        if (id != null && id.getNamespace().equals("mynethersdelight")) {
            boolean isHanging = ((Object) this instanceof HangingSignBlockEntity) || id.getPath().contains("hanging");
            BlockEntityType<?> clientType = isHanging ? BlockEntityType.HANGING_SIGN : BlockEntityType.SIGN;

            if (this.getWorld() != null) {
                NbtCompound nbt = ((SignBlockEntity) (Object) this).toInitialChunkDataNbt(this.getWorld().getRegistryManager());

                cir.setReturnValue((BlockEntityUpdateS2CPacket) PolymerBlockUtils.createBlockEntityPacket(
                        this.pos,
                        clientType,
                        nbt
                ));
            }
        }
    }
}