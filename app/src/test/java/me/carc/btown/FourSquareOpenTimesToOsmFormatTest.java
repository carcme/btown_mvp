package me.carc.btown;

import org.junit.Test;

import java.util.ArrayList;

import me.carc.btown.data.all4squ.entities.Open;
import me.carc.btown.data.all4squ.entities.Timeframe;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class FourSquareOpenTimesToOsmFormatTest {
    @Test
    public void buildTestData() throws Exception {

        ArrayList<Timeframe> timeframes = new ArrayList<>();
        ArrayList<Open> opens = new ArrayList<>();
        {
            Open open = new Open();
            open.setRenderedTime("1:00 PM–7:00 PM");
            opens.add(open);

            Timeframe timeframe = new Timeframe();
            timeframe.setOpen(opens);
            timeframe.setDays("Today");
            timeframe.setIncludesToday(true);

            timeframes.add(timeframe);
        }

/*
        StringBuilder sb = new StringBuilder();
        for(Timeframe frame : timeframes){
            if (frame.isIncludesToday()) {
                sb = new StringBuilder();
                for (Open open : frame.getOpen()) {
                    sb.append(getOsmFormatDayOfWeek()).append(" ").append(open.getRenderedTime()).append(", ");
                }
                element.tags.openingHours = sb.substring(0, sb.lastIndexOf(", "));
            }
        }



        assertEquals(sb.toString(), "Fr 13:00-19:00");

*/

    }
}