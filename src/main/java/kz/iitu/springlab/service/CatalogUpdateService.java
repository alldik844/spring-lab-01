package kz.iitu.springlab.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CatalogUpdateService {

    private final Map<Long, String> items = new HashMap<>();

    public CatalogUpdateService() {
        items.put(1L, "Notebook");
    }

    public synchronized Change updateItem(long id, String newName) {
        if (!items.containsKey(id)) {
            throw new IllegalArgumentException("Item not found: " + id);
        }

        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }

        String oldName = items.put(id, newName);
        return new Change(id, oldName, newName);
    }

    public record Change(long id, String oldValue, String newValue) {
    }
}
