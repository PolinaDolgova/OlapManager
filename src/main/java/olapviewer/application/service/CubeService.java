package olapviewer.application.service;

import olapviewer.application.model.olapcube.*;
import olapviewer.application.model.olapcube.Cell;
import olapviewer.application.model.response.ResponseForDiagram;
import olapviewer.application.model.response.ResponseForTable;
import org.olap4j.*;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CubeService {

    @Autowired
    private DBService dbService;

    private String cubeName;
    private String catalogName;
    private Connection connection;
    private OlapCube cube;

    public void initialization(String cubeName) {
        setCubeName(cubeName);
        setConnection(getConnection());
        setCube(dbService.getCube(cubeName));
    }

    private OlapCube cubeTransformation(Cube cube) {
        List measures = new ArrayList();
        List dimensions = new ArrayList();

        for (org.olap4j.metadata.Dimension dimension : cube.getDimensions()) {
            if (!dimension.getName().equals("Measures")) {
                List hierarchies = new ArrayList();
                for (Hierarchy hierarchy : dimension.getHierarchies()) {
                    hierarchies.add(hierarchy.getName());
                }
                dimensions.add(new Dimension(dimension.getName(), hierarchies));
            }
        }

        for (org.olap4j.metadata.Measure measure : cube.getMeasures()) {
            if (!measure.getName().contains("Число")) {
                measures.add(measure.getName());
            }
        }
        return new OlapCube(cube.getName(), dimensions, new Measure(measures));
    }

    private List<Row> responseTransformationForTable(CellSet response) {
        List<Row> result = new ArrayList<>();

        List<CellSetAxis> cellSetAxes = response.getAxes();

        CellSetAxis columnsAxis = cellSetAxes.get(Axis.COLUMNS.axisOrdinal());
        CellSetAxis rowsAxis = cellSetAxes.get(Axis.ROWS.axisOrdinal());

        int cellOrdinal = 0;
        for (Position rowPosition : rowsAxis.getPositions()) {
            List<String> header = new ArrayList<>();
            List<Cell> cells = new ArrayList<>();
            for (Member member : rowPosition.getMembers()) {
                cells.add(new Cell(member.getName()));
                header.add(member.getDimension().getName());
            }

            for (Position columnPosition : columnsAxis.getPositions()) {
                org.olap4j.Cell cell = response.getCell(cellOrdinal);
                ++cellOrdinal;
                cells.add(new Cell(cell.getFormattedValue()));
                header.add(columnPosition.getMembers().get(0).getName());
            }
            result.add(new Row(cells));
        }
        return result;
    }

    private ResponseForDiagram responseTransformationForDiagram(CellSet response) {
        List xAxes = new ArrayList<>();
        List yAxes = new ArrayList<>();
        List<CellSetAxis> cellSetAxes = response.getAxes();
        CellSetAxis columnsAxis = cellSetAxes.get(Axis.COLUMNS.axisOrdinal());
        CellSetAxis rowsAxis = cellSetAxes.get(Axis.ROWS.axisOrdinal());

        int cellOrdinal = 0;
        for (Position rowPosition : rowsAxis.getPositions()) {
            for (Member member : rowPosition.getMembers()) {
                xAxes.add(member.getName());
            }

            for (Position columnPosition : columnsAxis.getPositions()) {
                org.olap4j.Cell cell = response.getCell(cellOrdinal);
                ++cellOrdinal;
                yAxes.add(Integer.parseInt(cell.getFormattedValue()));
            }
        }
        return new ResponseForDiagram(xAxes, yAxes);
    }

    private Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("org.olap4j.driver.xmla.XmlaOlap4jDriver");
            String connectionUrl = "jdbc:xmla:Server=http://localhost/OLAP/msmdpump.dll;Catalog="
                    + catalogName
                    + ";integratedSecurity=true";
            connection =
                    DriverManager.getConnection(
                            connectionUrl);
            OlapWrapper wrapper = (OlapWrapper) connection;
            OlapConnection olapConnection = wrapper.unwrap(OlapConnection.class);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public ResponseForTable executeQuery(String query) {
        List<Row> result = null;
        try {
            OlapStatement statement = (OlapStatement) connection.createStatement();
            result = responseTransformationForTable(statement.executeOlapQuery(query));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ResponseForTable(query, result);
    }

    public ResponseForDiagram executeDiagram(String query) {
        ResponseForDiagram result = null;
        try {
            OlapStatement statement = (OlapStatement) connection.createStatement();
            result = responseTransformationForDiagram(statement.executeOlapQuery(query));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void setCube(Cube cube) {
        this.cube = cubeTransformation(cube);
    }

    private void setCubeName(String cubeName) {
        this.cubeName = cubeName;
    }

    private void setConnection(Connection connection) {
        this.connection = connection;
    }

    public OlapCube getCube() {
        return cube;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }
}
