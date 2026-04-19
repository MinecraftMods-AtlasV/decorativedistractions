package info.atlasv.decorative_distractions.basic.datagen;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import info.atlasv.decorative_distractions.basic.block.BasicAmethystBlockSets;
import info.atlasv.decorative_distractions.basic.block.BasicStoneBlockSets;
import info.atlasv.decorative_distractions.core.blocksets.Amethyst;
import info.atlasv.decorative_distractions.core.blocksets.Stone;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BasicBlockStateProvider extends BlockStateProvider {
    public BasicBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, DecorativeDistractions.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (Stone set : BasicStoneBlockSets.SMOOTH_VARIANTS.values()) {
            registerStoneSet(set);
        }
        for (Stone set : BasicStoneBlockSets.SMOOTH_BRICKS_VARIANTS.values()) {
            registerStoneSet(set);
        }
        for (Amethyst set : BasicAmethystBlockSets.CRYSTALS.values()) {
            registerAmethystSet(set);
        }

        // Vanilla amethyst slab & stairs, only registered if
        // `BasicAmethystBlockSets.registerVanillaAmethystSlabsAndStairs()` was called.
        // TODO: is this prevention even needed?
        if (BasicAmethystBlockSets.VANILLA_AMETHYST_SLAB != null) {
            registerVanillaAmethystSlabsAndStairs();
        }
    }

    private void registerStoneSet(Stone set) {
        // Full cube block
        simpleBlockWithItem(set.block.get(), cubeAll(set.block.get()));

        // Stairs
        stairsBlock(set.stairs.get(), modLoc(set.texturePath));
        simpleBlockItem(set.stairs.get(), models().getExistingFile(modLoc("block/" + set.name + "_stairs")));

        // Slab - top texture same as the full block
        slabBlock(set.slab.get(), modLoc("block/" + set.name), modLoc(set.texturePath));
        simpleBlockItem(set.slab.get(), models().getExistingFile(modLoc("block/" + set.name + "_slab")));

        // Wall
        wallBlock(set.wall.get(), modLoc(set.texturePath));
        simpleBlockItem(set.wall.get(), models().wallInventory(set.name + "_wall_inventory", modLoc(set.texturePath)));

        // Pressure plate
        pressurePlateBlock(set.pressurePlate.get(), modLoc(set.texturePath));
        simpleBlockItem(set.pressurePlate.get(), models().getExistingFile(modLoc("block/" + set.name + "_pressure_plate")));

        // Button
        buttonBlock(set.button.get(), modLoc(set.texturePath));
        simpleBlockItem(set.button.get(), models().buttonInventory(set.name + "_button_inventory", modLoc(set.texturePath)));
    }

    private void registerAmethystSet(Amethyst set) {
        // Solid block
        simpleBlockWithItem(set.block.get(),
                models().cubeAll(set.name + "_block", modLoc(set.blockTexture)));

        // Budding block - own texture, no item *really* needed in vanilla but registered for modded and for exploits
        simpleBlockWithItem(set.buddingBlock.get(),
                models().cubeAll("budding_" + set.name, modLoc(set.buddingTexture)));

        // Growth stages of buds
        registerClusterStage(set.cluster.get(),   set.name + "_cluster",         set.clusterTexture);
        registerClusterStage(set.largeBud.get(),  "large_" + set.name + "_bud",  set.largeBudTexture);
        registerClusterStage(set.mediumBud.get(), "medium_" + set.name + "_bud", set.mediumBudTexture);
        registerClusterStage(set.smallBud.get(),  "small_" + set.name + "_bud",  set.smallBudTexture);

        // Slab - double-slab state references the solid block model, all faces use blockTexture like planks,
        // and unlike smooth stone
        slabBlock(set.slab.get(), modLoc("block/" + set.name + "_block"), modLoc(set.blockTexture));
        simpleBlockItem(set.slab.get(),
                models().getExistingFile(modLoc("block/" + set.name + "_block_slab")));

        // Stairs - all faces use blockTexture.
        stairsBlock(set.stairs.get(), modLoc(set.blockTexture));
        simpleBlockItem(set.stairs.get(),
                models().getExistingFile(modLoc("block/" + set.name + "_block_stairs")));
    }

    /**
     * Registers blockstates and item models for the vanilla {@code amethyst_block} slab and
     * stairs added by {@link BasicAmethystBlockSets#registerVanillaAmethystSlabsAndStairs()}.
     *
     * <p>The texture is sourced from vanilla ({@code minecraft:block/amethyst_block}).
     * The double-slab state uses vanilla block
     */
    private void registerVanillaAmethystSlabsAndStairs() {
        // Slab - double slab state points at the vanilla full block model.
        slabBlock(
                BasicAmethystBlockSets.VANILLA_AMETHYST_SLAB.get(),
                mcLoc("block/amethyst_block"),   // full block model for double slab state
                mcLoc("block/amethyst_block")    // texture for single slab top/bottom/side
        );
        simpleBlockItem(
                BasicAmethystBlockSets.VANILLA_AMETHYST_SLAB.get(),
                models().getExistingFile(modLoc("block/amethyst_block_slab"))
        );

        // Stairs - all faces use the vanilla amethyst_block texture.
        stairsBlock(
                BasicAmethystBlockSets.VANILLA_AMETHYST_STAIRS.get(),
                mcLoc("block/amethyst_block")
        );
        simpleBlockItem(
                BasicAmethystBlockSets.VANILLA_AMETHYST_STAIRS.get(),
                models().getExistingFile(modLoc("block/amethyst_block_stairs"))
        );
    }

    // Registers the bud stages models
    private void registerClusterStage(AmethystClusterBlock block, String name, String texture) {
        ModelFile model = models()
                .withExistingParent(name, mcLoc("block/cross"))
                .texture("cross", modLoc(texture))
                .renderType("minecraft:cutout_mipped"); // Needed or the cross model has the texture on a black background

        getVariantBuilder(block).forAllStatesExcept(state -> {
            Direction dir = state.getValue(AmethystClusterBlock.FACING);
            int xRot = 0, yRot = 0;
            switch (dir) {
                case DOWN  -> { xRot = 180; yRot = 0;   }
                case NORTH -> { xRot = 90;  yRot = 0;   }
                case SOUTH -> { xRot = 90;  yRot = 180; }
                case WEST  -> { xRot = 90;  yRot = 270; }
                case EAST  -> { xRot = 90;  yRot = 90;  }
                default    -> { xRot = 0;   yRot = 0;   } // UP not really needed bc of the int type def above the switch
            }
            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationX(xRot)
                    .rotationY(yRot)
                    .build();
        }, AmethystClusterBlock.WATERLOGGED); // WATERLOGGED doesn't affect the model so skip it

        // Makes the item model a flat texture instead of the cross model, like the vanilla amethyst texture
        itemModels().singleTexture(name, mcLoc("item/generated"), "layer0", modLoc(texture));
    }

    // Prevents "Duplciate Providers" error when running data gen
    @Override
    public String getName() {
        return "Basic Block States";
    }
}