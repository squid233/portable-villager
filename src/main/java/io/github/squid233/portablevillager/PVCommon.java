package io.github.squid233.portablevillager;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PVCommon {
    public static final String MOD_ID = "portable_villager";
    public static final Logger log = LoggerFactory.getLogger(MOD_ID);

    private PVCommon() {
    }

    public static InteractionResult onPickVillager(Player player, Entity target, Level level) {
        GameType gameMode = player.gameMode();
        if (!player.isShiftKeyDown() ||
            target.getType() != EntityType.VILLAGER ||
            !player.getMainHandItem().isEmpty() ||
            (target instanceof LivingEntity livingEntity && (livingEntity.isSleeping() || livingEntity.isDeadOrDying())) ||
            gameMode == null ||
            gameMode.isBlockPlacingRestricted()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        try (ProblemReporter.ScopedCollector problemReporter = new ProblemReporter.ScopedCollector(target.problemPath(), log)) {
            TagValueOutput valueOutput = TagValueOutput.createWithContext(problemReporter, target.registryAccess());
            target.save(valueOutput);
            CompoundTag tag = valueOutput.buildResult();
            ItemStack itemStack = new ItemStack(PVItems.VILLAGER_ITEM);
            itemStack.applyComponentsAndValidate(DataComponentPatch.builder()
                .set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
                .build());
            player.addItem(itemStack);
        }
        target.discard();
        return InteractionResult.SUCCESS_SERVER;
    }
}
