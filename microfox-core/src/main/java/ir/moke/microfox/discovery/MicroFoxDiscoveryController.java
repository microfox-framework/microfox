package ir.moke.microfox.discovery;

import ir.moke.kafir.http.Kafir;
import ir.moke.microfox.MicrofoxEnvironment;
import ir.moke.microfox.discovery.dto.CheckDTO;
import ir.moke.microfox.discovery.dto.RegisterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpResponse;
import java.util.Optional;

public class MicroFoxDiscoveryController {
    private static final Logger logger = LoggerFactory.getLogger(MicroFoxDiscoveryController.class);
    private static final String baseURL = "http://%s:%s";

    private static ServiceDiscovery getServiceDiscoveryAPI() {
        String discoveryHost = MicrofoxEnvironment.getEnv("MICROFOX_SERVICE_DISCOVERY_HOST");
        String discoveryPort = MicrofoxEnvironment.getEnv("MICROFOX_SERVICE_DISCOVERY_PORT");
        if (discoveryHost == null || discoveryPort == null) return null;
        return new Kafir.KafirBuilder()
                .setBaseUri(baseURL.formatted(discoveryHost, discoveryPort))
                .build(ServiceDiscovery.class);
    }

    public static void register() {
        ServiceDiscovery serviceDiscovery = getServiceDiscoveryAPI();
        if (serviceDiscovery == null) return;

        String host = Optional.ofNullable(MicrofoxEnvironment.getEnv("MICROFOX_HEALTH_CHECK_HOST")).orElse("192.168.1.100");
        String port = Optional.ofNullable(MicrofoxEnvironment.getEnv("MICROFOX_HEALTH_CHECK_PORT")).orElse("9091");
        String interval = Optional.ofNullable(MicrofoxEnvironment.getEnv("MICROFOX_HEALTH_CHECK_INTERVAL")).orElse("5");

        CheckDTO checkDTO = new CheckDTO("http://%s:%s/health".formatted(host, port), interval + "s");
        RegisterDTO registerDTO = new RegisterDTO("1", "test", host, Integer.parseInt(port), checkDTO);
        HttpResponse<String> response = serviceDiscovery.register(registerDTO);
        if (response.statusCode() != 200) logger.warn("Service discovery registry failed, {}", response.body());
    }
}