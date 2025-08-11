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
        String discoveryHost = MicrofoxEnvironment.getEnv("microfox.service.discovery.host");
        String discoveryPort = MicrofoxEnvironment.getEnv("microfox.service.discovery.port");
        if (discoveryHost == null || discoveryPort == null) {
            logger.warn("Service discovery environment variables is empty: [microfox.service.discovery.host , microfox.service.discovery.host]");
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

        String host = MicrofoxEnvironment.getEnv("microfox.health.check.host");
        String port = MicrofoxEnvironment.getEnv("microfox.health.check.port");
        String interval = MicrofoxEnvironment.getEnv("microfox.health.check.interval");
        String path = MicrofoxEnvironment.getEnv("microfox.health.api.path");

        CheckDTO checkDTO = new CheckDTO("http://%s:%s/%s".formatted(host, port, path), interval + "s");
        RegisterDTO registerDTO = new RegisterDTO("1", "test", host, Integer.parseInt(port), checkDTO);
        HttpResponse<String> response = serviceDiscovery.register(registerDTO);
        if (response.statusCode() != 200) logger.warn("Service discovery registration failed, {}", response.body());
        logger.info("{}{}{}", BACKGROUND_BLUE, "Service Discovery Activated", RESET);
    }
}