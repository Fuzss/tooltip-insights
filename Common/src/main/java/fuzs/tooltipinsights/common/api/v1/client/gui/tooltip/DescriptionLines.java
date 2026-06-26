package fuzs.tooltipinsights.common.api.v1.client.gui.tooltip;

import fuzs.puzzleslib.common.api.client.gui.v2.tooltip.ClientComponentSplitter;
import fuzs.puzzleslib.common.api.util.v1.ComponentHelper;
import fuzs.tooltipinsights.common.api.v1.config.TooltipComponentsConfig;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.stream.Stream;

public abstract class DescriptionLines<T> extends TooltipLinesExtractor<T, TooltipComponentsConfig> {

    public DescriptionLines() {
        super(true);
    }

    @Override
    protected boolean isEnabled(TooltipComponentsConfig tooltipComponents) {
        return tooltipComponents.valueDescription;
    }

    @Override
    public Stream<Component> getTooltipLines(T value, int maxWidth) {
        String descriptionKey = getDescriptionTranslationKey(this.getDescriptionId(value));
        if (descriptionKey != null) {
            return ClientComponentSplitter.splitTooltipLines(maxWidth, Component.translatable(descriptionKey))
                    .map(ComponentHelper::getAsComponent);
        } else {
            return Stream.empty();
        }
    }

    public static @Nullable String getDescriptionTranslationKey(String translationKey) {
        if (Language.getInstance().has(translationKey + ".desc")) {
            // The default format established by the popular Enchantment Descriptions mod.
            return translationKey + ".desc";
        } else if (Language.getInstance().has(translationKey + ".description")) {
            // An alternative format from the Just Enough Effect Descriptions mod.
            return translationKey + ".description";
        } else if (Language.getInstance().has("description." + translationKey)) {
            // The old Potion Descriptions mod format.
            return "description." + translationKey;
        } else {
            return null;
        }
    }

    protected abstract String getDescriptionId(T t);
}
