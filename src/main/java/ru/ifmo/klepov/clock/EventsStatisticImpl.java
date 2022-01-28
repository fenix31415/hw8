package ru.ifmo.klepov.clock;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventsStatisticImpl implements EventsStatistic {
    private final Map<String, List<Instant>> events = new HashMap<>();
    private final Clock clock;

    public EventsStatisticImpl(Clock clock) {
        this.clock = clock;
    }

    private void removeOld() {
        Instant oldTime = clock.now().minus(1, ChronoUnit.HOURS);

        for (String name : events.keySet()) {
            List<Instant> newInstants = events.get(name).stream()
                    .filter(instant -> instant.isAfter(oldTime))
                    .collect(Collectors.toList());

            events.put(name, newInstants);
        }

        events.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    @Override
    public void incEvent(String name) {
        removeOld();

        events.computeIfAbsent(name, k -> new ArrayList<>());
        events.get(name).add(clock.now());
    }

    @Override
    public double getEventStatisticByName(String name) {
        return getEventStatisticByName(name, true);
    }

    private double getEventStatisticByName(String name, boolean removeOld) {
        if (removeOld) {
            removeOld();
        }

        if (!events.containsKey(name)) {
            return 0;
        }

        return events.get(name).size() / 60.0;
    }

    @Override
    public Map<String, Double> getAllEventStatistic() {
        removeOld();

        return events.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                entry -> getEventStatisticByName(entry.getKey(), false)));
    }

    @Override
    public void printStatistic() {
        var statistic = getAllEventStatistic();

        for (String name : statistic.keySet()) {
            System.out.printf("%s: %f%n", name, statistic.get(name));
        }
    }

}