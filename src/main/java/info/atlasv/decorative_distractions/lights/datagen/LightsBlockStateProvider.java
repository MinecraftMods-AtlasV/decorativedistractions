package info.atlasv.decorative_distractions.lights.datagen;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import info.atlasv.decorative_distractions.lights.block.LightsBlocks;
import info.atlasv.decorative_distractions.lights.block.LightsRedstoneLightPanelBlock;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;


public class LightsBlockStateProvider extends BlockStateProvider {

    public LightsBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, DecorativeDistractions.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (var metalEntry : LightsBlocks.LIGHT_PANELS.entrySet()) {
            LightsBlocks.MetalVariant metal = metalEntry.getKey();
            for (var dyeEntry : metalEntry.getValue().entrySet()) {
                DyeColor dye = dyeEntry.getKey();
                registerLightPanel(dyeEntry.getValue(), metal, dye);
            }
        }
    }

    private void registerLightPanel(DeferredBlock<Block> deferredBlock,
                                    LightsBlocks.MetalVariant metal,
                                    DyeColor dye) {
        Block  block = deferredBlock.get();
        String name  = deferredBlock.getId().getPath();

        // Two models: one for unlit state, one for lit (emissive) state
        // Both share the same geometry. The parent model files define the panel shape
        // with texture variables #frame and #glass. The lit variant adds an emissive
        // overlay on the glass face
        //
        // Texture paths:
        //   frame = decorative_distractions:block/light_panel/frame/<metal_name>
        //   glass = decorative_distractions:block/light_panel/glass/panel_glass (tinted at runtime)

        // TODO: Change these to boob light (but not that name)
        BlockModelBuilder unlitModel = models()
                .withExistingParent(name, modLoc("block/bulb_lamp"))
                .texture("frame", modLoc("block/light_panel/frame/" + metal.getName()))
                .texture("glass", modLoc("block/light_panel/glass/panel_glass"));

        BlockModelBuilder litModel = models()
                .withExistingParent(name + "_lit", modLoc("block/bulb_lamp"))
                .texture("frame", modLoc("block/light_panel/frame/" + metal.getName()))
                .texture("glass", modLoc("block/light_panel/glass/panel_glass"));

        // Blockstate variants
        // 6 facing directions x 2 lit values x 2 inverted values = 24 variants per block.
        // INVERTED has no visual difference, so both INVERTED values map to the same model.
        // The rotation mapping assumes the base model's glass face points NORTH (-Z) I think? No actually it might not matter.
        //
        // Y-rotation (around vertical axis):
        //   NORTH = 0°, EAST = 90°, SOUTH = 180°, WEST = 270°
        // X-rotation (tilt):
        //   UP   = 270° (tilt the north face upward)
        //   DOWN = 90°  (tilt the north face downward)
        // WRONG ↑ I don't understand spacial translations

        getVariantBuilder(block).forAllStates(state -> {
            Direction facing   = state.getValue(LightsRedstoneLightPanelBlock.FACING);
            boolean   lit      = state.getValue(LightsRedstoneLightPanelBlock.LIT);
            // INVERTED is intentionally ignored bc no visual change needed.

            BlockModelBuilder model = lit ? litModel : unlitModel;
            int xRot = 0;
            int yRot = 0;

            switch (facing) {
                case NORTH -> { xRot =   90; yRot =   0; }
                case EAST  -> { xRot =   90; yRot =  90; }
                case SOUTH -> { xRot =   90; yRot = 180; }
                case WEST  -> { xRot =   90; yRot = 270; }
                case UP    -> { xRot =   0; yRot =   0; }
                case DOWN  -> { xRot = 180; yRot =   0; }
            }

            return ConfiguredModel.builder()
                    .modelFile(model)
                    .rotationX(xRot)
                    .rotationY(yRot)
                    .build();
        });

    }

    @Override
    public String getName() {
        return "Lights Block States";
    }
}