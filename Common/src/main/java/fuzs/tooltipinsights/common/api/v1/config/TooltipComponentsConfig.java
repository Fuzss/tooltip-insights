package fuzs.tooltipinsights.common.api.v1.config;

import fuzs.puzzleslib.common.api.config.v3.Config;
import fuzs.puzzleslib.common.api.config.v3.ConfigCore;

public class TooltipComponentsConfig implements ConfigCore {
    @Config(description = "The maximum line width in pixels.")
    @Config.IntRange(min = 25)
    public int maximumWidth = 175;
    @Config(description = "Add the description to tooltips.")
    public boolean valueDescription = true;
    @Config(description = "Add the name of the source mod to tooltips.")
    public boolean modName = false;
    @Config(description = "Add the internal id to tooltips.")
    public boolean internalName = false;
}
