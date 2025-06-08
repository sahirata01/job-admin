package hrt_mode.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hrt_mode.model.JarInfo;
import hrt_mode.service.JarRegistryService;
import hrt_mode.service.JarService;
@RestController
@RequestMapping("/api/apps")
public class JarController {
	
	private static final Logger log = LoggerFactory.getLogger(JarController.class);
	
    private final JarService jarService;
    private final JarRegistryService jarRegistryService;
    
    public JarController(JarService jarService,JarRegistryService jarRegistryService) {
        this.jarService = jarService;
        this.jarRegistryService = jarRegistryService;
    }
 

    @PostMapping("/start")
    public String startApp(@RequestParam String appId) {
    	log.info("App Start : " +  appId );
        return jarService.startApp(appId) ? "Started" : "Failed to start";
    }

    @PostMapping("/stop")
    public String stopApp(@RequestParam String appId) {
        return jarService.stopApp(appId) ? "Stopped" : "Failed to stop";
    }

    @PostMapping("/restart")
    public String restartApp(@RequestParam String appId) {
    	log.info("App ReStart : "+ appId );
        return jarService.restartApp(appId) ? "Restarted" : "Failed to restart";
    }

    @GetMapping("/status")
    public Map<String, Boolean> statusAll() {
        Map<String, Boolean> map = new HashMap<>();
        for (String id : jarService.getAllAppIds()) {
            map.put(id, jarService.isRunning(id));
        }
        return map;
    }
    @GetMapping("/list")
    public Collection<JarInfo> listApps() {
    	return jarService.getAllApps(); // Collection<JarInfo> を返すメソッドがある
    }
    
    @PostMapping("/register")
    public ResponseEntity<String> registerApp(@RequestBody JarInfo newApp) {
        try {
        	jarRegistryService.addNewApp(newApp); // ←次の項目で定義
            return ResponseEntity.ok("登録成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("登録失敗");
        }
    }
    @PostMapping("/delete")
    public ResponseEntity<String> deleteApp(@RequestParam String appId) {
        try {
            jarService.deleteApp(appId);
            return ResponseEntity.ok("削除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("削除失敗");
        }
    }
}
