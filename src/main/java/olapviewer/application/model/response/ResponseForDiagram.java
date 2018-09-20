package olapviewer.application.model.response;

import java.util.List;

public class ResponseForDiagram {

    public List<String> xAxes;
    public List<Integer> yAxes;

    public ResponseForDiagram(List<String> xAxes, List<Integer> yAxes) {
        this.xAxes = xAxes;
        this.yAxes = yAxes;
    }
}
