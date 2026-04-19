package info.atlasv.decorative_distractions.basic.datagen;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import info.atlasv.decorative_distractions.basic.item.BasicItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BasicItemModelProvider extends ItemModelProvider {
    public BasicItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, DecorativeDistractions.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // Each module gets handed `this` (the provider tool) and uses it
        BasicItems.registerModels(this);

    }

    // Prevents "Duplciate Providers" error when running data gen
    @Override
    public String getName() {
        return "Basic Item Models";
    }
}
