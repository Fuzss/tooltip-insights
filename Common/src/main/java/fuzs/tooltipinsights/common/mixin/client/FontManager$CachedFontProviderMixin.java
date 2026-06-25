package fuzs.tooltipinsights.common.mixin.client;

import fuzs.tooltipinsights.common.impl.client.gui.font.SizedAtlasGlyphProvider;
import fuzs.tooltipinsights.common.impl.client.gui.font.WidthLimitedGlyphProvider;
import fuzs.tooltipinsights.common.impl.network.chat.contents.objects.SizedAtlasSprite;
import fuzs.tooltipinsights.common.impl.network.chat.contents.objects.WidthLimitedSprite;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.network.chat.FontDescription;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.gui.font.FontManager$CachedFontProvider")
abstract class FontManager$CachedFontProviderMixin {
    @Shadow
    @Final
    FontManager this$0;

    @Inject(method = "getGlyphSource", at = @At("HEAD"), cancellable = true)
    private void getGlyphSource(FontDescription description, CallbackInfoReturnable<GlyphSource> callback) {
        if (description instanceof SizedAtlasSprite contents) {
            callback.setReturnValue(SizedAtlasGlyphProvider.getSpriteFont(contents, this.this$0.missingFontSet));
        } else if (description instanceof WidthLimitedSprite(int width)) {
            callback.setReturnValue(WidthLimitedGlyphProvider.getWidthFont(width, this.this$0.missingFontSet));
        }
    }
}
