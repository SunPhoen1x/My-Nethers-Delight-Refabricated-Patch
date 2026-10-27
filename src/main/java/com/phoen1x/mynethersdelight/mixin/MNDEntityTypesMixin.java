package com.phoen1x.mynethersdelight.mixin;

import com.phoen1x.mynethersdelight.impl.entity.PolymerStriderRock;
import com.soytutta.mynethersdelight.common.entity.StriderRockEntity;
import com.soytutta.mynethersdelight.common.registry.MNDEntityTypes;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MNDEntityTypes.class, remap = false)
public class MNDEntityTypesMixin {

    @Inject(method = "register", at = @At("RETURN"))
    private static <T extends Entity> void polymerifyEntity(String string, EntityType.Builder<T> builder, CallbackInfoReturnable<EntityType<T>> cir) {
        if ("strider_rock".equals(string)) {
            EntityType<T> type = cir.getReturnValue();
            PolymerEntityUtils.registerType(type);
            PolymerEntityUtils.registerOverlay(type, entity -> new PolymerStriderRock((StriderRockEntity) entity));
        }
    }
}