package fuzs.tooltipinsights.common.api.v1.client.gui.tooltip;

import fuzs.puzzleslib.common.api.core.v1.ModContainer;
import fuzs.puzzleslib.common.api.core.v1.ModLoaderEnvironment;
import fuzs.tooltipinsights.common.api.v1.config.AbstractClientConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

import java.util.stream.Stream;

public abstract class ModNameLines<T> extends TooltipLinesExtractor<T, AbstractClientConfig.TooltipComponents> {

    public ModNameLines() {
        super(true);
    }

    @Override
    protected boolean isEnabled(AbstractClientConfig.TooltipComponents tooltipComponents) {
        return tooltipComponents.modName;
    }

    @Override
    protected Stream<Component> getTooltipLines(T t, int maxWidth) {
        ResourceKey<?> resourceKey = this.getResourceKey(t);
        return ModLoaderEnvironment.INSTANCE.getModContainer(resourceKey.identifier().getNamespace())
                .map(ModContainer::getDisplayName)
                .<Component>map((String string) -> Component.literal(string).withStyle(ChatFormatting.BLUE))
                .stream();
    }

    protected abstract ResourceKey<?> getResourceKey(T t);
}
