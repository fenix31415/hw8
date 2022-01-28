package ru.ifmo.klepov.clock;

import java.time.Instant;
import java.time.temporal.TemporalUnit;

public class SettableClock implements Clock {
    private Instant now;

    public SettableClock(Instant now) {
        this.now = now;
    }

    @Override
    public Instant now() {
        return now;
    }

    @Override
    public void setNow(Instant now) {
        this.now = now;
    }

    public void sleep(long amountToAdd, TemporalUnit unit) {
        setNow(now.plus(amountToAdd, unit));
    }
}
