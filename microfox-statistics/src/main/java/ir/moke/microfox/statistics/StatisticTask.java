package ir.moke.microfox.statistics;

import ir.moke.jsysbox.dev.Device;
import ir.moke.jsysbox.dev.JDevice;
import ir.moke.jsysbox.disk.Disk;
import ir.moke.jsysbox.disk.JDiskManager;
import ir.moke.jsysbox.disk.PartitionInformation;
import ir.moke.jsysbox.network.Ethernet;
import ir.moke.jsysbox.network.JNetwork;
import ir.moke.jsysbox.network.Netstat;
import ir.moke.jsysbox.network.Route;
import ir.moke.jsysbox.system.*;
import ir.moke.microfox.MicroFox;
import ir.moke.microfox.utils.HttpClientConfig;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public class StatisticTask extends TimerTask {
    @Override
    public void run() {
        send();
    }

    private static void send() {
        HttpClientConfig config = new HttpClientConfig.Builder().build();
        MicroFoxAdminAPI microFoxAdminAPI = MicroFox.httpClient(config, MicroFoxAdminAPI.class);
        StatisticsDTO statisticsDTO = generateStatisticsDTO();
        microFoxAdminAPI.statistics(statisticsDTO);
    }

    private static StatisticsDTO generateStatisticsDTO() {
        List<CpuStat> cpuStats = JSystem.cpuStats();
        CpuInfo cpuInfo = JSystem.cpuInfo();
        MemoryInfo memoryInfo = JSystem.memoryInfo();
        LoadAverage loadAverage = JSystem.loadAverage();
        List<Ulimit> ulimitList = JSystem.getAllUlimits();
        List<ModInfo> modInfos = JSystem.lsmod();
        List<Route> routes = JNetwork.route();
        List<Netstat> netstats = JNetwork.netstatIpv4();
        List<Ethernet> ethernets = JNetwork.ethernetList(false);
        List<Disk> disks = JDiskManager.getAllDiskInformation();
        List<PartitionInformation> partitions = JDiskManager.partitions();
        List<Device> devices = JDevice.scanDevices();
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        Map<String, String> sysctl = JSystem.sysctl();
        return new StatisticsDTO(cpuStats,
                cpuInfo,
                memoryInfo,
                loadAverage,
                ulimitList,
                modInfos,
                routes,
                netstats,
                ethernets,
                disks,
                partitions,
                devices,
                zonedDateTime,
                sysctl
        );
    }
}
