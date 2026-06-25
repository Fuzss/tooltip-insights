package fuzs.tooltipinsights.common.api.v1.client.gui.tooltip;

import fuzs.tooltipinsights.common.api.v1.config.AbstractClientConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

import java.util.stream.Stream;

public abstract class InternalNameLines<T> extends TooltipLinesExtractor<T, AbstractClientConfig.TooltipComponents> {

    public InternalNameLines() {
        super(true);
    }

    @Override
    protected boolean isEnabled(AbstractClientConfig.TooltipComponents tooltipComponents) {
        return tooltipComponents.internalName;
    }

    @Override
    protected Stream<Component> getTooltipLines(T t, int maxWidth) {
        ResourceKey<?> resourceKey = this.getResourceKey(t);
        return Stream.of(Component.literal(resourceKey.identifier().toString()).withStyle(ChatFormatting.DARK_GRAY));
    }

    protected abstract ResourceKey<?> getResourceKey(T t);
}
