package info.atlasv.decorative_distractions.lights.datagen;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import info.atlasv.decorative_distractions.lights.block.LightsBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class LightsItemModelProvider extends ItemModelProvider {
    public LightsItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, DecorativeDistractions.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // Each module gets handed `this` (the provider tool) and uses it
        LightsBlocks.registerModels(this);
    }

    @Override
    public String getName() {
        return "Lights Item Models";
    }
}