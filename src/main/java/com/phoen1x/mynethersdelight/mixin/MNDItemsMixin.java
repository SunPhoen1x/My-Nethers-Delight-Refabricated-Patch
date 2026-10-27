package com.phoen1x.mynethersdelight.mixin;

import com.phoen1x.mynethersdelight.impl.item.PolyBaseItem;
import com.soytutta.mynethersdelight.common.registry.MNDItems;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerSyncedObject;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MNDItems.class, remap = false)
public class MNDItemsMixin {

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void polymerifyMNDItems(CallbackInfo ci) {
        applyToAllModItems();
    }

    @Unique
    private static void applyToAllModItems() {
        Registries.ITEM.getIds().forEach(id -> {
            if (id.getNamespace().equals("mynethersdelight")) {
                Item item = Registries.ITEM.get(id);
                if (PolymerSyncedObject.getSyncedObject(Registries.ITEM, item) == null) {
                    PolymerItem.registerOverlay(item, new PolyBaseItem(item));
                }
            }
        });
    }
}