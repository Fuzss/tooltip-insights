package fuzs.tooltipinsights.api.v1.config;

import fuzs.puzzleslib.api.config.v3.Config;
import fuzs.puzzleslib.api.config.v3.ConfigCore;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class StyledTooltipsConfig<T extends TooltipComponentsConfig> implements ConfigCore {
    @Config(description = "Add descriptions and other useful information to tooltips.")
    public TooltipDescriptionMode tooltipDescriptions = TooltipDescriptionMode.ALWAYS;
    @Config(description = "Add a note on how to show descriptions when they are hidden.")
    public boolean tooltipDescriptionsHint = true;
    @Config
    public final T tooltipLines;
    @Config(description = "Formatting for setting a text color and various styles for the description component.")
    final TextFormattingConfig descriptionFormatting = new TextFormattingConfig(ChatFormatting.GRAY);
    @Config(description = "Apply a fixed string before every initial description line.")
    String descriptionDecoration = " \u25C6 ";

    public Style descriptionStyle;
    public Component descriptionDecorationComponent;

    public StyledTooltipsConfig(T tooltipLines) {
        this.tooltipLines = tooltipLines;
    }

    @Override
    public void afterConfigReload() {
        this.descriptionStyle = this.descriptionFormatting.getStyle();
        this.descriptionDecorationComponent = Component.literal(this.descriptionDecoration);
    }
}
