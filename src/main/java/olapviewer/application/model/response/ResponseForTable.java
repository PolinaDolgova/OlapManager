package olapviewer.application.model.response;

import olapviewer.application.model.olapcube.Row;

import java.util.List;

public class ResponseForTable {

    public String query;
    public List<String> headers;
    public List<Row> result;

    public ResponseForTable(String query, List<Row> resultSet) {
        this.query = query;
        this.result = resultSet;
    }
}
