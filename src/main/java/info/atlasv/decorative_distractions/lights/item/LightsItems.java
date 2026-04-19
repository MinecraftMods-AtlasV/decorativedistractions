package info.atlasv.decorative_distractions.lights.item;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;

public class LightsItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DecorativeDistractions.MODID);
    public static Collection<DeferredHolder<Item, ? extends Item>> getEntries() {
        return ITEMS.getEntries();
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
    public static void registerModels(ItemModelProvider provider) {

    }


}
