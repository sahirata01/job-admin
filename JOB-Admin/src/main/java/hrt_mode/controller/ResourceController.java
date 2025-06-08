package hrt_mode.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hrt_mode.service.ResourceMonitorService;

@RestController
@RequestMapping("/api/resource")
public class ResourceController {

    private final ResourceMonitorService resourceService;

    public ResourceController(ResourceMonitorService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    public Map<String, Object> getResourceStatus() {
        return resourceService.getSystemResource();
    }
}
