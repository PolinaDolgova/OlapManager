package olapviewer.application.controller;

import olapviewer.application.model.olapcube.OlapCube;
import olapviewer.application.model.response.ResponseForDiagram;
import olapviewer.application.model.response.ResponseForTable;
import olapviewer.application.service.CubeService;
import olapviewer.application.service.DBService;
import olapviewer.application.service.QueryService;
import org.olap4j.metadata.Catalog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class ApplicationController {

    @Autowired
    QueryService queryService;

    @Autowired
    CubeService cubeService;

    @Autowired
    DBService dbService;

    @RequestMapping(value = "/cube", method = RequestMethod.GET)
    public OlapCube getCube(@RequestParam(value = "name") String cubeName) {
        cubeService.initialization(cubeName.trim());
        return cubeService.getCube();
    }

    @RequestMapping(value = "/getData", method = RequestMethod.GET)
    public ResponseForTable getDataFromTable(
            @RequestParam(value = "name") String cubeName,
            @RequestParam(value = "check") ArrayList selected,
            @RequestParam(value = "selectval") ArrayList selectvalues) {
        OlapCube currentCube = cubeService.getCube();

        List<String> measures = new ArrayList<>();
        List<String> dimensions = new ArrayList<>();
        List<String> dimensionValues = new ArrayList<>();

        for (int i = 0; i < selected.size(); i++) {
            if (currentCube.measure.names.contains(selected.get(i))) {
                measures.add(selected.get(i).toString());
            } else {
                dimensions.add(selected.get(i).toString());
                dimensionValues.add(selectvalues.get(i).toString().trim());
            }
        }

        String query = queryService.createMDXQuery(dimensions,
                dimensionValues,
                measures,
                currentCube.getName());
        System.out.println(query);
        return cubeService.executeQuery(query);
    }

    @RequestMapping(value = "/createQuery", method = RequestMethod.GET)
    public ResponseForTable getDataFromQuery(
            @RequestParam(value = "name") String cubeName,
            @RequestParam(value = "query") String query) {
        return cubeService.executeQuery(query);
    }

    @RequestMapping(value = "/createDiagram", method = RequestMethod.GET)
    public ResponseForDiagram getDataForDiagram(
            @RequestParam(value = "dimension") String dimension,
            @RequestParam(value = "hierarchy") String hierarchy,
            @RequestParam(value = "measure") String measure) {
        String query = queryService.createMDXQuery(
                new ArrayList<String>() {{
                    add(dimension.trim());
                }},
                new ArrayList<String>() {{
                    add(hierarchy.trim());
                }},
                new ArrayList<String>() {{
                    add(measure.trim());
                }},
                cubeService.getCube().getName());
        System.out.println(query);
        return cubeService.executeDiagram(query);
    }

    @RequestMapping(value = "/getCatalogs", method = RequestMethod.GET)
    public List getCatalogs() {
        dbService.initialize();
        List<String> catalogNames = new ArrayList<>();
        for (Map.Entry<String, Catalog> catalog : dbService.getCatalogs().entrySet()) {
            catalogNames.add(catalog.getKey());
        }
        return catalogNames;
    }

    @RequestMapping(value = "/getCubes", method = RequestMethod.GET)
    public List getCubes(@RequestParam(value = "name") String catalogName) {
        cubeService.setCatalogName(catalogName);
        return dbService.getCubes(catalogName.trim());
    }
}