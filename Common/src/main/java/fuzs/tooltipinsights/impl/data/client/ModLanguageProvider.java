package fuzs.tooltipinsights.impl.data.client;

import fuzs.puzzleslib.api.client.data.v2.AbstractLanguageProvider;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.tooltipinsights.common.api.v1.config.TooltipDescriptionMode;

public class ModLanguageProvider extends AbstractLanguageProvider {

    public ModLanguageProvider(DataProviderContext context) {
        super(context);
    }

    @Override
    public void addTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add(TooltipDescriptionMode.VIEW_DESCRIPTIONS_KEY, "Hold %s to view descriptions.");
    }
}
