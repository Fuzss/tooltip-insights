package fuzs.tooltipinsights.common.impl.client;

import com.google.common.collect.ImmutableList;
import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.common.api.client.core.v1.context.ResourcePackReloadListenersContext;
import fuzs.puzzleslib.common.api.client.event.v1.gui.ItemTooltipCallback;
import fuzs.puzzleslib.common.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.common.api.event.v1.core.EventPhase;
import fuzs.tooltipinsights.common.api.v1.client.gui.tooltip.DescriptionLines;
import fuzs.tooltipinsights.common.api.v1.client.gui.tooltip.InternalNameLines;
import fuzs.tooltipinsights.common.api.v1.client.gui.tooltip.ModNameLines;
import fuzs.tooltipinsights.common.api.v1.client.gui.tooltip.TooltipLinesExtractor;
import fuzs.tooltipinsights.common.api.v1.client.handler.TooltipDescriptionsHandler;
import fuzs.tooltipinsights.common.api.v1.config.StyledTooltipsConfig;
import fuzs.tooltipinsights.common.api.v1.config.TooltipComponentsConfig;
import fuzs.tooltipinsights.common.api.v1.config.TooltipDescriptionMode;
import fuzs.tooltipinsights.common.impl.TooltipInsights;
import fuzs.tooltipinsights.common.impl.client.gui.font.SizedAtlasGlyphProvider;
import fuzs.tooltipinsights.common.impl.client.gui.font.WidthLimitedGlyphProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Util;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TooltipInsightsClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        setupDevelopmentEnvironment();
    }

    private static void setupDevelopmentEnvironment() {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment(TooltipInsights.MOD_ID)) {
            return;
        }

        ItemTooltipCallback.EVENT.register(EventPhase.LAST, new TooltipDescriptionsHandler<MobEffectInstance>() {
            static final TooltipLinesExtractor<MobEffectInstance, TooltipComponentsConfig> DESCRIPTION = new DescriptionLines<>() {
                @Override
                public Stream<Component> getTooltipLines(MobEffectInstance value, int maxWidth) {
                    return Stream.of(Component.literal("Prevents fire and lava damage."));
                }

                @Override
                protected String getDescriptionId(MobEffectInstance mobEffect) {
                    throw new UnsupportedOperationException();
                }
            };
            static final TooltipLinesExtractor<MobEffectInstance, TooltipComponentsConfig> MOD_NAME = new ModNameLines<>() {
                @Override
                protected ResourceKey<?> getResourceKey(MobEffectInstance mobEffect) {
                    return mobEffect.getEffect().unwrapKey().orElseThrow();
                }
            };
            static final TooltipLinesExtractor<MobEffectInstance, TooltipComponentsConfig> INTERNAL_NAME = new InternalNameLines<>() {
                @Override
                protected ResourceKey<?> getResourceKey(MobEffectInstance mobEffect) {
                    return mobEffect.getEffect().unwrapKey().orElseThrow();
                }
            };
            static final List<TooltipLinesExtractor<MobEffectInstance, TooltipComponentsConfig>> ITEM_SUPPLIERS = ImmutableList.of(
                    DESCRIPTION,
                    MOD_NAME,
                    INTERNAL_NAME);

            @Override
            protected StyledTooltipsConfig<?> getStyleConfig() {
                return Util.make(new StyledTooltipsConfig<>(new TooltipComponentsConfig()),
                        (StyledTooltipsConfig<TooltipComponentsConfig> config) -> {
                            config.tooltipDescriptions = TooltipDescriptionMode.SHIFT;
                        });
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

            @Override
            protected List<Component> getItemTooltipLines(MobEffectInstance value) {
                return TooltipLinesExtractor.getTooltipLines(ITEM_SUPPLIERS,
                        Component.literal(" \u25C6 "),
                        Style.EMPTY.withColor(ChatFormatting.GRAY),
                        value,
                        Util.make(new TooltipComponentsConfig(), (TooltipComponentsConfig config) -> {
                            config.modName = config.internalName = true;
                        }));
            }
        }::onItemTooltip);
    }

    @Override
    public void onAddResourcePackReloadListeners(ResourcePackReloadListenersContext context) {
        context.registerReloadListener(TooltipInsights.id("glyph_providers"), (ResourceManager resourceManager) -> {
            SizedAtlasGlyphProvider.onResourceManagerReload();
            WidthLimitedGlyphProvider.onResourceManagerReload();
        });
    }
}
