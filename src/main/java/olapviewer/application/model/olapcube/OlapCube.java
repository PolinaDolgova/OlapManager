package olapviewer.application.model.olapcube;

import java.util.List;

public class OlapCube {

    public String name;
    public List<Dimension> dimensions;
    public Measure measure;

    public OlapCube(){};

    public OlapCube(String name, List dimensions, Measure measure){
        this.name = name;
        this.dimensions = dimensions;
        this.measure = measure;
    }

    public List<Dimension> getDimensions() {
        return dimensions;
    }

    public Measure getMeasure() {
        return measure;
    }

    public String getName() {
        return name;
    }
}
