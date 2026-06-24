package fuzs.tooltipinsights.api.v1.client.handler;

import fuzs.puzzleslib.api.client.event.v1.entity.player.ClientPlayerNetworkEvents;
import fuzs.tooltipinsights.api.v1.client.gui.tooltip.DescriptionLines;
import fuzs.tooltipinsights.api.v1.config.TooltipDescriptionMode;
import fuzs.tooltipinsights.impl.TooltipInsights;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public abstract class TooltipDescriptionsHandler<T> {

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
        TooltipDescriptionMode tooltipDescriptionMode = this.getTooltipDescriptionMode();

        if (!tooltipDescriptionMode.isActive()) {
            return;
        }

        Map<String, T> descriptionIds = this.getByDescriptionId(itemStack, registries);

        if (!descriptionIds.isEmpty()) {
            MutableBoolean mutableBoolean = new MutableBoolean(true);

            for (MutableInt mutableInt = new MutableInt();
                 mutableInt.intValue() < tooltipLines.size(); mutableInt.increment()) {

                modifyTranslatableContents(tooltipLines.get(mutableInt.intValue()),
                        UnaryOperator.identity(),
                        (TranslatableContents translatableContents, UnaryOperator<Component> componentReplacer) -> {

                            if (descriptionIds.containsKey(translatableContents.getKey())) {
                                T t = descriptionIds.get(translatableContents.getKey());
                                Component component = this.getValueComponent(t);

                                if (component != null) {
                                    tooltipLines.set(mutableInt.intValue(), componentReplacer.apply(component));
                                }

                                if (tooltipDescriptionMode.isActive()) {
                                    List<Component> list = this.getItemTooltipLines(t);
                                    tooltipLines.addAll(mutableInt.intValue() + 1, list);
                                    mutableInt.add(list.size());
                                    return true;
                                } else if (mutableBoolean.isTrue()) {
                                    // make sure the view description line is only added when there will actually be a description
                                    mutableBoolean.setFalse();
                                    tooltipDescriptionMode.processTooltipLines(itemStack, tooltipLines, tooltipFlag);
                                    return true;
                                }
                            }

                            return false;
                        });
            }
        }
    }

    protected abstract TooltipDescriptionMode getTooltipDescriptionMode();

    protected abstract Map<String, T> getByDescriptionId(ItemStack itemStack, HolderLookup.Provider registries);

    @Nullable
    protected Component getValueComponent(T t) {
        return null;
    }

    protected abstract List<Component> getItemTooltipLines(T t);

    public static boolean modifyTranslatableContents(Component component, UnaryOperator<Component> componentReplacer, BiPredicate<TranslatableContents, UnaryOperator<Component>> contentsGatherer) {
        if (component.getContents() instanceof TranslatableContents translatableContents) {
            if (contentsGatherer.test(translatableContents, componentReplacer)) {
                return true;
            } else {
                for (int i = 0; i < translatableContents.getArgs().length; i++) {
                    int index = i;

                    if (translatableContents.getArgs()[index] instanceof Component componentArg) {
                        if (modifyTranslatableContents(componentArg, (Component componentX) -> {
                            translatableContents.getArgs()[index] = componentX;
                            return componentReplacer.apply(component);
                        }, contentsGatherer)) {
                            return true;
                        }
                    }
                }
            }
        }

        for (int i = 0; i < component.getSiblings().size(); i++) {
            int index = i;

            if (modifyTranslatableContents(component.getSiblings().get(index), (Component componentX) -> {
                component.getSiblings().set(index, componentX);
                return componentReplacer.apply(component);
            }, contentsGatherer)) {
                return true;
            }
        }

        return false;
    }

    public static <T> void printMissingDescriptionWarnings(ResourceKey<? extends Registry<? extends T>> registryKey, Function<Holder.Reference<T>, String> descriptionIdGetter) {
        ClientPlayerNetworkEvents.LOGGED_IN.register((LocalPlayer player, MultiPlayerGameMode multiPlayerGameMode, Connection connection) -> {
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
