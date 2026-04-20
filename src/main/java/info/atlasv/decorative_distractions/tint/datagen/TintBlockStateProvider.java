package info.atlasv.decorative_distractions.tint.datagen;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import info.atlasv.decorative_distractions.tint.TintEntry;
import info.atlasv.decorative_distractions.tint.block.TintBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TintBlockStateProvider extends BlockStateProvider {

    public TintBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, DecorativeDistractions.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (TintEntry entry : TintBlocks.getEntries()) {
            String name = entry.name;
            ResourceLocation texture = modLoc("block/" + name);

            BlockModelBuilder cubeModel = buildTintedCubeModel(name, texture);
            simpleBlockWithItem(entry.block.get(), cubeModel);

            registerTintedSlab(entry, name, texture, cubeModel);
            registerTintedStairs(entry, name, texture);
        }
    }

    // Base block

    private BlockModelBuilder buildTintedCubeModel(String name, ResourceLocation texture) {
        return models()
                .withExistingParent("block/" + name, "minecraft:block/block")
                .texture("particle", texture)
                .texture("all", texture)
                .element()
                .from(0, 0, 0).to(16, 16, 16)
                .face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#all").tintindex(0).cullface(Direction.DOWN).end()
                .face(Direction.UP).uvs(0, 0, 16, 16).texture("#all").tintindex(0).cullface(Direction.UP).end()
                .face(Direction.NORTH).uvs(0, 0, 16, 16).texture("#all").tintindex(0).cullface(Direction.NORTH).end()
                .face(Direction.SOUTH).uvs(0, 0, 16, 16).texture("#all").tintindex(0).cullface(Direction.SOUTH).end()
                .face(Direction.WEST).uvs(0, 0, 16, 16).texture("#all").tintindex(0).cullface(Direction.WEST).end()
                .face(Direction.EAST).uvs(0, 0, 16, 16).texture("#all").tintindex(0).cullface(Direction.EAST).end()
                .end();
    }

    // Slab

    private void registerTintedSlab(TintEntry entry, String name, ResourceLocation texture, BlockModelBuilder fullModel) {
        String slabName = name + "_slab";

        BlockModelBuilder bottomModel = models()
                .withExistingParent("block/" + slabName, "minecraft:block/block")
                .texture("particle", texture)
                .texture("bottom", texture).texture("top", texture).texture("side", texture)
                .element().from(0, 0, 0).to(16, 8, 16)
                .face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#bottom").tintindex(0).cullface(Direction.DOWN).end()
                .face(Direction.UP).uvs(0, 0, 16, 16).texture("#top").tintindex(0).end()
                .face(Direction.NORTH).uvs(0, 8, 16, 16).texture("#side").tintindex(0).cullface(Direction.NORTH).end()
                .face(Direction.SOUTH).uvs(0, 8, 16, 16).texture("#side").tintindex(0).cullface(Direction.SOUTH).end()
                .face(Direction.WEST).uvs(0, 8, 16, 16).texture("#side").tintindex(0).cullface(Direction.WEST).end()
                .face(Direction.EAST).uvs(0, 8, 16, 16).texture("#side").tintindex(0).cullface(Direction.EAST).end()
                .end();

        BlockModelBuilder topModel = models()
                .withExistingParent("block/" + slabName + "_top", "minecraft:block/block")
                .texture("particle", texture)
                .texture("bottom", texture).texture("top", texture).texture("side", texture)
                .element().from(0, 8, 0).to(16, 16, 16)
                .face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#bottom").tintindex(0).end()
                .face(Direction.UP).uvs(0, 0, 16, 16).texture("#top").tintindex(0).cullface(Direction.UP).end()
                .face(Direction.NORTH).uvs(0, 0, 16, 8).texture("#side").tintindex(0).cullface(Direction.NORTH).end()
                .face(Direction.SOUTH).uvs(0, 0, 16, 8).texture("#side").tintindex(0).cullface(Direction.SOUTH).end()
                .face(Direction.WEST).uvs(0, 0, 16, 8).texture("#side").tintindex(0).cullface(Direction.WEST).end()
                .face(Direction.EAST).uvs(0, 0, 16, 8).texture("#side").tintindex(0).cullface(Direction.EAST).end()
                .end();

        slabBlock(entry.slab.get(), bottomModel, topModel, fullModel);
        simpleBlockItem(entry.slab.get(), bottomModel);
    }

    // Stairs, fully inlined geometry so tintindex is preserved
    // If not inlined it breaks and jsut displays the raw base texture on the item and block
    // does introduce the issue of the item model being rotated 270 degrees clockwise incorrectly

    private void registerTintedStairs(TintEntry entry, String name, ResourceLocation texture) {
        String n = name + "_stairs";

        BlockModelBuilder straight  = buildTintedStairsStraight(n, texture);
        BlockModelBuilder inner     = buildTintedStairsInner(n + "_inner", texture);
        BlockModelBuilder outer     = buildTintedStairsOuter(n + "_outer", texture);
        BlockModelBuilder inventory = buildTintedStairsInventory(n, texture);

        stairsBlock(entry.stairs.get(), straight, inner, outer);
        simpleBlockItem(entry.stairs.get(), inventory);
    }


    private BlockModelBuilder buildTintedStairsStraight(String modelName, ResourceLocation texture) {
        return models()
                .withExistingParent("block/" + modelName, "minecraft:block/block")
                .texture("particle", texture)
                .texture("bottom", texture).texture("top", texture).texture("side", texture)
                // lower slab portion
                .element().from(0, 0, 0).to(16, 8, 16)
                .face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#bottom").tintindex(0).cullface(Direction.DOWN).end()
                .face(Direction.UP).uvs(0, 0, 16, 16).texture("#top").tintindex(0).end()
                .face(Direction.NORTH).uvs(0, 8, 16, 16).texture("#side").tintindex(0).cullface(Direction.NORTH).end()
                .face(Direction.SOUTH).uvs(0, 8, 16, 16).texture("#side").tintindex(0).cullface(Direction.SOUTH).end()
                .face(Direction.WEST).uvs(0, 8, 16, 16).texture("#side").tintindex(0).cullface(Direction.WEST).end()
                .face(Direction.EAST).uvs(0, 8, 16, 16).texture("#side").tintindex(0).cullface(Direction.EAST).end()
                .end()
                // upper step
                .element().from(8, 8, 0).to(16, 16, 16)
                .face(Direction.DOWN).uvs(8, 0, 16, 16).texture("#top").tintindex(0).end()
                .face(Direction.UP).uvs(8, 0, 16, 16).texture("#top").tintindex(0).cullface(Direction.UP).end()
                .face(Direction.NORTH).uvs(0, 0, 8, 8).texture("#side").tintindex(0).cullface(Direction.NORTH).end()
                .face(Direction.SOUTH).uvs(0, 0, 8, 8).texture("#side").tintindex(0).cullface(Direction.SOUTH).end()
                .face(Direction.WEST).uvs(0, 0, 16, 8).texture("#side").tintindex(0).end()
                .face(Direction.EAST).uvs(0, 0, 16, 8).texture("#side").tintindex(0).cullface(Direction.EAST).end()
                .end();
    }


    private BlockModelBuilder buildTintedStairsInner(String modelName, ResourceLocation texture) {
        return models()
                .withExistingParent("block/" + modelName, "minecraft:block/block")
                .texture("particle", texture)
                .texture("bottom", texture).texture("top", texture).texture("side", texture)
                // lower slab portion
                .element().from(0, 0, 0).to(16, 8, 16)
                .face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#bottom").tintindex(0).cullface(Direction.DOWN).end()
                .face(Direction.UP).uvs(0, 0, 16, 16).texture("#top").tintindex(0).end()
                .face(Direction.NORTH).uvs(0, 8, 16, 16).texture("#side").tintindex(0).cullface(Direction.NORTH).end()
                .face(Direction.SOUTH).uvs(0, 8, 16, 16).texture("#side").tintindex(0).cullface(Direction.SOUTH).end()
                .face(Direction.WEST).uvs(0, 8, 16, 16).texture("#side").tintindex(0).cullface(Direction.WEST).end()
                .face(Direction.EAST).uvs(0, 8, 16, 16).texture("#side").tintindex(0).cullface(Direction.EAST).end()
                .end()
                // upper east step
                .element().from(8, 8, 0).to(16, 16, 16)
                .face(Direction.DOWN).uvs(8, 0, 16, 16).texture("#top").tintindex(0).end()
                .face(Direction.UP).uvs(8, 0, 16, 16).texture("#top").tintindex(0).cullface(Direction.UP).end()
                .face(Direction.NORTH).uvs(0, 0, 8, 8).texture("#side").tintindex(0).cullface(Direction.NORTH).end()
                .face(Direction.SOUTH).uvs(0, 0, 8, 8).texture("#side").tintindex(0).cullface(Direction.SOUTH).end()
                .face(Direction.WEST).uvs(0, 0, 16, 8).texture("#side").tintindex(0).end()
                .face(Direction.EAST).uvs(0, 0, 16, 8).texture("#side").tintindex(0).cullface(Direction.EAST).end()
                .end()
                // upper south west step
                .element().from(0, 8, 8).to(8, 16, 16)
                .face(Direction.DOWN).uvs(0, 8, 8, 16).texture("#top").tintindex(0).end()
                .face(Direction.UP).uvs(0, 8, 8, 16).texture("#top").tintindex(0).cullface(Direction.UP).end()
                .face(Direction.NORTH).uvs(8, 0, 16, 8).texture("#side").tintindex(0).end()
                .face(Direction.SOUTH).uvs(8, 0, 16, 8).texture("#side").tintindex(0).cullface(Direction.SOUTH).end()
                .face(Direction.WEST).uvs(0, 0, 8, 8).texture("#side").tintindex(0).cullface(Direction.WEST).end()
                .face(Direction.EAST).uvs(0, 0, 8, 8).texture("#side").tintindex(0).end()
                .end();
    }


    private BlockModelBuilder buildTintedStairsOuter(String modelName, ResourceLocation texture) {
        return models()
                .withExistingParent("block/" + modelName, "minecraft:block/block")
                .texture("particle", texture)
                .texture("bottom", texture).texture("top", texture).texture("side", texture)
                // lower slab portion
                .element().from(0, 0, 0).to(16, 8, 16)
                .face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#bottom").tintindex(0).cullface(Direction.DOWN).end()
                .face(Direction.UP).uvs(0, 0, 16, 16).texture("#top").tintindex(0).end()
                .face(Direction.NORTH).uvs(0, 8, 16, 16).texture("#side").tintindex(0).cullface(Direction.NORTH).end()
                .face(Direction.SOUTH).uvs(0, 8, 16, 16).texture("#side").tintindex(0).cullface(Direction.SOUTH).end()
                .face(Direction.WEST).uvs(0, 8, 16, 16).texture("#side").tintindex(0).cullface(Direction.WEST).end()
                .face(Direction.EAST).uvs(0, 8, 16, 16).texture("#side").tintindex(0).cullface(Direction.EAST).end()
                .end()
                // upper corner step
                .element().from(8, 8, 8).to(16, 16, 16)
                .face(Direction.DOWN).uvs(8, 8, 16, 16).texture("#top").tintindex(0).end()
                .face(Direction.UP).uvs(8, 8, 16, 16).texture("#top").tintindex(0).cullface(Direction.UP).end()
                .face(Direction.NORTH).uvs(0, 0, 8, 8).texture("#side").tintindex(0).end()
                .face(Direction.SOUTH).uvs(0, 0, 8, 8).texture("#side").tintindex(0).cullface(Direction.SOUTH).end()
                .face(Direction.WEST).uvs(8, 0, 16, 8).texture("#side").tintindex(0).end()
                .face(Direction.EAST).uvs(8, 0, 16, 8).texture("#side").tintindex(0).cullface(Direction.EAST).end()
                .end();
    }

    private BlockModelBuilder buildTintedStairsInventory(String modelName, ResourceLocation texture) {
        return models()
                .withExistingParent("block/" + modelName, "minecraft:block/block")
                .texture("particle", texture)
                .texture("bottom", texture).texture("top", texture).texture("side", texture)
                // lower slab portion
                .element().from(0, 0, 0).to(16, 8, 16)
                .face(Direction.DOWN).uvs(0, 0, 16, 16).texture("#bottom").tintindex(0).cullface(Direction.DOWN).end()
                .face(Direction.UP).uvs(0, 0, 16, 16).texture("#top").tintindex(0).end()
                .face(Direction.NORTH).uvs(0, 8, 16, 16).texture("#side").tintindex(0).cullface(Direction.NORTH).end()
                .face(Direction.SOUTH).uvs(0, 8, 16, 16).texture("#side").tintindex(0).cullface(Direction.SOUTH).end()
                .face(Direction.WEST).uvs(0, 8, 16, 16).texture("#side").tintindex(0).cullface(Direction.WEST).end()
                .face(Direction.EAST).uvs(0, 8, 16, 16).texture("#side").tintindex(0).cullface(Direction.EAST).end()
                .end()
                // upper step
                .element().from(8, 8, 0).to(16, 16, 16)
                .face(Direction.DOWN).uvs(8, 0, 16, 16).texture("#top").tintindex(0).end()
                .face(Direction.UP).uvs(8, 0, 16, 16).texture("#top").tintindex(0).cullface(Direction.UP).end()
                .face(Direction.NORTH).uvs(0, 0, 8, 8).texture("#side").tintindex(0).cullface(Direction.NORTH).end()
                .face(Direction.SOUTH).uvs(0, 0, 8, 8).texture("#side").tintindex(0).cullface(Direction.SOUTH).end()
                .face(Direction.WEST).uvs(0, 0, 16, 8).texture("#side").tintindex(0).end()
                .face(Direction.EAST).uvs(0, 0, 16, 8).texture("#side").tintindex(0).cullface(Direction.EAST).end()
                .end()
                .transforms()
                .transform(ItemDisplayContext.GUI)
                .rotation(30, 135, 0).scale(0.625f)
                .end()
                .transform(ItemDisplayContext.FIXED)
                .rotation(0, 90, 0).scale(0.5f)
                .end()
                .end();
    }
}