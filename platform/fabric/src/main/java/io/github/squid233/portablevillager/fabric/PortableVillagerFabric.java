package io.github.squid233.portablevillager.fabric;

import io.github.squid233.portablevillager.PVCommon;
import io.github.squid233.portablevillager.PVItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class PortableVillagerFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.ITEM, PVItems.VILLAGER_ITEM_ID, PVItems.VILLAGER_ITEM);
        Registry.register(BuiltInRegistries.ITEM, PVItems.OLD_VILLAGER_ITEM_ID, PVItems.OLD_VILLAGER_ITEM);

        UseEntityCallback.EVENT.register((player, world, _, entity, _) ->
            PVCommon.onPickVillager(player, entity, world));
    }
}
