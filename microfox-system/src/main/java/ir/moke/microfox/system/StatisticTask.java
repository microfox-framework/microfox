package ir.moke.microfox.system;

import ir.moke.microfox.MicroFox;
import ir.moke.microfox.MicrofoxEnvironment;
import ir.moke.microfox.utils.HttpClientConfig;

import java.util.TimerTask;

public class StatisticTask extends TimerTask {
    @Override
    public void run() {
        send();
    }

    public static void send() {
        HttpClientConfig config = new HttpClientConfig.Builder()
                .setBaseUri(MicrofoxEnvironment.getEnv("microfox.admin.base-url"))
                .build();
        MicroFoxAdminAPI microFoxAdminAPI = MicroFox.httpClient(config, MicroFoxAdminAPI.class);
        SystemDTO systemDTO = generateSystemDTO();
        microFoxAdminAPI.statistics(systemDTO);
    }

    private static SystemDTO generateSystemDTO() {
        return new SystemDTO(
                SystemInformation.pid(),
                SystemInformation.threads(),
                SystemInformation.isContainer(),
                SystemInformation.hostname(),
                SystemInformation.environments(),
                SystemInformation.dnsNameServers(),
                SystemInformation.hosts(),
                SystemInformation.jvmVendor(),
                SystemInformation.jvmVersion(),
                SystemInformation.jvmMaxHeapSize(),
                SystemInformation.jvmTotalHeapSize(),
                SystemInformation.jvmUsedHeapSize(),
                SystemInformation.ulimit(),
                SystemInformation.lsmod(),
                SystemInformation.routes(),
                SystemInformation.netstat(),
                SystemInformation.ethernets(),
                SystemInformation.disks(),
                SystemInformation.partitions(),
                SystemInformation.devices(),
                SystemInformation.zonedDateTime(),
                SystemInformation.sysctl()
        );
    }
}
