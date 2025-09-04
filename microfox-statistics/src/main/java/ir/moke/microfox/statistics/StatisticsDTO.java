package ir.moke.microfox.statistics;

import ir.moke.jsysbox.dev.Device;
import ir.moke.jsysbox.disk.Disk;
import ir.moke.jsysbox.disk.PartitionInformation;
import ir.moke.jsysbox.network.Ethernet;
import ir.moke.jsysbox.network.Netstat;
import ir.moke.jsysbox.network.Route;
import ir.moke.jsysbox.system.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public record StatisticsDTO(List<CpuStat> cpuStats,
                            CpuInfo cpuInfo,
                            MemoryInfo memoryInfo,
                            LoadAverage loadAverage,
                            List<Ulimit> ulimits,
                            List<ModInfo> modInfos,
                            List<Route> routes,
                            List<Netstat> netstat,
                            List<Ethernet> ethernets,
                            List<Disk> disks,
                            List<PartitionInformation> partitions,
                            List<Device> devices,
                            ZonedDateTime dateTime,
                            Map<String, String> sysctl) {
}