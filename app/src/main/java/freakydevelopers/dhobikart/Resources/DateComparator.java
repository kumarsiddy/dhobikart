package freakydevelopers.dhobikart.Resources;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;

import java.util.Comparator;

import freakydevelopers.dhobikart.pojo.OrderSummary;

/**
 * Created by PURUSHOTAM on 8/21/2017.
 */

public class DateComparator implements Comparator<OrderSummary> {
    public int compare(OrderSummary o1, OrderSummary o2) {
        DateTime o1Date = DateTime.parse(o1.getTime(),
                DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss"));

        DateTime o2Date = DateTime.parse(o2.getTime(),
                DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss"));
        return Minutes.minutesBetween(o1Date, o2Date).getMinutes();
    }

}
