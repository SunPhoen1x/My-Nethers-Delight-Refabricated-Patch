package com.phoen1x.mynethersdelight.mixin;

import com.soytutta.mynethersdelight.common.registry.MNDEffects;
import eu.pb4.polymer.core.api.other.PolymerStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MNDEffects.class)
public class MNDEffectsMixin {

    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/registry/Registry;registerReference(Lnet/minecraft/registry/Registry;Lnet/minecraft/util/Identifier;Ljava/lang/Object;)Lnet/minecraft/registry/entry/RegistryEntry$Reference;"
            )
    )
    private static RegistryEntry.Reference<StatusEffect> polymerifyEffect(Registry<StatusEffect> registry, Identifier id, Object entry) {
        StatusEffect effect = (StatusEffect) entry;
        PolymerStatusEffect.registerOverlay(effect);
        return Registry.registerReference(registry, id, effect);
    }
}