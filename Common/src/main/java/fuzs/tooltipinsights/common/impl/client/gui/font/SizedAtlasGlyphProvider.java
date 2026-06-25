package fuzs.tooltipinsights.common.impl.client.gui.font;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fuzs.tooltipinsights.common.impl.network.chat.contents.objects.SizedAtlasSprite;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.*;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4fc;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

public class SizedAtlasGlyphProvider extends AtlasGlyphProvider {
    private static Map<Identifier, SizedAtlasGlyphProvider> atlasProviders = Collections.emptyMap();
    private final Map<SpriteKey, GlyphSource> wrapperCache = new Object2ObjectOpenHashMap<>();
    private final Function<SpriteKey, GlyphSource> spriteResolver = this::createSprite;

    private SizedAtlasGlyphProvider(TextureAtlas atlas) {
        super(atlas);
    }

    public static void onResourceManagerReload() {
        AtlasManager atlasManager = Minecraft.getInstance().getAtlasManager();
        ImmutableMap.Builder<Identifier, SizedAtlasGlyphProvider> builder = ImmutableMap.builder();
        atlasManager.forEach((Identifier atlasId, TextureAtlas atlasTexture) -> {
            builder.put(atlasId, new SizedAtlasGlyphProvider(atlasTexture));
        });
        atlasProviders = builder.build();
    }

    /**
     * @see FontManager#getSpriteFont(FontDescription.AtlasSprite)
     */
    public static GlyphSource getSpriteFont(SizedAtlasSprite contents, FontSet missingFontSet) {
        SizedAtlasGlyphProvider provider = atlasProviders.get(contents.atlas());
        return provider == null ? missingFontSet.source(false) :
                provider.sourceForSprite(contents.sprite(), contents.ratio());
    }

    /**
     * @see AtlasGlyphProvider#sourceForSprite(Identifier)
     */
    private GlyphSource sourceForSprite(Identifier sprite, float ratio) {
        return this.wrapperCache.computeIfAbsent(new SpriteKey(sprite, ratio), this.spriteResolver);
    }

    private GlyphSource createSprite(SpriteKey key) {
        return this.createSprite(key.sprite(), key.ratio());
    }

    /**
     * @see AtlasGlyphProvider#AtlasGlyphProvider(TextureAtlas)
     */
    private GlyphSource createSprite(Identifier id, float ratio) {
        TextureAtlasSprite sprite = this.atlas.getSprite(id);
        return sprite == this.atlas.missingSprite() ? this.missingWrapper : this.createSprite(sprite, ratio);
    }

    private GlyphSource createSprite(TextureAtlasSprite sprite, float ratio) {
        return this.createSprite(sprite, () -> GLYPH_INFO.getAdvance() * ratio, ratio);
    }

    /**
     * @see AtlasGlyphProvider#createSprite(TextureAtlasSprite)
     */
    private GlyphSource createSprite(TextureAtlasSprite sprite, GlyphInfo info, float ratio) {
        return new SingleSpriteSource(new BakedGlyph() {
            @Override
            public GlyphInfo info() {
                return info;
            }

            @Override
            public TextRenderable.Styled createGlyph(float x, float y, int color, int shadowColor, Style style, float boldOffset, float shadowOffset) {
                return new Instance(SizedAtlasGlyphProvider.this.renderTypes,
                        SizedAtlasGlyphProvider.this.atlas.getTextureView(),
                        sprite,
                        x,
                        y,
                        color,
                        shadowColor,
                        shadowOffset,
                        style,
                        ratio);
            }
        });
    }

    /**
     * @see AtlasGlyphProvider.Instance
     */
    private record Instance(GlyphRenderTypes renderTypes,
                            GpuTextureView textureView,
                            TextureAtlasSprite sprite,
                            float x,
                            float y,
                            int color,
                            int shadowColor,
                            float shadowOffset,
                            Style style,
                            float ratio) implements PlainTextRenderable {
        @Override
        public void renderSprite(Matrix4fc pose, VertexConsumer buffer, int packedLightCoords, float offsetX, float offsetY, float z, int color) {
            float x0 = offsetX + this.left();
            float x1 = offsetX + this.right();
            float y0 = offsetY + this.top();
            float y1 = offsetY + this.bottom();
            buffer.addVertex(pose, x0, y0, z)
                    .setUv(this.sprite.getU0(), this.sprite.getV0())
                    .setColor(color)
                    .setLight(packedLightCoords);
            buffer.addVertex(pose, x0, y1, z)
                    .setUv(this.sprite.getU0(), this.sprite.getV1())
                    .setColor(color)
                    .setLight(packedLightCoords);
            buffer.addVertex(pose, x1, y1, z)
                    .setUv(this.sprite.getU1(), this.sprite.getV1())
                    .setColor(color)
                    .setLight(packedLightCoords);
            buffer.addVertex(pose, x1, y0, z)
                    .setUv(this.sprite.getU1(), this.sprite.getV0())
                    .setColor(color)
                    .setLight(packedLightCoords);
        }

        @Override
        public RenderType renderType(Font.DisplayMode displayMode) {
            return this.renderTypes.select(displayMode);
        }

        @Override
        public RenderPipeline guiPipeline() {
            return this.renderTypes.guiPipeline();
        }

        @Override
        public float width() {
            return PlainTextRenderable.super.width() * this.ratio;
        }

        @Override
        public float ascent() {
            return PlainTextRenderable.super.ascent() - 1.0F;
        }
    }

    private record SpriteKey(Identifier sprite, float ratio) {

    }
}
