package fuzs.tooltipinsights.api.v1.client.gui.tooltip;

import fuzs.puzzleslib.api.client.gui.v2.tooltip.ClientComponentSplitter;
import fuzs.puzzleslib.api.util.v1.ComponentHelper;
import fuzs.tooltipinsights.api.v1.config.TooltipComponentsConfig;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public abstract class DescriptionLines<T> extends TooltipLinesExtractor<T, TooltipComponentsConfig> {

    public DescriptionLines() {
        super(true);
    }

    @Override
    protected final boolean isEnabled(TooltipComponentsConfig tooltipComponents) {
        return tooltipComponents.valueDescription;
    }

    @Override
    public Stream<Component> getTooltipLines(T value, int maxWidth) {
        String descriptionKey = getDescriptionTranslationKey(this.getDescriptionId(value));
        if (descriptionKey != null) {
            return ClientComponentSplitter.splitTooltipLines(Component.translatable(descriptionKey))
                    .map(ComponentHelper::getAsComponent);
        } else {
            return Stream.empty();
        }
    }

    public static @Nullable String getDescriptionTranslationKey(String translationKey) {
        if (Language.getInstance().has(translationKey + ".desc")) {
            // our own format, similar to Enchantment Descriptions mod format
            return translationKey + ".desc";
        } else if (Language.getInstance().has(translationKey + ".description")) {
            // Just Enough Effect Descriptions mod format
            return translationKey + ".description";
        } else if (Language.getInstance().has("description." + translationKey)) {
            // Potion Descriptions mod format
            return "description." + translationKey;
        } else {
            return null;
        }
    }

    protected abstract String getDescriptionId(T value);
}
