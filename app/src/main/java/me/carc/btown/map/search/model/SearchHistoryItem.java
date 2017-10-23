package me.carc.btown.map.search.model;

/**
 * Created by Carc.me on 18.10.16.
 * <p/>
 * TODO: Add a class header comment!
 */
public class SearchHistoryItem {

    private long date;
    private Place place;

    public Place getPlace() {
        return place;
    }
    public void setPlace(Place place) {
        this.place = place;
    }

    public long getDate() {
        return date;
    }
    public void setDate(long date) {
        this.date = date;
    }
}
