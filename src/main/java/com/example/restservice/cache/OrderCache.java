package com.example.restservice.cache;

import com.example.restservice.model.Order;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

@Component
public class OrderCache {

    private static final Logger logger = LoggerFactory.getLogger(OrderCache.class);

    private static final long MAX_CACHE_SIZE = 1_000_000_000L;
    private long currentCacheSize = 0;

    private final Map<String, List<Order>> cache;

    public OrderCache() {
        this.cache = Collections.synchronizedMap(new LinkedHashMap<>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, List<Order>> eldest) {
                if (currentCacheSize > MAX_CACHE_SIZE) {
                    long size = estimateSize(eldest.getValue());
                    currentCacheSize -= size;
                    logger.info("🧹 Removed eldest cache entry for key '{}' (size {} bytes)", eldest.getKey(), size);
                    return true;
                }
                return false;
            }
        });
    }

    @Scheduled(fixedRate = 30 * 1000)
    public void autoClearCache() {
        clear();
        logger.info("🕒 Автоматически очищен кэш заказов по расписанию");
    }

    public List<Order> get(String key) {
        logger.info("📥 Get from cache for key '{}'", key);
        return cache.get(key);
    }

    public void put(String key, List<Order> orders) {
        long size = estimateSize(orders);
        cache.put(key, orders);
        currentCacheSize += size;
        logger.info("📦 Cached {} orders for key '{}' (estimated size: {} bytes)", orders.size(), key, size);
    }

    public void clear() {
        cache.clear();
        currentCacheSize = 0;
        logger.info("🧹 Cleared entire order cache");
    }

    private long estimateSize(List<Order> orders) {
        return orders.size() * 500L;
    }

    public boolean contains(String key) {
        return cache.containsKey(key);
    }
}

