package fuzs.tooltipinsights.impl.client;

import com.google.common.collect.ImmutableList;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.gui.ItemTooltipCallback;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.tooltipinsights.api.v1.client.gui.tooltip.DescriptionLines;
import fuzs.tooltipinsights.api.v1.client.gui.tooltip.InternalNameLines;
import fuzs.tooltipinsights.api.v1.client.gui.tooltip.ModNameLines;
import fuzs.tooltipinsights.api.v1.client.gui.tooltip.TooltipLinesExtractor;
import fuzs.tooltipinsights.api.v1.client.handler.TooltipDescriptionsHandler;
import fuzs.tooltipinsights.api.v1.config.StyledTooltipsConfig;
import fuzs.tooltipinsights.api.v1.config.TooltipComponentsConfig;
import fuzs.tooltipinsights.api.v1.config.TooltipDescriptionMode;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TooltipInsightsClientDevelopment implements ClientModConstructor {
    private static final TooltipLinesExtractor<MobEffectInstance, TooltipComponentsConfig> DESCRIPTION = new DescriptionLines<>() {
        @Override
        public Stream<Component> getTooltipLines(MobEffectInstance value, int maxWidth) {
            return Stream.of(Component.literal("Prevents fire and lava damage."));
        }

        @Override
        protected String getDescriptionId(MobEffectInstance value) {
            throw new UnsupportedOperationException();
        }
    };
    private static final TooltipLinesExtractor<MobEffectInstance, TooltipComponentsConfig> MOD_NAME = new ModNameLines<>() {
        @Override
        protected ResourceKey<?> getResourceKey(MobEffectInstance mobEffect) {
            return mobEffect.getEffect().unwrapKey().orElseThrow();
        }
    };
    private static final TooltipLinesExtractor<MobEffectInstance, TooltipComponentsConfig> INTERNAL_NAME = new InternalNameLines<>() {
        @Override
        protected ResourceKey<?> getResourceKey(MobEffectInstance mobEffect) {
            return mobEffect.getEffect().unwrapKey().orElseThrow();
        }
    };
    private static final List<TooltipLinesExtractor<MobEffectInstance, TooltipComponentsConfig>> ITEM_SUPPLIERS = ImmutableList.of(
            DESCRIPTION,
            MOD_NAME,
            INTERNAL_NAME);

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ItemTooltipCallback.EVENT.register(EventPhase.LAST, new TooltipDescriptionsHandler<>(ITEM_SUPPLIERS) {
            @Override
            protected StyledTooltipsConfig<TooltipComponentsConfig> getStyleConfig() {
                TooltipComponentsConfig tooltipLines = new TooltipComponentsConfig();
                tooltipLines.modName = tooltipLines.internalName = true;
                StyledTooltipsConfig<TooltipComponentsConfig> styleConfig = new StyledTooltipsConfig<>(tooltipLines);
                styleConfig.tooltipDescriptions = TooltipDescriptionMode.SHIFT;
                return styleConfig;
            }

            @Override
            protected Map<String, MobEffectInstance> getByDescriptionId(ItemStack itemStack, HolderLookup.Provider registries) {
                // an item can contain the same effect multiple times, so make sure to include a merge function in our collect call
                return StreamSupport.stream(itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
                                .getAllEffects()
                                .spliterator(), false)
                        .collect(Collectors.toMap(MobEffectInstance::getDescriptionId,
                                Function.identity(),
                                (MobEffectInstance o1, MobEffectInstance o2) -> o2));
            }
        }::onItemTooltip);
    }
}
