package fuzs.tooltipinsights.common.impl.client;

import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.common.api.client.core.v1.context.ResourcePackReloadListenersContext;
import fuzs.tooltipinsights.common.impl.TooltipInsights;
import fuzs.tooltipinsights.common.impl.client.gui.font.SizedAtlasGlyphProvider;
import fuzs.tooltipinsights.common.impl.client.gui.font.WidthLimitedGlyphProvider;
import net.minecraft.server.packs.resources.ResourceManager;

public class TooltipInsightsClient implements ClientModConstructor {

    @Override
    public void onAddResourcePackReloadListeners(ResourcePackReloadListenersContext context) {
        context.registerReloadListener(TooltipInsights.id("glyph_providers"), (ResourceManager resourceManager) -> {
            SizedAtlasGlyphProvider.onResourceManagerReload();
            WidthLimitedGlyphProvider.onResourceManagerReload();
        });
    }
}
