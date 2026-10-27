package com.phoen1x.mynethersdelight.impl.entity;

import com.soytutta.mynethersdelight.common.entity.StriderRockEntity;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import net.minecraft.entity.EntityType;
import xyz.nucleoid.packettweaker.PacketContext;

public record PolymerStriderRock(StriderRockEntity entity) implements PolymerEntity {

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.SNOWBALL;
    }
}