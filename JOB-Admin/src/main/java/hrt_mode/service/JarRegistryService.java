package hrt_mode.service;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import hrt_mode.model.JarInfo;

@Service
public class JarRegistryService {

    private static final String CONFIG_PATH = "./app-config/apps.json";
    
    private final Map<String, JarInfo> jarMap = new HashMap<>();
    
    public JarRegistryService() {
        loadJarConfig();
    }
    
    private void loadJarConfig() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(CONFIG_PATH); // または "app-config/apps.json"
            Map<String, JarInfo> map = mapper.readValue(file, new TypeReference<>() {});
            jarMap.putAll(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public JarInfo getJarInfo(String name) {
        return jarMap.get(name);
    }

    public List<JarInfo> getAllJarInfo() {
        return Collections.unmodifiableList(jarMap.values().stream().toList());
    }
    public Map<String, JarInfo> getAll() {
        return jarMap;
    }
    public void addNewApp(JarInfo newApp) throws IOException {
        jarMap.put(newApp.getId(), newApp);
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(CONFIG_PATH);
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, jarMap);
    }
    public void removeApp(String appId) throws IOException {
        jarMap.remove(appId);
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("jarlist.json");
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, jarMap);
    }
    public void saveAll() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(CONFIG_PATH);
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, jarMap);
    }

}