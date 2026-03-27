package io.github.squid233.portablevillager;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.TagValueOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortableVillager implements ModInitializer {
    public static final String MOD_ID = "portable-villager";
    public static final Item VILLAGER_ITEM = new VillagerItem(new Item.Properties()
        .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, "villager"))));
    public static final Logger log = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, "villager"), VILLAGER_ITEM);

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            GameType gameMode = player.gameMode();
            if (!player.isShiftKeyDown() ||
                entity.getType() != EntityType.VILLAGER ||
                !player.getMainHandItem().isEmpty() ||
                (entity instanceof LivingEntity livingEntity && (livingEntity.isSleeping() || livingEntity.isDeadOrDying())) ||
                gameMode == null ||
                gameMode.isBlockPlacingRestricted()) {
                return InteractionResult.PASS;
            }
            if (world.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            try (ProblemReporter.ScopedCollector problemReporter = new ProblemReporter.ScopedCollector(entity.problemPath(), log)) {
                TagValueOutput valueOutput = TagValueOutput.createWithContext(problemReporter, entity.registryAccess());
                entity.save(valueOutput);
                CompoundTag tag = valueOutput.buildResult();
                ItemStack itemStack = new ItemStack(VILLAGER_ITEM);
                itemStack.applyComponentsAndValidate(DataComponentPatch.builder()
                    .set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
                    .build());
                player.addItem(itemStack);
            }
            entity.discard();
            return InteractionResult.SUCCESS_SERVER;
        });
    }
}
