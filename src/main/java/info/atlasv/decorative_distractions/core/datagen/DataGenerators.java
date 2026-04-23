package info.atlasv.decorative_distractions.core.datagen;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import info.atlasv.decorative_distractions.basic.datagen.BasicDataGenerators;
import info.atlasv.decorative_distractions.lights.datagen.LightsDataGenerators;
import info.atlasv.decorative_distractions.tint.datagen.TintDataGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = DecorativeDistractions.MODID)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

//        SculkDataGenerators.register(event);
//        BaseDataGenerators.register(event);
        LightsDataGenerators.register(event);
        TintDataGenerators.register(event);
        BasicDataGenerators.register(event);

        event.getGenerator().addProvider(event.includeClient(), new CoreLangProvider(event.getGenerator().getPackOutput()));
        generator.addProvider(event.includeServer(), new CoreRecipeProvider(packOutput, lookupProvider));
        BlockTagsProvider blockTagsProvider = new CoreBlockTagProvider(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTagsProvider);
        generator.addProvider(event.includeClient(), new CoreItemTagProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
    }
}