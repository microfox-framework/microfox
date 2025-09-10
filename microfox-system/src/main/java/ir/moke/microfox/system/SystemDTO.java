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

public class SystemDTO {
    private Long pid;
    private Set<ThreadInfo> threads;
    private Boolean isContainer;
    private String hostname;
    private Map<String, String> environments;
    private List<String> nameservers;
    private Map<String, String> hosts;
    private String jvmVendor;
    private String jvmVersion;
    private Long jvmMaxHeapSize;
    private Long jvmTotalHeapSize;
    private Long jvmUsedHeapSize;
    private List<Ulimit> ulimits;
    private List<ModInfo> modInfos;
    private List<Route> routes;
    private List<Netstat> netstat;
    private List<Ethernet> ethernets;
    private List<Disk> disks;
    private List<PartitionInformation> partitions;
    private List<Device> devices;
    private @JsonSerialize(using = ZonedDateTimeSerializer.class) ZonedDateTime dateTime;
    private Map<String, String> sysctl;
    private List<ClassInstance> classInstance;

    public SystemDTO() {
    }

    public SystemDTO(Long pid, Set<ThreadInfo> threads, Boolean isContainer, String hostname, Map<String, String> environments, List<String> nameservers, Map<String, String> hosts, String jvmVendor, String jvmVersion, Long jvmMaxHeapSize, Long jvmTotalHeapSize, Long jvmUsedHeapSize, List<Ulimit> ulimits, List<ModInfo> modInfos, List<Route> routes, List<Netstat> netstat, List<Ethernet> ethernets, List<Disk> disks, List<PartitionInformation> partitions, List<Device> devices, ZonedDateTime dateTime, Map<String, String> sysctl, List<ClassInstance> classInstance) {
        this.pid = pid;
        this.threads = threads;
        this.isContainer = isContainer;
        this.hostname = hostname;
        this.environments = environments;
        this.nameservers = nameservers;
        this.hosts = hosts;
        this.jvmVendor = jvmVendor;
        this.jvmVersion = jvmVersion;
        this.jvmMaxHeapSize = jvmMaxHeapSize;
        this.jvmTotalHeapSize = jvmTotalHeapSize;
        this.jvmUsedHeapSize = jvmUsedHeapSize;
        this.ulimits = ulimits;
        this.modInfos = modInfos;
        this.routes = routes;
        this.netstat = netstat;
        this.ethernets = ethernets;
        this.disks = disks;
        this.partitions = partitions;
        this.devices = devices;
        this.dateTime = dateTime;
        this.sysctl = sysctl;
        this.classInstance = classInstance;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Set<ThreadInfo> getThreads() {
        return threads;
    }

    public void setThreads(Set<ThreadInfo> threads) {
        this.threads = threads;
    }

    public Boolean getContainer() {
        return isContainer;
    }

    public void setContainer(Boolean container) {
        isContainer = container;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Map<String, String> getEnvironments() {
        return environments;
    }

    public void setEnvironments(Map<String, String> environments) {
        this.environments = environments;
    }

    public List<String> getNameservers() {
        return nameservers;
    }

    public void setNameservers(List<String> nameservers) {
        this.nameservers = nameservers;
    }

    public Map<String, String> getHosts() {
        return hosts;
    }

    public void setHosts(Map<String, String> hosts) {
        this.hosts = hosts;
    }

    public String getJvmVendor() {
        return jvmVendor;
    }

    public void setJvmVendor(String jvmVendor) {
        this.jvmVendor = jvmVendor;
    }

    public String getJvmVersion() {
        return jvmVersion;
    }

    public void setJvmVersion(String jvmVersion) {
        this.jvmVersion = jvmVersion;
    }

    public Long getJvmMaxHeapSize() {
        return jvmMaxHeapSize;
    }

    public void setJvmMaxHeapSize(Long jvmMaxHeapSize) {
        this.jvmMaxHeapSize = jvmMaxHeapSize;
    }

    public Long getJvmTotalHeapSize() {
        return jvmTotalHeapSize;
    }

    public void setJvmTotalHeapSize(Long jvmTotalHeapSize) {
        this.jvmTotalHeapSize = jvmTotalHeapSize;
    }

    public Long getJvmUsedHeapSize() {
        return jvmUsedHeapSize;
    }

    public void setJvmUsedHeapSize(Long jvmUsedHeapSize) {
        this.jvmUsedHeapSize = jvmUsedHeapSize;
    }

    public List<Ulimit> getUlimits() {
        return ulimits;
    }

    public void setUlimits(List<Ulimit> ulimits) {
        this.ulimits = ulimits;
    }

    public List<ModInfo> getModInfos() {
        return modInfos;
    }

    public void setModInfos(List<ModInfo> modInfos) {
        this.modInfos = modInfos;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public List<Netstat> getNetstat() {
        return netstat;
    }

    public void setNetstat(List<Netstat> netstat) {
        this.netstat = netstat;
    }

    public List<Ethernet> getEthernets() {
        return ethernets;
    }

    public void setEthernets(List<Ethernet> ethernets) {
        this.ethernets = ethernets;
    }

    public List<Disk> getDisks() {
        return disks;
    }

    public void setDisks(List<Disk> disks) {
        this.disks = disks;
    }

    public List<PartitionInformation> getPartitions() {
        return partitions;
    }

    public void setPartitions(List<PartitionInformation> partitions) {
        this.partitions = partitions;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public ZonedDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(ZonedDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Map<String, String> getSysctl() {
        return sysctl;
    }

    public void setSysctl(Map<String, String> sysctl) {
        this.sysctl = sysctl;
    }

    public List<ClassInstance> getClassInstance() {
        return classInstance;
    }

    public void setClassInstance(List<ClassInstance> classInstance) {
        this.classInstance = classInstance;
    }
}