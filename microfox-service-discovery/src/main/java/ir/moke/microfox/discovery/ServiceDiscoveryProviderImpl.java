package ir.moke.microfox.discovery;

import ir.moke.kafir.http.Kafir;
import ir.moke.microfox.MicrofoxEnvironment;
import ir.moke.microfox.api.discovery.ServiceDiscoveryProvider;
import ir.moke.microfox.discovery.dto.CheckDTO;
import ir.moke.microfox.discovery.dto.RegisterDTO;
import ir.moke.microfox.utils.TtyAsciiCodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpResponse;

public class ServiceDiscoveryProviderImpl implements ServiceDiscoveryProvider, TtyAsciiCodecs {
    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscoveryProviderImpl.class);
    private static final String baseURL = "http://%s:%s";

    private static ServiceDiscovery getServiceDiscoveryAPI() {
        String discoveryHost = MicrofoxEnvironment.getEnv("MICROFOX_SERVICE_DISCOVERY_HOST");
        String discoveryPort = MicrofoxEnvironment.getEnv("MICROFOX_SERVICE_DISCOVERY_PORT");
        if (discoveryHost == null || discoveryPort == null) {
            logger.warn("Service discovery environment variables is empty: [MICROFOX_SERVICE_DISCOVERY_HOST , MICROFOX_SERVICE_DISCOVERY_HOST]");
            return null;
        }
        return new Kafir.KafirBuilder()
                .setBaseUri(baseURL.formatted(discoveryHost, discoveryPort))
                .build(ServiceDiscovery.class);
    }

    @Override
    public void registerServiceDiscovery() {
        ServiceDiscovery serviceDiscovery = getServiceDiscoveryAPI();
        if (serviceDiscovery == null) return;

        String host = MicrofoxEnvironment.getEnv("MICROFOX_HEALTH_CHECK_HOST");
        String port = MicrofoxEnvironment.getEnv("MICROFOX_HEALTH_CHECK_PORT");
        String interval = MicrofoxEnvironment.getEnv("MICROFOX_HEALTH_CHECK_INTERVAL");
        String path = MicrofoxEnvironment.getEnv("MICROFOX_HEALTH_API_PATH");

        CheckDTO checkDTO = new CheckDTO("http://%s:%s/%s".formatted(host, port, path), interval + "s");
        RegisterDTO registerDTO = new RegisterDTO("1", "test", host, Integer.parseInt(port), checkDTO);
        HttpResponse<String> response = serviceDiscovery.register(registerDTO);
        if (response.statusCode() != 200) logger.warn("Service discovery registry failed, {}", response.body());
        logger.info("{}{}{}", BACKGROUND_BLUE, "Service Discovery Activated", RESET);
    }
}