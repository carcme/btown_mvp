package me.carc.btown_map.data.overpass.query;

import java.util.Set;

import me.carc.btown_map.data.overpass.output.OutputFormat;


/**
 * Represents a sub-query to apply filters for type elements (node, way, relation, area),
 * tag values, bounding boxes, etc.
 *
 * @see <a href="http://wiki.openstreetmap.org/wiki/Overpass_API/Overpass_QL#Filters">
 *                http://wiki.openstreetmap.org/wiki/Overpass_API/Overpass_QL#Filters</a>
 */
public class OverpassFilterQuery extends AbstractOverpassSubQuery {
    private boolean separateNext;

    public OverpassFilterQuery(OverpassQuery parent) {
        super(parent);
        init();
    }

    OverpassFilterQuery(OverpassQuery parent, OverpassQueryBuilder builder) {
        super(parent, builder);
        init();
    }

    void init() {
        builder.append("; (");
    }

    /**
     * When supplying multiple type elements in a query, use this method to automatically
     * separate them with a <i>;</i> character added in-between.
     *
     * @return the current query object
     */
    public OverpassFilterQuery prepareNext() {
        separateNext = true;

        return this;
    }

    private void applySeparator() {
        if (separateNext) {
            builder.append("; ");
        }

        separateNext = false;
    }

    /**
     * Appends the string <i>node</i> to the current query.
     * 
     * @return the current query object
     */
    public OverpassFilterQuery node() {
        applySeparator();
        builder.append("node");

        return this;
    }

    /**
     * Appends the string <i>rel</i> to the current query.
     *
     * @return the current query object
     */
    public OverpassFilterQuery rel() {
        applySeparator();
        builder.append("rel");

        return this;
    }

    /**
     * Appends the string <i>way</i> to the current query.
     *
     * @return the current query object
     */
    public OverpassFilterQuery way() {
        applySeparator();
        builder.append("way");

        return this;
    }

    /**
     * Appends the string <i>area</i> to the current query.
     *
     * @return the current query object
     */
    public OverpassFilterQuery area() {
        applySeparator();
        builder.append("area");

        return this;
    }


    public OverpassFilterQuery custom(String key, String value) {
        builder.equals(key, value);

        return this;
    }

    /**
     * A convenience method filtering the output for a single amenity.
     * It's equivalent to calling {@link #tag(String, String)} with ("amenity", amenity)
     *
     * @see #amenities(Set)
     *
     * @param amenity the filter value
     *
     * @return the current query object
     */
    public OverpassFilterQuery amenity(String amenity) {
        builder.equals("amenity", amenity);

        return this;
    }

    /**
     * A convenience method filtering the output for multiple amenities.
     * It's equivalent to calling {@link #tagMultiple(String, Set)} with ("amenity", amenities)
     * 
     * @param amenities the filter values
     *
     * @return the current query object
     */
    public OverpassFilterQuery amenities(Set<String> amenities) {
        builder.multipleValues("amenity", amenities);

        return this;
    }

    /**
     * Adds a <i>["name"]</i> filter tag to the current query.
     *
     * @param name the filter name
     *
     * @return the current query object
     */
    public OverpassFilterQuery tag(String name) {
        builder.standaloneParam(name);

        return this;
    }

    /**
     * Adds a <i>["name"=value]</i> filter tag to the current query.
     * 
     * @param name the filter name
     * @param value the filter value
     *
     * @return the current query object
     */
    public OverpassFilterQuery tag(String name, String value) {
        builder.equals(name, value);

        return this;
    }

    /**
     * Adds a <i>["name"~{value1}|{value2}|{value3}|...|{valueN}]</i> filter tag to the current query
     * to add a filter matching for any of the given values.
     * 
     * @param name the filter name
     * @param values the filter value
     *
     * @return the current query object
     */
    public OverpassFilterQuery tagMultiple(String name, Set<String> values) {
        builder.multipleValues(name, values);

        return this;
    }

    /**
     * Adds a <i>["name"!=value]</i> filter tag to the current query.
     * 
     * @param name the filter name
     * @param value the filter value
     *
     * @return the current query object
     */
    public OverpassFilterQuery tagNot(String name, String value) {
        builder.notEquals(name, value);

        return this;
    }

    /**
     * Adds a <i>["name"~value]</i> filter tag to the current query.
     *
     * @param name the filter name
     * @param value the filter value
     *
     * @return the current query object
     */
    public OverpassFilterQuery tagRegex(String name, String value) {
        builder.regexMatches(name, value);

        return this;
    }

    /**
     * Adds a <i>["name"!~value]</i> filter tag to the current query.
     *
     * @param name the filter name
     * @param value the filter value
     *
     * @return the current query object
     */
    public OverpassFilterQuery tagRegexNot(String name, String value) {
        builder.regexDoesntMatch(name, value);

        return this;
    }

    /**
     * Adds a <i>(southernLat,westernLon,northernLat,easternLon)</i> bounding box filter to the current query.
     *
     * @param southernLat the southern latitude
     * @param westernLon  the western longitude
     * @param northernLat the northern latitude
     * @param easternLon  the eastern longitude
     *
     * @return the current query object
     */
    public OverpassFilterQuery boundingBox(double southernLat, double westernLon, double northernLat, double easternLon) {
        builder.boundingBox(southernLat, westernLon, northernLat, easternLon);

        return this;
    }

    /**
     * Closes the current query with the characters <i>;&lt;;)</i> and returns the output as a string.
     *
     * @return the query as string
     */
    @Override
    public String build() {

        String END_TOKEN = ";<;)";
        builder.append(END_TOKEN);
//        builder.append("<;)");


        return builder.build();
    }

    @Override
    public OverpassQuery format(OutputFormat json) {
        return null;
    }

}
