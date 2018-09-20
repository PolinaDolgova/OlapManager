package olapviewer.application.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryService {

    public String createMDXQuery(List<String> dimensions,
                                 List<String> dimensionHierarchies,
                                 List<String> measures,
                                 String cubeName) {
        String query = new String();
        query += "SELECT NON EMPTY {";
        for (String measure : measures) {
            query += "[Measures].[" + measure + "],";
        }

        query = query.substring(0, query.length() - 1);

        query += "} ON COLUMNS";

        if (!dimensions.isEmpty()) {
            query += ", NON EMPTY {(";

            for (int i = 0; i < dimensions.size(); i++) {
                query += "[" + dimensions.get(i) + "].["
                        + dimensionHierarchies.get(i) + "].["
                        + dimensionHierarchies.get(i) + "].ALLMEMBERS*";
            }

            query = query.substring(0, query.length() - 1);
            query += ")} ON ROWS ";
        }
        query += "FROM [" + cubeName + "]";
        return query;
    }
}
