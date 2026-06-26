package fuzs.tooltipinsights.common.api.v1.client.gui.tooltip;

import fuzs.tooltipinsights.common.api.v1.config.StyledTooltipsConfig;
import fuzs.tooltipinsights.common.api.v1.config.TooltipComponentsConfig;
import fuzs.tooltipinsights.common.impl.network.chat.contents.objects.WidthLimitedSprite;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public abstract class TooltipLinesExtractor<T, C extends TooltipComponentsConfig> {
    private final boolean supportsDecorations;

    public TooltipLinesExtractor(boolean supportsDecorations) {
        this.supportsDecorations = supportsDecorations;
    }

    public static <T, C extends TooltipComponentsConfig> List<Component> getTooltipLines(List<TooltipLinesExtractor<T, C>> extractorList, T value, StyledTooltipsConfig<C> config) {
        return TooltipLinesExtractor.getTooltipLines(extractorList,
                config.descriptionDecorationComponent,
                config.descriptionStyle,
                value,
                config.tooltipLines);
    }

    public static <T, C extends TooltipComponentsConfig> List<Component> getTooltipLines(List<TooltipLinesExtractor<T, C>> extractorList, Component decorationComponent, Style style, T value, C tooltipComponents) {
        Font font = Minecraft.getInstance().font;
        Component indentComponent = Component.object(new WidthLimitedSprite(font.width(decorationComponent)));
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
