package edu.pzks.projtest;

import edu.pzks.projtest.model.Item;
import edu.pzks.projtest.repository.ItemRepository;
import edu.pzks.projtest.service.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceMockTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService underTest;

    @Test
    void whenCreateItem_thenRepositorySaveShouldBeCalled() {
        Item item = new Item("Jim Morrison", "Lizard", "Rock");
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item created = underTest.create(item);

        assertNotNull(created);
        assertEquals("Jim Morrison", created.getName());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void whenGetAll_thenShouldReturnListFromRepository() {
        List<Item> items = List.of(
                new Item("1", "Artist A", "C1", "D1"),
                new Item("2", "Artist B", "C2", "D2")
        );
        when(itemRepository.findAll()).thenReturn(items);

        List<Item> result = underTest.getAll();

        assertEquals(2, result.size());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void whenUpdateItem_thenShouldReturnUpdatedObject() {
        Item item = new Item("1", "Old Name", "Code", "Desc");
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        item.setName("New Name");
        Item updated = underTest.update(item);

        assertEquals("New Name", updated.getName());
        verify(itemRepository).save(item);
    }

    @Test
    void whenDeleteById_thenRepositoryDeleteShouldBeCalled() {
        String id = "test-id";

        underTest.deleteById(id);

        verify(itemRepository, times(1)).deleteById(id);
    }

    @Test
    void whenCreateAll_thenRepositorySaveAllShouldBeCalled() {
        List<Item> items = List.of(new Item("N1", "C1", "D1"));
        when(itemRepository.saveAll(anyList())).thenReturn(items);

        List<Item> saved = underTest.createAll(items);

        assertFalse(saved.isEmpty());
        verify(itemRepository).saveAll(items);
    }
}
