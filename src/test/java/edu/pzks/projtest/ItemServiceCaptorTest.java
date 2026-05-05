package edu.pzks.projtest;

import edu.pzks.projtest.model.Item;
import edu.pzks.projtest.repository.ItemRepository;
import edu.pzks.projtest.service.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ItemServiceCaptorTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService underTest;

    @Captor
    private ArgumentCaptor<Item> itemCaptor;

    @Captor
    private ArgumentCaptor<List<Item>> listCaptor;

    @Test
    void shouldCaptureItemWhenCreated() {
        Item item = new Item("Lennon", "001", "Music");

        underTest.create(item);

        verify(itemRepository).save(itemCaptor.capture());
        Item capturedItem = itemCaptor.getValue();

        assertEquals("Lennon", capturedItem.getName());
        assertEquals("001", capturedItem.getCode());
    }

    @Test
    void shouldVerifyDataIntegrityOnUpdate() {
        Item item = new Item("1", "Original", "C1", "D1");

        underTest.update(item);

        verify(itemRepository).save(itemCaptor.capture());
        Item captured = itemCaptor.getValue();

        assertEquals("1", captured.getId());
        assertEquals("Original", captured.getName());
    }

    @Test
    void shouldCaptureAllItemsInBulkOperation() {
        List<Item> items = List.of(
                new Item("N1", "C1", "D1"),
                new Item("N2", "C2", "D2")
        );

        underTest.createAll(items);

        verify(itemRepository).saveAll(listCaptor.capture());
        List<Item> capturedItems = listCaptor.getValue();

        assertEquals(2, capturedItems.size());
        assertEquals("N1", capturedItems.getFirst().getName());
    }

    @Test
    void shouldVerifyDescriptionPrefixBeforeSaving() {
        Item item = new Item("Test", "Code", "###test audit");

        underTest.create(item);

        verify(itemRepository).save(itemCaptor.capture());
        assertEquals("###test audit", itemCaptor.getValue().getDescription());
    }

    @Test
    void shouldVerifyNoChangesToCodeDuringServiceProcess() {
        String fixedCode = "STRICT_CODE_123";
        Item item = new Item("Fixed", fixedCode, "Desc");

        underTest.create(item);

        verify(itemRepository).save(itemCaptor.capture());
        assertEquals(fixedCode, itemCaptor.getValue().getCode());
    }
}