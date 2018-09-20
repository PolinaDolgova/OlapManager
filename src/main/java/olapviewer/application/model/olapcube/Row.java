package olapviewer.application.model.olapcube;

import java.util.List;

public class Row {

    public List<Cell> cells;

    public Row(){}

    public Row(List<Cell> values){
        this.cells = values;
    }

    public List<Cell> getCells() {
        return cells;
    }
}
