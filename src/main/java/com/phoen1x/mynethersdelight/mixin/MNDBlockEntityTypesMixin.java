package com.phoen1x.mynethersdelight.mixin;

import com.soytutta.mynethersdelight.common.registry.MNDBlockEntityTypes;
import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import eu.pb4.polymer.core.api.utils.PolymerSyncedObject;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MNDBlockEntityTypes.class, remap = false)
public class MNDBlockEntityTypesMixin {

    @Inject(
            method = "register",
            at = @At("RETURN")
    )
    private static <T extends BlockEntityType<?>> void polymerifyBlockEntity(String name, T type, CallbackInfoReturnable<T> cir) {
        T registeredType = cir.getReturnValue();
        if (registeredType != null) {
            if (name.contains("hanging_sign")) {
                PolymerSyncedObject.setSyncedObject(Registries.BLOCK_ENTITY_TYPE, registeredType, (a, b) -> BlockEntityType.HANGING_SIGN);
            } else if (name.contains("sign")) {
                PolymerSyncedObject.setSyncedObject(Registries.BLOCK_ENTITY_TYPE, registeredType, (a, b) -> BlockEntityType.SIGN);
            }
            PolymerBlockUtils.registerBlockEntity(registeredType);
        }
    }
}