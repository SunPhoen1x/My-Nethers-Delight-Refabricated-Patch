package com.phoen1x.mynethersdelight.mixin;

import com.phoen1x.mynethersdelight.impl.MyNethersDelightPatch;
import com.phoen1x.mynethersdelight.impl.block.*;
import com.soytutta.mynethersdelight.MyNethersDelight;
import com.soytutta.mynethersdelight.common.block.BlazierBlock;
import com.soytutta.mynethersdelight.common.block.NetherStoveBlock;
import com.soytutta.mynethersdelight.common.block.crops.PowderyCaneBlock;
import com.soytutta.mynethersdelight.common.block.crops.PowderyCannonBlock;
import com.soytutta.mynethersdelight.common.block.crops.PowderyCannonSaplingBlock;
import com.soytutta.mynethersdelight.common.block.crops.PowderyFlowerBlock;
import com.soytutta.mynethersdelight.common.block.feasts.BreadLoafBlock;
import com.soytutta.mynethersdelight.common.block.feasts.MagmaCakeBlock;
import com.soytutta.mynethersdelight.common.block.feasts.StriderloafBlock;
import com.soytutta.mynethersdelight.common.registry.MNDBlocks;
import eu.pb4.factorytools.api.block.model.SignModel;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModelManager;
import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.common.api.PolymerCommonUtils;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.virtualentity.api.BlockWithElementHolder;
import net.minecraft.block.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.CabinetBlock;

import java.util.function.Function;

import static com.phoen1x.mynethersdelight.impl.MyNethersDelightPatch.id;

@Mixin(MNDBlocks.class)
public abstract class MNDBlocksMixin {

    @Inject(
            method = "register(Ljava/lang/String;Lnet/minecraft/block/AbstractBlock$Settings;Ljava/util/function/Function;)Lnet/minecraft/block/Block;",
            at = @At("RETURN")
    )
    private static <B extends Block> void polymerifyBlock(
            String name,
            AbstractBlock.Settings properties,
            Function<AbstractBlock.Settings, B> factory,
            CallbackInfoReturnable<B> cir
    ) {
        Block block = cir.getReturnValue();
        if (block == null) return;

        polymerify(name, block);
    }

    @Unique
    private static void polymerify(String path, Block block) {
        Identifier blockId = MyNethersDelight.id(path);
        BlockStateModelManager.addBlock(blockId, block);
        boolean solid = true;
        PolymerBlock overlay = null;

        if (path.endsWith("wall_hanging_sign")) {
            overlay = StateCopyFactoryBlock.HANGING_WALL_SIGN;
            MyNethersDelightPatch.LATE_INIT.add(() -> SignModel.setModel(block, id("block/" + path)));
        } else if (path.endsWith("hanging_sign")) {
            overlay = StateCopyFactoryBlock.HANGING_SIGN;
            MyNethersDelightPatch.LATE_INIT.add(() -> SignModel.setModel(block, id("block/" + path)));
        } else if (path.endsWith("wall_sign")) {
            overlay = StateCopyFactoryBlock.WALL_SIGN;
            MyNethersDelightPatch.LATE_INIT.add(() -> SignModel.setModel(block, id("block/" + path)));
        } else if (path.endsWith("sign")) {
            overlay = StateCopyFactoryBlock.SIGN;
            MyNethersDelightPatch.LATE_INIT.add(() -> SignModel.setModel(block, id("block/" + path)));
        }

        else if (block instanceof PowderyCannonBlock
                || block instanceof PowderyCannonSaplingBlock
                || block instanceof PowderyCaneBlock
                || block instanceof PowderyFlowerBlock) {
            overlay = BaseFactoryBlock.SAPLING;
        }

        else if (block instanceof SlabBlock) {
            overlay = SlabFactoryBlock.INSTANCE;
        } else if (block instanceof StairsBlock) {
            overlay = StateCopyFactoryBlock.STAIR;
        }

        else if (block instanceof DoorBlock) {
            overlay = DoorPolymerBlock.INSTANCE;
        } else if (block instanceof TrapdoorBlock) {
            overlay = TrapdoorPolymerBlock.INSTANCE;
        }

        else if (block instanceof FenceBlock) {
            overlay = StateCopyFactoryBlock.FENCE;
        } else if (block instanceof FenceGateBlock) {
            overlay = StateCopyFactoryBlock.FENCE_GATE;
        } else if (block instanceof ButtonBlock) {
            overlay = StateCopyFactoryBlock.BUTTON;
        } else if (block instanceof PressurePlateBlock) {
            overlay = StateCopyFactoryBlock.PRESSURE_PLATE;
        }

        else if (block instanceof BlazierBlock || block instanceof CampfireBlock) {
            overlay = CampfireFactoryBlock.INSTANCE;
            solid = false;
        }

        else if (block instanceof WallTorchBlock) {
            overlay = BaseFactoryBlock.SAPLING.withTick(true).withModel(TorchModel.Wall::new);
        } else if (block instanceof TorchBlock) {
            overlay = BaseFactoryBlock.SAPLING.withTick(true).withModel(TorchModel::new);
        } else if (block instanceof NetherStoveBlock) {
            overlay = BaseFactoryBlock.BARRIER;
        }

        else if (block instanceof BreadLoafBlock) {
            overlay = BaseFactoryBlock.SAPLING;
        } else if (block instanceof StriderloafBlock) {
            overlay = StatePolymerBlock.of(block, BlockModelType.TRIPWIRE_BLOCK);
        } else if (block instanceof FlowerPotBlock) {
            overlay = BaseFactoryBlock.SAPLING;
        } else if (block instanceof MagmaCakeBlock) {
            overlay = BaseFactoryBlock.CAMPFIRE;
        } else if (block instanceof CabinetBlock || path.endsWith("_crate")) {
            overlay = StatePolymerBlock.of(block, BlockModelType.FULL_BLOCK);
        }

        else if (!(block instanceof BlockEntityProvider) && block.getDefaultState().isFullCube(PolymerCommonUtils.getFakeWorld(), BlockPos.ORIGIN)) {
            overlay = StatePolymerBlock.of(block, BlockModelType.FULL_BLOCK);
        }

        if (overlay == null) {
            overlay = block.getDefaultState().getCollisionShape(PolymerCommonUtils.getFakeWorld(), BlockPos.ORIGIN).isEmpty()
                    ? BaseFactoryBlock.SAPLING
                    : BaseFactoryBlock.BARRIER;
        }

        boolean finalSolid = solid;
        MyNethersDelightPatch.LATE_INIT.add(() -> {
            if (finalSolid) {
                BlockStateModelManager.addSolidBlock(blockId, block);
            } else {
                BlockStateModelManager.addTransparentBlock(blockId, block);
            }
        });

        PolymerBlock.registerOverlay(block, overlay);
        if (overlay instanceof BlockWithElementHolder blockWithElementHolder) {
            BlockWithElementHolder.registerOverlay(block, blockWithElementHolder);
        }
    }
}