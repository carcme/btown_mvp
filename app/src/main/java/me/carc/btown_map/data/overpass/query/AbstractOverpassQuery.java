package me.carc.btown_map.data.overpass.query;


import me.carc.btown_map.data.overpass.output.OutputFormat;

abstract class AbstractOverpassQuery {

    protected OverpassQueryBuilder builder;

    AbstractOverpassQuery() {
        this(new OverpassQueryBuilderImpl());
    }

    AbstractOverpassQuery(OverpassQueryBuilder builder) {
        this.builder = builder;
    }

    public void onSubQueryResult(AbstractOverpassSubQuery subQuery) {
        builder.append(subQuery.build());
    }

    protected abstract String build();

    public abstract OverpassQuery format(OutputFormat json);
}

