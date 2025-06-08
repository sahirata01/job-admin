package hrt_mode.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProcessUtils {
    private static final Map<String, Process> processMap = new HashMap<>();

    public static Long startProcess(String id, String javaPath, String jarPath, String workDir) {
        try {
            ProcessBuilder pb = new ProcessBuilder(javaPath, "-jar", jarPath);
            pb.directory(new File(workDir));
            pb.redirectOutput(new File("logs/" + id + "_out.log"));
            pb.redirectError(new File("logs/" + id + "_err.log"));
            Process process = pb.start();
            processMap.put(id, process);
            return process.pid(); // ✅ Java 9以降で利用可
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static boolean stopProcess(String id) {
        Process process = processMap.get(id);
        if (process != null && process.isAlive()) {
            process.destroy();
            return true;
        }
        return false;
    }

    public static boolean isRunning(String id) {
        Process process = processMap.get(id);
        return process != null && process.isAlive();
    }
}
