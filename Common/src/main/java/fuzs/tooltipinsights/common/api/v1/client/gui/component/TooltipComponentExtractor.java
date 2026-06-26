package fuzs.tooltipinsights.common.api.v1.client.gui.component;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import java.util.stream.Stream;

public abstract class TooltipComponentExtractor<T, C> {
    private final DataComponentType<C> type;

    public TooltipComponentExtractor(DataComponentType<C> type) {
        this.type = type;
    }

    protected abstract boolean isEnabled();

    protected abstract Stream<T> extractFromComponent(C component);

    public Stream<T> extractFromItemStack(ItemStack itemStack) {
        if (this.isEnabled() && itemStack.has(this.type)) {
            C component = itemStack.get(this.type);
            return this.extractFromComponent(component);
        } else {
            return Stream.empty();
        }
    }
}
