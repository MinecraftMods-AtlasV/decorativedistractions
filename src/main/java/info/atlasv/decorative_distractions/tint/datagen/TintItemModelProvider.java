package info.atlasv.decorative_distractions.tint.datagen;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import info.atlasv.decorative_distractions.tint.item.TintItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class TintItemModelProvider extends ItemModelProvider {

    public TintItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, DecorativeDistractions.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        TintItems.registerModels(this);
    }

    @Override
    public String getName() {
        return "Tint Item Models";
    }
}