package com.retailerp.util;

import javax.swing.SwingUtilities;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventBus {
    private static final Map<String, List<Consumer<String>>> listeners = new ConcurrentHashMap<>();

    // Event Constants added for convenience
    public static final String INVENTORY_CHANGED = "INVENTORY_CHANGED";
    public static final String SALE_CHANGED = "SALE_CHANGED";
    public static final String DASHBOARD_REFRESH = "DASHBOARD_REFRESH";

    public static void subscribe(String eventType, Consumer<String> listener) {
        listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    public static void unsubscribe(String eventType, Consumer<String> listener) {
        List<Consumer<String>> list = listeners.get(eventType);
        if (list != null) {
            list.remove(listener);
        }
    }

    public static void publish(String eventType) {
        List<Consumer<String>> list = listeners.get(eventType);
        if (list != null) {
            for (Consumer<String> listener : list) {
                // Ensure UI updates are always dispatched on the EDT
                SwingUtilities.invokeLater(() -> listener.accept(eventType));
            }
        }
    }
}
