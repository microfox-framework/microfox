package ir.moke.microfox.system;

import ir.moke.microfox.MicroFox;
import ir.moke.microfox.MicroFoxEnvironment;
import ir.moke.microfox.api.system.SystemProvider;
import ir.moke.microfox.utils.HttpClientConfig;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SystemProviderImpl implements SystemProvider {
    private static final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    private static final SystemDTO SYSTEM_DTO = new SystemDTO();

    static {
        onShutdown();
    }

    @Override
    public void activate() {
        boolean isActive = Boolean.parseBoolean(MicroFoxEnvironment.getEnv("microfox.system.active"));
        if (isActive) {
            ses.scheduleAtFixedRate(this::run, 0, 30, TimeUnit.SECONDS);
        }
    }

    public synchronized void run() {
        generateSystemDTO();
        send();
    }

    public static void send() {
        HttpClientConfig config = new HttpClientConfig.Builder()
                .setBaseUri(MicroFoxEnvironment.getEnv("microfox.admin.base-url"))
                .build();
        MicroFoxAdminAPI microFoxAdminAPI = MicroFox.httpClient(config, MicroFoxAdminAPI.class);
        microFoxAdminAPI.statistics(SYSTEM_DTO);
    }

    private static void generateSystemDTO() {
        SYSTEM_DTO.setPid(SystemInformation.pid());
        SYSTEM_DTO.setThreads(SystemInformation.threads());
        SYSTEM_DTO.setContainer(SystemInformation.isContainer());
        SYSTEM_DTO.setHostname(SystemInformation.hostname());
        SYSTEM_DTO.setEnvironments(SystemInformation.environments());
        SYSTEM_DTO.setNameservers(SystemInformation.dnsNameServers());
        SYSTEM_DTO.setHosts(SystemInformation.hosts());
        SYSTEM_DTO.setJvmVendor(SystemInformation.jvmVendor());
        SYSTEM_DTO.setJvmVersion(SystemInformation.jvmVersion());
        SYSTEM_DTO.setJvmMaxHeapSize(SystemInformation.jvmMaxHeapSize());
        SYSTEM_DTO.setJvmTotalHeapSize(SystemInformation.jvmTotalHeapSize());
        SYSTEM_DTO.setJvmUsedHeapSize(SystemInformation.jvmUsedHeapSize());
        SYSTEM_DTO.setUlimits(SystemInformation.ulimit());
        SYSTEM_DTO.setModInfos(SystemInformation.lsmod());
        SYSTEM_DTO.setRoutes(SystemInformation.routes());
        SYSTEM_DTO.setNetstat(SystemInformation.netstat());
        SYSTEM_DTO.setEthernets(SystemInformation.ethernets());
        SYSTEM_DTO.setDisks(SystemInformation.disks());
        SYSTEM_DTO.setPartitions(SystemInformation.partitions());
        SYSTEM_DTO.setDevices(SystemInformation.devices());
        SYSTEM_DTO.setDateTime(SystemInformation.zonedDateTime());
        SYSTEM_DTO.setSysctl(SystemInformation.sysctl());
        SYSTEM_DTO.setClassInstance(SystemInformation.instanceList());
    }

    private static void onShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(ses::shutdown));
    }
}
