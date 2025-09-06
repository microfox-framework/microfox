package ir.moke.microfox.system;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ir.moke.jsysbox.dev.Device;
import ir.moke.jsysbox.disk.Disk;
import ir.moke.jsysbox.disk.PartitionInformation;
import ir.moke.jsysbox.network.Ethernet;
import ir.moke.jsysbox.network.Netstat;
import ir.moke.jsysbox.network.Route;
import ir.moke.jsysbox.system.ModInfo;
import ir.moke.jsysbox.system.ThreadInfo;
import ir.moke.jsysbox.system.Ulimit;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record SystemDTO(long pid,
                        Set<ThreadInfo> threads,
                        boolean isContainer,
                        String hostname,
                        Map<String, String> environments,
                        List<String> nameservers,
                        Map<String, String> hosts,
                        String jvmVendor,
                        String jvmVersion,
                        long jvmMaxHeapSize,
                        long jvmTotalHeapSize,
                        long jvmUsedHeapSize,
                        List<Ulimit> ulimits,
                        List<ModInfo> modInfos,
                        List<Route> routes,
                        List<Netstat> netstat,
                        List<Ethernet> ethernets,
                        List<Disk> disks,
                        List<PartitionInformation> partitions,
                        List<Device> devices,
                        @JsonSerialize(using = ZonedDateTimeSerializer.class) ZonedDateTime dateTime,
                        Map<String, String> sysctl,
                        List<ClassInstance> classInstances) {
}