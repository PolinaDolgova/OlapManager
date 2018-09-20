package olapviewer.application.service;

import org.olap4j.OlapConnection;
import org.olap4j.OlapException;
import org.olap4j.metadata.Catalog;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.NamedList;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DBService {

    private Connection connection;
    private Map<String, Catalog> catalogs = new HashMap<>();
    private Map<String, Cube> cubes = new HashMap<>();

    DBService() {
    }

    public void initialize() {
        this.connection = createConnectionToDB();
    }

    public Connection createConnectionToDB() {
        Connection connectionToDB = null;
        try {
            Class.forName("org.olap4j.driver.xmla.XmlaOlap4jDriver");
            String connectionUrl = "jdbc:xmla:Server=http://localhost/OLAP/msmdpump.dll;" +
                    "integratedSecurity=true";
            connectionToDB =
                    DriverManager.getConnection(
                            connectionUrl);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connectionToDB;
    }

    public Map<String, Catalog> getCatalogs() {
        Map<String, Catalog> result = new HashMap<>();
        try {
            NamedList<Catalog> catalogs = ((OlapConnection) connection).getOlapCatalogs();
            for (Catalog catalog : catalogs) {
                result.put(catalog.getName(), catalog);
            }
        } catch (OlapException e) {
            e.printStackTrace();
        }
        this.catalogs = result;
        return result;
    }

    public List getCubes(String catalogName) {
        List<String> cubesName = new ArrayList<>();
        Catalog curCatalog = catalogs.get(catalogName);
        try {
            NamedList<Cube> cubes = curCatalog.getSchemas().get(0).getCubes();
            for (Cube cube : cubes) {
                cubesName.add(cube.getName());
                this.cubes.put(cube.getName(), cube);
            }
        } catch (OlapException e) {
            e.printStackTrace();
        }
        return cubesName;
    }

    public Cube getCube(String cubeName){
        return cubes.get(cubeName);
    }
}
