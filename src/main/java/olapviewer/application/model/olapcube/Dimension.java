package olapviewer.application.model.olapcube;

import java.util.List;

public class Dimension {

    public String name;
    public List<String> hierarchies;

    public Dimension() {
    }

    public Dimension(String name, List hierarchies) {
        this.name = name;
        this.hierarchies = hierarchies;
    }
}
