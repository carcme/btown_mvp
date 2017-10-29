package me.carc.btown.map.search.model;

import me.carc.btown.data.results.OverpassQueryResult;

/**
 * Created by Carc.me on 18.10.16.
 * <p/>
 * TODO: Add a class header comment!
 */
public class SavedFavoriteItem {

    private long date;
    private Place place;
    private OverpassQueryResult.Element element;

    public Place getPlace() {
        return place;
    }
    public void setPlace(Place place) { this.place = place; }

    public OverpassQueryResult.Element getElement() {
        return element;
    }
    public void setElement (OverpassQueryResult.Element element) { this.element = element; }

    public long getDate() {
        return date;
    }
    public void setDate(long date) {
        this.date = date;
    }
}
