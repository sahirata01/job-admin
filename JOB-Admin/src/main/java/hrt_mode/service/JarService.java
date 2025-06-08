package hrt_mode.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import hrt_mode.model.JarInfo;
import hrt_mode.util.ProcessUtils;

@Service
public class JarService {
	private static final Logger log = LoggerFactory.getLogger(JarService.class);
    private final JarRegistryService registry;

    public JarService(JarRegistryService registry) {
        this.registry = registry;
    }

    public boolean startApp(String id) {
        JarInfo jar = registry.getJarInfo(id);
        if (jar == null) return false;

        Long pid = ProcessUtils.startProcess(id, jar.getJavaPath(), jar.getJarPath(), jar.getWorkDir());
        if (pid != null) {
            jar.setPid(pid);
            try {
                registry.saveAll(); // 🔽 下で定義
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info("アプリ起動: ID={}, PID={}", id, pid);
            return true;
        }
        return false;
    }

    public boolean stopApp(String id) {
        return ProcessUtils.stopProcess(id);
    }

    public boolean restartApp(String id) {
        stopApp(id);
        return startApp(id);
    }

    public boolean isRunning(String id) {
        return ProcessUtils.isRunning(id);
    }
    public List<String> getAllAppIds() {
        return new ArrayList<>(registry.getAll().keySet());
    }
    public Collection<JarInfo> getAllApps() {
    	
        return registry.getAll().values();
    }
    public void deleteApp(String appId) throws IOException {
        // プロセス停止
        ProcessUtils.stopProcess(appId);
        // レジストリから削除し、ファイル保存
        registry.removeApp(appId);
    }

}
