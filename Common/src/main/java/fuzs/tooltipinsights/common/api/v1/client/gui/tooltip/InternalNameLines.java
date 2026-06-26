package fuzs.tooltipinsights.common.api.v1.client.gui.tooltip;

import fuzs.tooltipinsights.common.api.v1.config.TooltipComponentsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

import java.util.stream.Stream;

public abstract class InternalNameLines<T> extends TooltipLinesExtractor<T, TooltipComponentsConfig> {

    public InternalNameLines() {
        super(true);
    }

    @Override
    protected boolean isEnabled(TooltipComponentsConfig tooltipComponents) {
        return tooltipComponents.internalName;
    }

    @Override
    public Stream<Component> getTooltipLines(T value, int maxWidth) {
        ResourceKey<?> resourceKey = this.getResourceKey(value);
        return Stream.of(Component.literal(resourceKey.identifier().toString()).withStyle(ChatFormatting.DARK_GRAY));
    }

    protected abstract ResourceKey<?> getResourceKey(T t);
}
