package com.phoen1x.mynethersdelight.mixin;

import com.soytutta.mynethersdelight.common.registry.MNDCreativeTab;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MNDCreativeTab.class)
public class MNDCreativeTabMixin {

    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/registry/Registry;register(Lnet/minecraft/registry/Registry;Lnet/minecraft/registry/RegistryKey;Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private static Object polymerifyGroup(Registry<ItemGroup> registry, RegistryKey<ItemGroup> key, Object entry) {
        ItemGroup group = (ItemGroup) entry;
        PolymerItemGroupUtils.registerPolymerItemGroup(key.getValue(), group);
        return Registry.register(registry, key, group);
    }
}