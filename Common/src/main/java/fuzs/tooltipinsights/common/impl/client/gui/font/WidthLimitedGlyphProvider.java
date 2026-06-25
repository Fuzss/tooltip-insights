package fuzs.tooltipinsights.common.impl.client.gui.font;

import com.mojang.blaze3d.font.GlyphInfo;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.*;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

import java.util.function.IntFunction;

public final class WidthLimitedGlyphProvider {
    @Nullable
    private static WidthLimitedGlyphProvider instance;
    private final Int2ObjectMap<GlyphSource> wrapperCache = new Int2ObjectArrayMap<>();
    private final IntFunction<GlyphSource> widthResolver = this::createWidth;

    private WidthLimitedGlyphProvider() {
        // NO-OP
    }

    public static void onResourceManagerReload() {
        instance = new WidthLimitedGlyphProvider();
    }

    /**
     * @see FontManager#getSpriteFont(FontDescription.AtlasSprite)
     */
    public static GlyphSource getWidthFont(int width, FontSet missingFontSet) {
        WidthLimitedGlyphProvider provider = instance;
        return provider == null ? missingFontSet.source(false) : provider.sourceForWidth(width);
    }

    /**
     * @see AtlasGlyphProvider#sourceForSprite(Identifier)
     */
    private GlyphSource sourceForWidth(int width) {
        return this.wrapperCache.computeIfAbsent(width, this.widthResolver);
    }

    private GlyphSource createWidth(int width) {
        return createWidth(GlyphInfo.simple(width));
    }

    /**
     * @see AtlasGlyphProvider#createSprite(TextureAtlasSprite)
     */
    private static GlyphSource createWidth(GlyphInfo info) {
        return new SingleSpriteSource(new BakedGlyph() {
            @Override
            public GlyphInfo info() {
                return info;
            }

            @Override
            public TextRenderable.@Nullable Styled createGlyph(float x, float y, int color, int shadowColor, Style style, float boldOffset, float shadowOffset) {
                return null;
            }
        });
    }
}
