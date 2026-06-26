package fuzs.tooltipinsights.common.api.v1.config;

import fuzs.puzzleslib.common.api.config.v3.Config;
import fuzs.puzzleslib.common.api.config.v3.ConfigCore;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public class StyledTooltipsConfig<T extends TooltipComponentsConfig> implements ConfigCore {
    @Config(description = "Add descriptions and other useful information to tooltips.")
    public TooltipDescriptionMode tooltipDescriptions = TooltipDescriptionMode.ALWAYS;
    @Config
    public final T tooltipLines;
    @Config(description = "Formatting for setting a text color and various styles for the description component.")
    final TextFormattingConfig textFormatting = new TextFormattingConfig(TextColor.GRAY);
    @Config(description = "Apply a fixed string before every initial description line.")
    String decoration = " \u25C6 ";
    @Config(description = "Formatting for setting a text color and various styles for the decoration component.")
    final TextFormattingConfig decorationFormatting = new TextFormattingConfig(TextColor.GRAY);

    public Style textStyle;
    public Component decorationComponent;

    public StyledTooltipsConfig(T tooltipLines) {
        this.tooltipLines = tooltipLines;
    }

    @Override
    public void afterConfigReload() {
        this.textStyle = this.textFormatting.getStyle();
        this.decorationComponent = Component.literal(this.decoration).setStyle(this.decorationFormatting.getStyle());
    }
}
