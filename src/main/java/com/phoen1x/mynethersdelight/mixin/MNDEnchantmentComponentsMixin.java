package com.phoen1x.mynethersdelight.mixin;

import com.soytutta.mynethersdelight.common.registry.MNDEnchantmentComponents;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MNDEnchantmentComponents.class)
public class MNDEnchantmentComponentsMixin {

    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/registry/Registry;register(Lnet/minecraft/registry/Registry;Lnet/minecraft/util/Identifier;Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private static Object polymerifyEnchantmentComponent(Registry<ComponentType<?>> registry, Identifier id, Object entry) {
        ComponentType<?> componentType = (ComponentType<?>) entry;
        PolymerComponent.registerEnchantmentEffectComponent(componentType);
        return Registry.register(registry, id, componentType);
    }
}