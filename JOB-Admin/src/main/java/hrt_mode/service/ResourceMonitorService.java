package hrt_mode.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

@Service
public class ResourceMonitorService {

    private final SystemInfo systemInfo = new SystemInfo();
    private final CentralProcessor processor = systemInfo.getHardware().getProcessor();
    private final GlobalMemory memory = systemInfo.getHardware().getMemory();

    public Map<String, Object> getSystemResource() {
        Map<String, Object> result = new HashMap<>();

        // CPU ticks 前後で取得
        long[][] prevTicks = processor.getProcessorCpuLoadTicks();
        try {
            Thread.sleep(500); // CPU負荷測定に少し待つ
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long[][] currTicks = processor.getProcessorCpuLoadTicks();

        double[] coreLoads = new double[processor.getLogicalProcessorCount()];
        for (int i = 0; i < coreLoads.length; i++) {
            long user = currTicks[i][CentralProcessor.TickType.USER.getIndex()] - prevTicks[i][CentralProcessor.TickType.USER.getIndex()];
            long system = currTicks[i][CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[i][CentralProcessor.TickType.SYSTEM.getIndex()];
            long idle = currTicks[i][CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[i][CentralProcessor.TickType.IDLE.getIndex()];
            long total = user + system + idle;
            coreLoads[i] = total > 0 ? ((double)(user + system) / total) * 100 : 0;
        }

        // メモリ使用率（全体）
        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();
        double memoryUsage = ((double)(totalMemory - availableMemory) / totalMemory) * 100;

        result.put("cpu", Arrays.stream(coreLoads)
                                .map(load -> Math.round(load * 100.0) / 100.0)
                                .toArray());
        result.put("memory", Math.round(memoryUsage * 100.0) / 100.0);

        return result;
    }

}
