package me.carc.btown_map.data.overpass.query;

import java.util.Set;

interface OverpassQueryBuilder {
    OverpassQueryBuilder append(String statement);
    OverpassQueryBuilder boundingBox(double lat1, double lon1, double lat2, double lon2);
    OverpassQueryBuilder standaloneParam(String name);
    OverpassQueryBuilder clause(String name, String value);
    OverpassQueryBuilder equals(String name, String value);
    OverpassQueryBuilder notEquals(String name, String value);
    OverpassQueryBuilder regexMatches(String name, String value);
    OverpassQueryBuilder regexDoesntMatch(String name, String value);
    OverpassQueryBuilder multipleValues(String name, Set<String> values);
    String build();
}
