package fuzs.tooltipinsights.impl.client;

import com.google.common.collect.ImmutableList;
import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.client.event.v1.gui.ItemTooltipCallback;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.event.v1.core.EventPhase;
import fuzs.tooltipinsights.api.v1.client.gui.tooltip.DescriptionLines;
import fuzs.tooltipinsights.api.v1.client.gui.tooltip.InternalNameLines;
import fuzs.tooltipinsights.api.v1.client.gui.tooltip.ModNameLines;
import fuzs.tooltipinsights.api.v1.client.gui.tooltip.TooltipLinesExtractor;
import fuzs.tooltipinsights.api.v1.client.handler.TooltipDescriptionsHandler;
import fuzs.tooltipinsights.api.v1.config.AbstractClientConfig;
import fuzs.tooltipinsights.api.v1.config.TooltipDescriptionMode;
import fuzs.tooltipinsights.impl.TooltipInsights;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TooltipInsightsClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        setupDevelopmentEnvironment();
    }

    private static void setupDevelopmentEnvironment() {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment(TooltipInsights.MOD_ID)) return;
        ItemTooltipCallback.EVENT.register(EventPhase.LAST, new TooltipDescriptionsHandler<MobEffectInstance>() {
            static final TooltipLinesExtractor<MobEffectInstance, AbstractClientConfig.TooltipComponents> DESCRIPTION = new DescriptionLines<>() {
                @Override
                protected String getDescriptionId(MobEffectInstance mobEffect) {
                    return mobEffect.getDescriptionId();
                }
            };
            static final TooltipLinesExtractor<MobEffectInstance, AbstractClientConfig.TooltipComponents> MOD_NAME = new ModNameLines<>() {
                @Override
                protected ResourceKey<?> getResourceKey(MobEffectInstance mobEffect) {
                    return mobEffect.getEffect().unwrapKey().orElseThrow();
                }
            };
            static final TooltipLinesExtractor<MobEffectInstance, AbstractClientConfig.TooltipComponents> INTERNAL_NAME = new InternalNameLines<>() {
                @Override
                protected ResourceKey<?> getResourceKey(MobEffectInstance mobEffect) {
                    return mobEffect.getEffect().unwrapKey().orElseThrow();
                }
            };
            static final List<TooltipLinesExtractor<MobEffectInstance, AbstractClientConfig.TooltipComponents>> ITEM_SUPPLIERS = ImmutableList.of(
                    DESCRIPTION,
                    MOD_NAME,
                    INTERNAL_NAME);

            @Override
            protected TooltipDescriptionMode getItemDescriptionMode() {
                return TooltipDescriptionMode.SHIFT;
            }

            @Override
            protected Map<String, MobEffectInstance> getByDescriptionId(ItemStack itemStack, HolderLookup.Provider registries) {
                // an item can contain the same effect multiple times, so make sure to include a merge function in our collect call
                return StreamSupport.stream(itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
                                .getAllEffects()
                                .spliterator(), false)
                        .collect(Collectors.toMap(MobEffectInstance::getDescriptionId,
                                Function.identity(),
                                (MobEffectInstance o1, MobEffectInstance o2) -> o1));
            }

            @Override
            protected List<Component> getItemTooltipLines(MobEffectInstance mobEffectInstance) {
                return TooltipLinesExtractor.getTooltipLines(ITEM_SUPPLIERS,
                        Component.literal(" \u25C6 "),
                        Style.EMPTY.applyFormat(ChatFormatting.GRAY),
                        mobEffectInstance,
                        Util.make(new AbstractClientConfig.TooltipComponents(),
                                (AbstractClientConfig.TooltipComponents tooltipComponents) -> {
                                    tooltipComponents.modName = tooltipComponents.internalName = true;
                                }));
            }
        }::onItemTooltip);
    }
}
