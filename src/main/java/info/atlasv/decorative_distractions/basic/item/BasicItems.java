package info.atlasv.decorative_distractions.basic.item;

import info.atlasv.decorative_distractions.DecorativeDistractions;
import info.atlasv.decorative_distractions.basic.block.BasicAmethystBlockSets;
import info.atlasv.decorative_distractions.core.blocksets.Amethyst;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.Map;

public class BasicItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DecorativeDistractions.MODID);
    public static Collection<DeferredHolder<Item, ? extends Item>> getEntries() {
        return ITEMS.getEntries();
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static void registerModels(ItemModelProvider provider) {
        // Registers the modes for the ItemModelProvider for the crystal shards
        for (Map.Entry<BasicAmethystBlockSets.AmethystVariant, Amethyst> entry
                : BasicAmethystBlockSets.CRYSTALS.entrySet()) {

            Amethyst set   = entry.getValue();
            String  colour = entry.getKey().getColour();

            provider.withExistingParent(
                    set.shard.getId().getPath(),
                    provider.mcLoc("item/generated")
            ).texture("layer0", provider.modLoc("item/crystals/" + colour + "/crystal"));
        }
    }
}
