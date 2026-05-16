package io.github.squid233.portablevillager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class VillagerItem extends Item {
    public VillagerItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull InteractionResult useOn(@NonNull UseOnContext useOnContext) {
        ItemStack itemStack = useOnContext.getItemInHand();
        CustomData customData = itemStack.getComponents().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (customData.isEmpty()) {
            return InteractionResult.FAIL;
        }

        Level level = useOnContext.getLevel();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        Player player = useOnContext.getPlayer();
        Direction clickedFace = useOnContext.getClickedFace();
        BlockPos clickedPos = useOnContext.getClickedPos();
        BlockState blockState = level.getBlockState(clickedPos);
        BlockPos pos2 = blockState.getCollisionShape(level, clickedPos).isEmpty()
            ? clickedPos
            : clickedPos.relative(clickedFace);

        if (EntityType.VILLAGER.spawn((ServerLevel) level,
            villager -> {
                Vec3 position = villager.position();
                try (ProblemReporter.ScopedCollector problemReporter = new ProblemReporter.ScopedCollector(PVCommon.log)) {
                    ValueInput valueInput = TagValueInput.create(problemReporter, level.registryAccess(), customData.copyTag());
                    villager.load(valueInput);
                }
                villager.setPos(position);
            },
            pos2,
            EntitySpawnReason.SPAWN_ITEM_USE,
            true,
            !Objects.equals(clickedPos, pos2) && clickedFace == Direction.UP) != null) {
            itemStack.consume(1, player);
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public void appendHoverText(@NonNull ItemStack itemStack, @NonNull TooltipContext tooltipContext, @NonNull TooltipDisplay tooltipDisplay, @NonNull Consumer<Component> consumer, @NonNull TooltipFlag tooltipFlag) {
        CustomData customData = itemStack.getComponents().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (!customData.isEmpty()) {
            CompoundTag tag = customData.copyTag();
            Optional<VillagerData> optional = tag.read("VillagerData", VillagerData.CODEC);
            if (optional.isPresent()) {
                VillagerData villagerData = optional.get();
                var type = villagerData.type();
                var profession = villagerData.profession();
                int level = villagerData.level();
                consumer.accept(Component.translatable("itemTooltip.portable_villager.type", type.getRegisteredName()));
                consumer.accept(Component.translatable("itemTooltip.portable_villager.profession", profession.value().name(), profession.getRegisteredName()));
                consumer.accept(Component.translatable("itemTooltip.portable_villager.level", Component.translatableWithFallback("merchant.level." + level, "%s", level)));
            }
        }
    }
}
