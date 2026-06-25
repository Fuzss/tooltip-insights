package fuzs.tooltipinsights.common.impl;

import fuzs.puzzleslib.common.api.core.v1.ModConstructor;
import fuzs.tooltipinsights.common.impl.network.chat.contents.objects.SizedAtlasSprite;
import fuzs.tooltipinsights.common.impl.network.chat.contents.objects.WidthLimitedSprite;
import net.minecraft.network.chat.contents.objects.ObjectInfos;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TooltipInsights implements ModConstructor {
    public static final String MOD_ID = "tooltipinsights";
    public static final String MOD_NAME = "Tooltip Insights";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onCommonSetup() {
        ObjectInfos.ID_MAPPER.put(id("atlas").toString(), SizedAtlasSprite.MAP_CODEC);
        ObjectInfos.ID_MAPPER.put(id("width_limited").toString(), WidthLimitedSprite.MAP_CODEC);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
