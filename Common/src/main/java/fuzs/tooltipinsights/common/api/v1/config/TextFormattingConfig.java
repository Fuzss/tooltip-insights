package fuzs.tooltipinsights.common.api.v1.config;

import com.google.common.collect.Sets;
import fuzs.puzzleslib.common.api.config.v3.Config;
import fuzs.puzzleslib.common.api.config.v3.ConfigCore;
import fuzs.puzzleslib.common.api.config.v3.ValueCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.TriState;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @see net.minecraft.ChatFormatting
 */
public class TextFormattingConfig implements ConfigCore {
    private static final Set<ChatFormatting> TEXT_COLORS = Stream.of(ChatFormatting.values())
            .filter((ChatFormatting formatting) -> {
                return TextColor.fromLegacyFormat(formatting) != null;
            })
            .collect(Sets.toImmutableEnumSet());

    @Config(description = "Should text appear colored.")
    public boolean colored = true;
    public ChatFormatting color;
    @Config(description = "Should text appear bold.")
    public TriState bold = TriState.DEFAULT;
    @Config(description = "Should text appear struck-through.")
    public TriState strikethrough = TriState.DEFAULT;
    @Config(description = "Should text appear with an underline.")
    public TriState underline = TriState.DEFAULT;
    @Config(description = "Should text appear italic.")
    public TriState italic = TriState.DEFAULT;

    public TextFormattingConfig() {
        this(false, ChatFormatting.WHITE);
    }

    public TextFormattingConfig(ChatFormatting color) {
        this(true, color.isColor() ? color : null);
    }

    private TextFormattingConfig(boolean colored, ChatFormatting color) {
        Objects.requireNonNull(color, "color is null");
        this.colored = colored;
        this.color = color;
    }

    @Override
    public void addToBuilder(ModConfigSpec.Builder builder, ValueCallback callback) {
        callback.accept(builder.comment("The text color.").defineEnum("color", this.color, TEXT_COLORS),
                v -> this.color = v);
    }

    public Style getStyle() {
        Style style = Style.EMPTY;
        if (this.colored) {
            style = style.withColor(this.color);
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
