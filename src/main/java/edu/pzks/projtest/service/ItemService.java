package edu.pzks.projtest.service;

import edu.pzks.projtest.model.Item;
import edu.pzks.projtest.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    public Item create(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        return itemRepository.save(item);
    }

    public List<Item> createAll(List<Item> items) {
        return itemRepository.saveAll(items);
    }

    public Item update(Item item) {
        return itemRepository.save(item);
    }

    public void deleteById(String id) {
        itemRepository.deleteById(id);
    }
}
