package com.example.restservice.cache;

import com.example.restservice.model.Order;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderCache {

    private static final Logger logger = LoggerFactory.getLogger(OrderCache.class);

    private static final int MAX_ENTRIES = 1000;

    private final Map<String, List<Order>> cache;

    public OrderCache() {
        this.cache = Collections.synchronizedMap(new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, List<Order>> eldest) {
                boolean shouldRemove = size() > MAX_ENTRIES;
                if (shouldRemove) {
                    logger.info("🧹 Removed eldest cache entry for key '{}'", eldest.getKey());
                }
                return shouldRemove;
            }
        });
    }

    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void autoClearCache() {
        clear();
        logger.info("🕒 Автоматически очищен кэш заказов по расписанию");
    }

    public List<Order> get(String key) {
        logger.info("📥 Get from cache for key '{}'", key);
        return cache.get(key);
    }

    public void put(String key, List<Order> orders) {
        cache.put(key, orders);
        logger.info("📦 Cached {} orders for key '{}'", orders.size(), key);
    }

    public void clear() {
        cache.clear();
        logger.info("🧹 Cleared entire order cache");
    }

    public void invalidate(String key) {
        List<Order> removed = cache.remove(key);
        if (removed != null) {
            logger.info("❌ Invalidated cache for key '{}'", key);
        }
    }

    public boolean contains(String key) {
        return cache.containsKey(key);
    }
}

