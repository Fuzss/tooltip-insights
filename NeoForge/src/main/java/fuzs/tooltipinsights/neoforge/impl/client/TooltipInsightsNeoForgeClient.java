package fuzs.tooltipinsights.neoforge.impl.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.tooltipinsights.impl.client.TooltipInsightsClientDevelopment;
import fuzs.tooltipinsights.impl.TooltipInsights;
import fuzs.tooltipinsights.impl.client.TooltipInsightsClient;
import fuzs.tooltipinsights.impl.data.client.ModLanguageProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = TooltipInsights.MOD_ID, dist = Dist.CLIENT)
public class TooltipInsightsNeoForgeClient {

    public TooltipInsightsNeoForgeClient() {
        ClientModConstructor.construct(TooltipInsights.MOD_ID, TooltipInsightsClient::new);
        if (ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration(TooltipInsights.MOD_ID)) {
            ClientModConstructor.construct(TooltipInsights.id("client/development"),
                    TooltipInsightsClientDevelopment::new);
        }

        DataProviderHelper.registerDataProviders(TooltipInsights.MOD_ID, ModLanguageProvider::new);
    }
}
