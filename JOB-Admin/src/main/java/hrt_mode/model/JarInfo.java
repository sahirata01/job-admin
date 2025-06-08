package hrt_mode.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JarInfo {
	private String id;
    private String name;
    private String javaPath;
    private String jarPath;
    private String workDir;
    private Long pid;

}