package ru.ifmo.klepov.clock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class EventsStatisticImplTest {
    private SettableClock clock;
    private EventsStatistic eventStatistic;

    final static String e1 = "e1";
    final static String e2 = "e2";
    final static String e3 = "e3";

    @Before
    public void before() {
        clock = new SettableClock(Instant.now());
        eventStatistic = new EventsStatisticImpl(clock);
    }

    @Test
    public void Test0() {
        Assert.assertEquals(0.0, eventStatistic.getEventStatisticByName("Event"), 0.0001);
    }

    @Test
    public void Test1() {
        eventStatistic.incEvent(e1);
        eventStatistic.incEvent(e2);
        eventStatistic.incEvent(e1);

        Assert.assertEquals(2.0 / 60.0, eventStatistic.getEventStatisticByName(e1), 0.0001);
    }

    @Test
    public void Test_old() {
        eventStatistic.incEvent(e1);
        Assert.assertEquals(1.0 / 60.0, eventStatistic.getEventStatisticByName(e1), 0.0001);

        clock.sleep(31, ChronoUnit.MINUTES);
        Assert.assertEquals(1.0 / 60.0, eventStatistic.getEventStatisticByName(e1), 0.0001);

        eventStatistic.incEvent(e1);
        Assert.assertEquals(2.0 / 60.0, eventStatistic.getEventStatisticByName(e1), 0.0001);

        clock.sleep(31, ChronoUnit.MINUTES);
        Assert.assertEquals(1.0 / 60.0, eventStatistic.getEventStatisticByName(e1), 0.0001);

        clock.sleep(31, ChronoUnit.MINUTES);
        Assert.assertEquals(0.0 / 60.0, eventStatistic.getEventStatisticByName(e1), 0.0001);

    }

    @Test
    public void Test_all() {
        eventStatistic.incEvent(e1);
        clock.sleep(30, ChronoUnit.MINUTES);

        eventStatistic.incEvent(e2);
        eventStatistic.incEvent(e2);
        clock.sleep(30, ChronoUnit.MINUTES);

        eventStatistic.incEvent(e3);

        var ans = eventStatistic.getAllEventStatistic();
        Assert.assertTrue(!ans.containsKey(e1) && ans.containsKey(e2) && ans.containsKey(e3));
        Assert.assertEquals(1.0 / 30.0, ans.get(e2), 0.0001);
        Assert.assertEquals(1.0 / 60.0, ans.get(e3), 0.0001);
    }
}