package fuzs.tooltipinsights.common.api.v1.config;

import fuzs.puzzleslib.common.api.config.v3.Config;
import fuzs.puzzleslib.common.api.config.v3.ConfigCore;
import fuzs.puzzleslib.common.api.config.v3.ValueCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.TriState;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @see net.minecraft.ChatFormatting
 */
public class TextFormattingConfig implements ConfigCore {
    @Config(description = "Should text appear colored.")
    public boolean colored;
    public String color;
    @Config(description = "Should text appear bold.")
    public TriState bold = TriState.DEFAULT;
    @Config(description = "Should text appear struck-through.")
    public TriState strikethrough = TriState.DEFAULT;
    @Config(description = "Should text appear with an underline.")
    public TriState underline = TriState.DEFAULT;
    @Config(description = "Should text appear italic.")
    public TriState italic = TriState.DEFAULT;

    private TextColor textColor;

    public TextFormattingConfig() {
        this(false, ChatFormatting.WHITE);
    }

    public TextFormattingConfig(ChatFormatting color) {
        this(true, color);
    }

    private TextFormattingConfig(boolean colored, ChatFormatting color) {
        TextColor textColor = TextColor.fromLegacyFormat(color);
        Objects.requireNonNull(textColor, "color is null");
        this.colored = colored;
        this.color = textColor.serialize();
    }

    @Override
    public void addToBuilder(ModConfigSpec.Builder builder, ValueCallback callback) {
        callback.accept(builder.comment("The text color. Must be enabled separately.",
                "Allowed Values: " + Stream.of(ChatFormatting.values())
                        .map(TextColor::fromLegacyFormat)
                        .filter(Objects::nonNull)
                        .map(TextColor::serialize)
                        .collect(Collectors.joining(", "))).define("text_color", this.color, o -> {
            return o instanceof String s && TextColor.parseColor(s).isSuccess();
        }), v -> this.color = v);
    }

    @Override
    public void afterConfigReload() {
        this.textColor = TextColor.parseColor(this.color).getOrThrow();
    }

    public Style getStyle() {
        Style style = Style.EMPTY;
        if (this.colored) {
            style = style.withColor(this.textColor);
        }

        if (this.bold != TriState.DEFAULT) {
            style = style.withBold(this.bold.toBoolean(false));
        }

        if (this.strikethrough != TriState.DEFAULT) {
            style = style.withStrikethrough(this.strikethrough.toBoolean(false));
        }

        if (this.underline != TriState.DEFAULT) {
            style = style.withUnderlined(this.underline.toBoolean(false));
        }

        if (this.italic != TriState.DEFAULT) {
            style = style.withItalic(this.italic.toBoolean(false));
        }

        return style;
    }
}
