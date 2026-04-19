package info.atlasv.decorative_distractions.compat;

import net.neoforged.bus.api.IEventBus;

public final class CompatLoader {
    public static void init(IEventBus modEventBus) {
        // Conditionally loads the XycraftCompatInit class if xycraft_world is present
//        if (ModList.get().isLoaded("xycraft_world")) {
//            info.atlasv.decorative_distractions.compat.xycraft.XycraftCompatInit.init(modEventBus);
//        }
    }
}