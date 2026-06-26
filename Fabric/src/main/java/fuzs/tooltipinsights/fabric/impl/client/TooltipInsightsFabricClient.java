package fuzs.tooltipinsights.fabric.impl.client;

import fuzs.puzzleslib.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.tooltipinsights.common.impl.client.TooltipInsightsClientDevelopment;
import fuzs.tooltipinsights.impl.TooltipInsights;
import fuzs.tooltipinsights.impl.client.TooltipInsightsClient;
import net.fabricmc.api.ClientModInitializer;

public class TooltipInsightsFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(TooltipInsights.MOD_ID, TooltipInsightsClient::new);
        if (ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration(TooltipInsights.MOD_ID)) {
            ClientModConstructor.construct(TooltipInsights.id("client/development"),
                    TooltipInsightsClientDevelopment::new);
        }
    }
}
