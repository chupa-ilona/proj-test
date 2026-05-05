package edu.pzks.projtest;

import edu.pzks.projtest.model.Item;
import edu.pzks.projtest.repository.ItemRepository;
import edu.pzks.projtest.service.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceExceptionTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService underTest;

    @Test
    void shouldThrowExceptionWhenDatabaseIsDown() {
        // given
        Item item = new Item("Name", "Code", "Desc");
        when(itemRepository.save(any(Item.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // when & then
        assertThrows(RuntimeException.class, () -> underTest.create(item));
    }

    @Test
    void shouldHandleOptimisticLockingFailure() {
        // given
        Item item = new Item("1", "Locked", "C1", "D1");
        when(itemRepository.save(any(Item.class)))
                .thenThrow(new OptimisticLockingFailureException("Conflict detected"));

        // when & then
        assertThrows(OptimisticLockingFailureException.class, () -> underTest.update(item));
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentId() {
        // given
        String id = "invalid-id";
        doThrow(new IllegalArgumentException("ID not found"))
                .when(itemRepository).deleteById(id);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> underTest.deleteById(id));
    }

    @Test
    void shouldThrowExceptionWhenCreatingNullItem() {
        // when & then
        assertThrows(Exception.class, () -> underTest.create(null));
    }

    @Test
    void shouldThrowExceptionOnDuplicateKey() {
        // given
        Item item = new Item("Duplicate", "CODE_1", "Desc");
        when(itemRepository.save(any(Item.class)))
                .thenThrow(new RuntimeException("Duplicate Key Error"));

        // when & then
        assertThrows(RuntimeException.class, () -> underTest.create(item));
    }
}
