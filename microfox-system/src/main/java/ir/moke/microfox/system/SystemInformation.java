package ir.moke.microfox.system;

import ir.moke.jsysbox.dev.Device;
import ir.moke.jsysbox.dev.JDevice;
import ir.moke.jsysbox.disk.Disk;
import ir.moke.jsysbox.disk.JDiskManager;
import ir.moke.jsysbox.disk.PartitionInformation;
import ir.moke.jsysbox.network.Ethernet;
import ir.moke.jsysbox.network.JNetwork;
import ir.moke.jsysbox.network.Netstat;
import ir.moke.jsysbox.network.Route;
import ir.moke.jsysbox.system.JSystem;
import ir.moke.jsysbox.system.ModInfo;
import ir.moke.jsysbox.system.ThreadInfo;
import ir.moke.jsysbox.system.Ulimit;

import java.io.BufferedReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SystemInformation {

    private static final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

    public static long jvmTotalHeapSize() {
        return memoryMXBean.getHeapMemoryUsage().getCommitted();
    }

    public static long jvmMaxHeapSize() {
        return memoryMXBean.getHeapMemoryUsage().getMax();
    }

    public static long jvmUsedHeapSize() {
        return memoryMXBean.getHeapMemoryUsage().getUsed();
    }

    public static String hostname() {
        return JSystem.getHostname();
    }

    public static Map<String, String> environments() {
        return System.getenv();
    }

    public static List<String> dnsNameServers() {
        return JNetwork.getDnsNameServers();
    }

    public static Map<String, String> hosts() {
        return JNetwork.hosts();
    }

    public static List<Ulimit> ulimit() {
        return JSystem.getAllUlimits();
    }

    public static List<ModInfo> lsmod() {
        return JSystem.lsmod();
    }

    public static List<Route> routes() {
        return JNetwork.route();
    }

    public static List<Netstat> netstat() {
        return JNetwork.netstatIpv4();
    }

    public static List<Ethernet> ethernets() {
        return JNetwork.ethernetList(false);
    }

    public static List<Disk> disks() {
        return JDiskManager.getAllDiskInformation();
    }

    public static List<PartitionInformation> partitions() {
        return JDiskManager.partitions();
    }

    public static List<Device> devices() {
        return JDevice.scanDevices();
    }

    public static ZonedDateTime zonedDateTime() {
        return ZonedDateTime.now();
    }

    public static Map<String, String> sysctl() {
        return JSystem.sysctl();
    }

    public static String jvmVersion() {
        return System.getProperty("java.runtime.version");
    }

    public static String jvmVendor() {
        return System.getProperty("java.specification.vendor");
    }

    public static long pid() {
        return JSystem.pid();
    }

    public static Set<ThreadInfo> threads() {
        return JSystem.threads(pid());
    }

    public static boolean isContainer() {
        return JSystem.insideContainer();
    }

    public static List<ClassInstance> instanceList() {
        try {
            String javaHome = System.getProperty("java.home");
            String cmd = javaHome + "/bin/jmap";
            ProcessBuilder builder = new ProcessBuilder(cmd, "-histo", "" + pid());
            Process process = builder.start();
            try (BufferedReader reader = process.inputReader()) {
                return reader.lines()
                        .filter(item -> !item.startsWith("Total"))
                        .skip(2)
                        .map(item -> item.split("\\s+", 4))
                        .map(item -> new ClassInstance(Long.parseLong(item[1]), Long.parseLong(item[2]), item[3]))
                        .toList();
            }
        } catch (Exception ignore) {
        }
        return Collections.emptyList();
    }
}
