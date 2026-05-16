package io.github.squid233.portablevillager;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public final class PVItems {
    public static final Identifier VILLAGER_ITEM_ID = Identifier.fromNamespaceAndPath(PVCommon.MOD_ID, "villager");
    // We have to maintain compatibility.
    public static final Identifier OLD_VILLAGER_ITEM_ID = Identifier.fromNamespaceAndPath("portable-villager", "villager");
    public static final Item VILLAGER_ITEM = new VillagerItem(new Item.Properties()
        .setId(ResourceKey.create(Registries.ITEM, VILLAGER_ITEM_ID)));
    public static final Item OLD_VILLAGER_ITEM = new VillagerItem(new Item.Properties()
        .setId(ResourceKey.create(Registries.ITEM, OLD_VILLAGER_ITEM_ID)));

    private PVItems() {
    }
}
