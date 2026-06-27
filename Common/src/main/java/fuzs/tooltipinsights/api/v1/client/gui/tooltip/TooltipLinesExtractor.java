package fuzs.tooltipinsights.api.v1.client.gui.tooltip;

import fuzs.tooltipinsights.api.v1.config.TooltipComponentsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public abstract class TooltipLinesExtractor<T, C extends TooltipComponentsConfig> {
    private final boolean supportsDecorations;

    public TooltipLinesExtractor(boolean supportsDecorations) {
        this.supportsDecorations = supportsDecorations;
    }

    /**
     * @see net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil#BACKGROUND_COLOR
     */
    public static <T, C extends TooltipComponentsConfig> List<Component> getTooltipLines(List<TooltipLinesExtractor<T, C>> extractorList, Component decorationComponent, Style style, T value, C tooltipComponents) {
        // This works much better in 1.21.9+ with the custom object info component which can represent an arbitrary width,
        // but here this seems like an ok workaround.
        Component plainComponent = Component.literal(ChatFormatting.stripFormatting(decorationComponent.getString()));
        Component indentComponent = modifyAllStyles(plainComponent, (Style updatedStyle) -> {
            // This is the exact color of the tooltip background.
            return updatedStyle.withColor(0xF0100010);
        });
        MutableBoolean isMissingDecoration = new MutableBoolean(true);
        List<Component> tooltipLines = new ArrayList<>();

        for (TooltipLinesExtractor<T, C> extractor : extractorList) {
            List<Component> list = extractor.getTooltipLines(tooltipComponents, value).toList();

            if (extractor.supportsDecorations) {
                for (Component tooltipLine : list) {
                    Component component;

                    if (isMissingDecoration.isTrue()) {
                        isMissingDecoration.setFalse();
                        component = decorationComponent;
                    } else {
                        component = indentComponent;
                    }

                    tooltipLines.add(Component.empty().append(component).append(tooltipLine).withStyle(style));
                }
            } else {
                tooltipLines.addAll(list);
            }
        }

        return tooltipLines;
    }

    private static Component modifyAllStyles(Component component, UnaryOperator<Style> modifier) {
        MutableComponent updatedComponent = component.copy();
        updatedComponent.withStyle(modifier);
        updatedComponent.getSiblings().replaceAll((Component sibling) -> {
            return modifyAllStyles(sibling, modifier);
        });
        return updatedComponent;
    }

    protected abstract boolean isEnabled(C tooltipComponents);

    public abstract Stream<Component> getTooltipLines(T value, int maxWidth);

    public final Stream<Component> getTooltipLines(C tooltipComponents, T value) {
        if (this.isEnabled(tooltipComponents)) {
            return this.getTooltipLines(value, tooltipComponents.maximumWidth);
        } else {
            return Stream.empty();
        }
    }

    public <E extends TooltipLinesExtractor<?, ?>> E cast() {
        return (E) this;
    }
}
