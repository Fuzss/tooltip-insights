package fuzs.tooltipinsights.api.v1.client.gui.tooltip;

import fuzs.puzzleslib.api.core.v1.ModContainer;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.tooltipinsights.api.v1.config.TooltipComponentsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

import java.util.stream.Stream;

public abstract class ModNameLines<T> extends TooltipLinesExtractor<T, TooltipComponentsConfig> {

    public ModNameLines() {
        super(true);
    }

    @Override
    protected boolean isEnabled(TooltipComponentsConfig tooltipComponents) {
        return tooltipComponents.modName;
    }

    @Override
    public Stream<Component> getTooltipLines(T value, int maxWidth) {
        ResourceKey<?> resourceKey = this.getResourceKey(value);
        return ModLoaderEnvironment.INSTANCE.getModContainer(resourceKey.location().getNamespace())
                .map(ModContainer::getDisplayName)
                .<Component>map((String string) -> Component.literal(string).withStyle(ChatFormatting.BLUE))
                .stream();
    }

    protected abstract ResourceKey<?> getResourceKey(T t);
}
