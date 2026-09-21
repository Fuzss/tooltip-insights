package fuzs.tooltipinsights.common.api.v1.client.handler;

import fuzs.puzzleslib.common.api.client.event.v1.entity.player.ClientPlayerNetworkEvents;
import fuzs.puzzleslib.common.api.core.v1.ModLoaderEnvironment;
import fuzs.tooltipinsights.common.api.v1.client.gui.tooltip.DescriptionLines;
import fuzs.tooltipinsights.common.api.v1.client.gui.tooltip.TooltipLinesExtractor;
import fuzs.tooltipinsights.common.api.v1.config.StyledTooltipsConfig;
import fuzs.tooltipinsights.common.api.v1.config.TooltipComponentsConfig;
import fuzs.tooltipinsights.common.api.v1.config.TooltipDescriptionMode;
import fuzs.tooltipinsights.common.impl.TooltipInsights;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public abstract class TooltipDescriptionsHandler<T, C extends TooltipComponentsConfig> {
    protected final List<TooltipLinesExtractor<T, C>> extractorList;

    public TooltipDescriptionsHandler(List<TooltipLinesExtractor<T, C>> extractorList) {
        this.extractorList = extractorList;
    }

    public void onItemTooltip(ItemStack itemStack, List<Component> tooltipLines, Item.TooltipContext tooltipContext, @Nullable Player player, TooltipFlag tooltipFlag) {
        this.modifyTooltip(itemStack, tooltipLines, tooltipContext.registries(), tooltipFlag);
    }

    public void onGatherTooltipComponents(Minecraft minecraft, List<Component> tooltipLines) {
        this.modifyTooltip(ItemStack.EMPTY,
                tooltipLines,
                minecraft.getConnection().registryAccess(),
                minecraft.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);
    }

    private void modifyTooltip(ItemStack itemStack, List<Component> tooltipLines, HolderLookup.Provider registries, TooltipFlag tooltipFlag) {
        StyledTooltipsConfig<C> styleConfig = this.getStyleConfig();

        if (styleConfig.tooltipDescriptions == TooltipDescriptionMode.DISABLED) {
            return;
        }

        Map<Component, T> possibleNames = this.getByName(itemStack, registries);

        if (!possibleNames.isEmpty()) {
            MutableBoolean tooltipDescriptionsHint = new MutableBoolean(styleConfig.tooltipDescriptionsHint);

            for (MutableInt mutableInt = new MutableInt();
                 mutableInt.intValue() < tooltipLines.size(); mutableInt.increment()) {

                Component previousComponent = tooltipLines.get(mutableInt.intValue());
                modifyTranslatableContents(previousComponent,
                        UnaryOperator.identity(),
                        (Component component, UnaryOperator<Component> componentReplacer) -> {

                            if (possibleNames.containsKey(component)) {
                                T value = possibleNames.get(component);
                                Component name = this.getNameComponent(value);

                                if (name != null) {
                                    tooltipLines.set(mutableInt.intValue(), componentReplacer.apply(name));
                                }

                                if (styleConfig.tooltipDescriptions.isActive()) {
                                    List<Component> list = this.getItemTooltipLines(value, styleConfig);
                                    tooltipLines.addAll(mutableInt.intValue() + 1, list);
                                    mutableInt.add(list.size());
                                    return true;
                                } else if (tooltipDescriptionsHint.isTrue()) {
                                    // make sure the view description line is only added when there will actually be a description
                                    tooltipDescriptionsHint.setFalse();
                                    styleConfig.tooltipDescriptions.processTooltipLines(itemStack,
                                            tooltipLines,
                                            tooltipFlag);
                                    return true;
                                }
                            }

                            return false;
                        });
            }
        }
    }

    protected abstract StyledTooltipsConfig<C> getStyleConfig();

    protected abstract Map<Component, T> getByName(ItemStack itemStack, HolderLookup.Provider registries);

    @Nullable
    protected Component getNameComponent(T value) {
        return null;
    }

    protected List<Component> getItemTooltipLines(T value, StyledTooltipsConfig<C> styleConfig) {
        return TooltipLinesExtractor.getTooltipLines(this.extractorList,
                styleConfig.descriptionDecorationComponent,
                styleConfig.descriptionStyle,
                value,
                styleConfig.tooltipLines());
    }

    public static boolean modifyTranslatableContents(Component component, UnaryOperator<Component> componentReplacer, BiPredicate<Component, UnaryOperator<Component>> contentsGatherer) {
        if (contentsGatherer.test(component, componentReplacer)) {
            return true;
        }

        if (component.getContents() instanceof TranslatableContents contents) {
            for (int i = 0; i < contents.getArgs().length; i++) {
                int index = i;

                if (contents.getArgs()[index] instanceof Component previousComponent) {
                    if (modifyTranslatableContents(previousComponent, (Component updatedComponent) -> {
                        contents.getArgs()[index] = updatedComponent;
                        return componentReplacer.apply(component);
                    }, contentsGatherer)) {
                        return true;
                    }
                }
            }
        }

        for (int i = 0; i < component.getSiblings().size(); i++) {
            int index = i;

            Component previousComponent = component.getSiblings().get(index);
            if (modifyTranslatableContents(previousComponent, (Component updatedComponent) -> {
                component.getSiblings().set(index, updatedComponent);
                return componentReplacer.apply(component);
            }, contentsGatherer)) {
                return true;
            }
        }

        return false;
    }

    public static <T> void printMissingDescriptionWarnings(ResourceKey<? extends Registry<? extends T>> registryKey, Function<Holder.Reference<T>, String> descriptionIdGetter) {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment(TooltipInsights.MOD_ID)) {
            return;
        }

        ClientPlayerNetworkEvents.JOIN.register((LocalPlayer player, MultiPlayerGameMode multiPlayerGameMode, Connection connection) -> {
            player.registryAccess().lookupOrThrow(registryKey).listElements().forEach((Holder.Reference<T> holder) -> {
                String translationKey = descriptionIdGetter.apply(holder);
                if (DescriptionLines.getDescriptionTranslationKey(translationKey) == null) {
                    TooltipInsights.LOGGER.warn("Missing description for {}: {}",
                            holder.key(),
                            translationKey + ".desc");
                }
            });
        });
    }
}
