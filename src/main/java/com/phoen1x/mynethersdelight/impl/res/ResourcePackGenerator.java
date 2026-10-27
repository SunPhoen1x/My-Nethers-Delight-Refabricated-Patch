package com.phoen1x.mynethersdelight.impl.res;

import eu.pb4.factorytools.api.block.model.generic.BlockStateModelManager;
import eu.pb4.factorytools.api.resourcepack.ModelModifiers;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.api.PackResource;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import eu.pb4.polymer.resourcepack.extras.api.format.atlas.AtlasAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.atlas.SingleAtlasSource;
import eu.pb4.polymer.resourcepack.extras.api.format.model.ModelAsset;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.phoen1x.mynethersdelight.impl.MyNethersDelightPatch.id;

public class ResourcePackGenerator {
    private static final String MOD_ID = "mynethersdelight";
    private static final String PATCH_MOD_ID = "mynethersdelightpatch";

    private static final Set<String> EXPANDABLE = Set.of(
            "wall",
            "slab",
            "stairs",
            "fence",
            "fence_gate",
            "button",
            "pressure_plate"
    );

    public static void setup() {
        PolymerResourcePackUtils.RESOURCE_PACK_AFTER_INITIAL_CREATION_EVENT.register(ResourcePackGenerator::build);
    }

    private static void build(ResourcePackBuilder builder) {
        final var expansion = new Vec3d(0.08, 0.08, 0.08);
        var atlas = AtlasAsset.builder();
        createPowderySignModels(builder, atlas);
        builder.forEachResource((string, resource) -> {
            for (var expandable : EXPANDABLE) {
                if (string.contains(expandable) && string.startsWith("assets/" + MOD_ID + "/models/block/")) {
                    var asset = ModelAsset.fromJson(Objects.requireNonNull(resource.asString()));
                    if (asset.parent().isPresent()) {
                        var parentId = asset.parent().get();
                        var parentData = builder.getDataOrSource(AssetPaths.model(parentId) + ".json");
                        if (parentData != null) {
                            var parentAsset = ModelAsset.fromJson(new String(parentData, StandardCharsets.UTF_8));
                            builder.addData(
                                    AssetPaths.model(PATCH_MOD_ID, parentId.getPath()) + ".json",
                                    ModelModifiers.expandModel(parentAsset, expansion)
                            );
                        }
                    }
                }
            }
        });
        var uvLockedMap = BlockStateModelManager.UV_LOCKED_MODELS.get(MOD_ID);
        if (uvLockedMap != null) {
            for (var entry : uvLockedMap.entrySet()) {
                var expand = EXPANDABLE.stream().anyMatch(expandable -> entry.getKey().contains(expandable) && entry.getKey().startsWith("block/"))
                        ? expansion
                        : Vec3d.ZERO;

                for (var v : entry.getValue()) {
                    var suffix = "_uvlock_" + v.x() + "_" + v.y();
                    var modelId = v.model().withSuffixedPath(suffix);
                    var rawModelData = builder.getData(AssetPaths.model(v.model()) + ".json");
                    if (rawModelData == null) continue;

                    var asset = ModelAsset.fromJson(new String(rawModelData, StandardCharsets.UTF_8));
                    if (asset.parent().isPresent()) {
                        var parentId = asset.parent().get();
                        var parentData = builder.getDataOrSource(AssetPaths.model(parentId) + ".json");
                        if (parentData != null) {
                            var parentAsset = ModelAsset.fromJson(new String(parentData, StandardCharsets.UTF_8));
                            builder.addData(
                                    AssetPaths.model(PATCH_MOD_ID, parentId.getPath() + suffix) + ".json",
                                    ModelModifiers.expandModelAndRotateUVLocked(parentAsset, expand, v.x(), v.y())
                            );
                            builder.addData(
                                    AssetPaths.model(modelId) + ".json",
                                    new ModelAsset(
                                            Optional.of(Identifier.of(PATCH_MOD_ID, parentId.getPath() + suffix)),
                                            asset.elements(),
                                            asset.textures(),
                                            asset.display(),
                                            asset.guiLight(),
                                            asset.ambientOcclusion()
                                    ).toBytes()
                            );
                        }
                    }
                }
            }
        }
        builder.addResourceConverter((string, resource) -> {
            if (!string.contains("_uvlock_")) {
                for (var expandable : EXPANDABLE) {
                    if (string.contains(expandable) && string.startsWith("assets/" + MOD_ID + "/models/block/")) {
                        var asset = ModelAsset.fromJson(Objects.requireNonNull(resource.asString()));
                        return PackResource.fromAsset(new ModelAsset(
                                asset.parent().map(x -> id(x.getPath())),
                                asset.elements(),
                                asset.textures(),
                                asset.display(),
                                asset.guiLight(),
                                asset.ambientOcclusion()
                        ));
                    }
                }
            }
            return resource;
        });

        builder.addData("assets/minecraft/atlases/blocks.json", atlas.build());
    }

    private static void createPowderySignModels(ResourcePackBuilder builder, AtlasAsset.Builder atlas) {
        var textureRegular = Identifier.of(MOD_ID, "entity/signs/powdery");
        var textureHanging = Identifier.of(MOD_ID, "entity/signs/hanging/powdery");

        atlas.add(new SingleAtlasSource(textureRegular, Optional.empty()));
        atlas.add(new SingleAtlasSource(textureHanging, Optional.empty()));

        builder.addData(AssetPaths.blockModel(id("powdery_sign")), ModelAsset.builder()
                .parent(Identifier.of("factorytools", "block_sign/template_sign"))
                .texture("sign", textureRegular.toString()).build());

        builder.addData(AssetPaths.blockModel(id("powdery_wall_sign")), ModelAsset.builder()
                .parent(Identifier.of("factorytools", "block_sign/template_wall_sign"))
                .texture("sign", textureRegular.toString()).build());

        builder.addData(AssetPaths.blockModel(id("powdery_hanging_sign")), ModelAsset.builder()
                .parent(Identifier.of("factorytools", "block_sign/template_hanging_sign"))
                .texture("sign", textureHanging.toString()).build());

        builder.addData(AssetPaths.blockModel(id("powdery_wall_hanging_sign")), ModelAsset.builder()
                .parent(Identifier.of("factorytools", "block_sign/template_wall_hanging_sign"))
                .texture("sign", textureHanging.toString()).build());
    }
}