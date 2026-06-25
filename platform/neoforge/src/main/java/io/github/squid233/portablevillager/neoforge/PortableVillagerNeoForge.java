package io.github.squid233.portablevillager.neoforge;

import io.github.squid233.portablevillager.PVCommon;
import io.github.squid233.portablevillager.PVItems;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(PVCommon.MOD_ID)
@EventBusSubscriber(modid = PVCommon.MOD_ID)
public class PortableVillagerNeoForge {
    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        event.register(Registries.ITEM, registry -> registry.register(PVItems.VILLAGER_ITEM_ID, PVItems.VILLAGER_ITEM));
        event.register(Registries.ITEM, registry -> registry.register(PVItems.OLD_VILLAGER_ITEM_ID, PVItems.OLD_VILLAGER_ITEM));
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        event.setCancellationResult(PVCommon.onPickVillager(
            event.getEntity(),
            event.getTarget(),
            event.getLevel()
        ));
    }
}
