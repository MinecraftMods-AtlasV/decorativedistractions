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
        BasicDataGenerators.register(event);
        LightsDataGenerators.register(event);
        TintDataGenerators.register(event);

        event.getGenerator().addProvider(event.includeClient(), new CoreLangProvider(event.getGenerator().getPackOutput()));
    }
}