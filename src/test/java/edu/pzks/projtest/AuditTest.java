package edu.pzks.projtest;

import edu.pzks.projtest.model.Item;
import edu.pzks.projtest.service.ItemService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuditTest {

    @Autowired
    private ItemService underTest;

    @BeforeEach
    void setUp() {
        Item freddy = new Item("Freddy Mercury", "Queen", "###test audit");
        Item paul = new Item("Paul McCartney", "Beatles", "###test audit");
        Item mick = new Item("Mick Jagger", "Rolling Stones", "###test audit");
        underTest.createAll(List.of(freddy, paul, mick));
    }

    @AfterEach
    void tearDown() {
        List<Item> itemsToDelete = underTest.getAll().stream()
                .filter(item -> item.getDescription() != null && item.getDescription().contains("###test"))
                .toList();
        for (Item item : itemsToDelete) {
            underTest.deleteById(item.getId());
        }
    }

    @Test
    void testSetShouldContains_3_Records_ToTest() {
        List<Item> items = underTest.getAll().stream()
                .filter(item -> item.getDescription().contains("###test"))
                .toList();
        assertEquals(3, items.size());
    }

    @Test
    void whenCreateNewItemThenAuditIsFullPresent() {
        Item item = new Item("Till Lindemann", "Rammstein", "###test audit");
        Item created = underTest.create(item);

        assertNotNull(created.getId());
        assertNotNull(created.getCreatedDate());
        assertNotNull(created.getLastModifiedDate());
    }

    @Test
    void whenUpdateItemThenLastModifiedDateChanges() throws InterruptedException {
        Item item = underTest.create(new Item("Kurt Cobain", "Nirvana", "###test audit"));
        LocalDateTime firstDate = item.getLastModifiedDate();

        Thread.sleep(100); // пауза для зміни часу
        item.setName("Updated Kurt");
        Item updated = underTest.update(item);

        assertTrue(updated.getLastModifiedDate().isAfter(firstDate));
        assertEquals(item.getCreatedDate(), updated.getCreatedDate(), "Created date must be immutable");
    }

    @Test
    void whenBulkCreateThenAllHaveAuditData() {
        List<Item> items = List.of(
                new Item("Artist 1", "B1", "###test bulk"),
                new Item("Artist 2", "B2", "###test bulk")
        );
        List<Item> saved = underTest.createAll(items);

        saved.forEach(i -> {
            assertNotNull(i.getCreatedDate());
            assertNotNull(i.getLastModifiedDate());
        });
    }

    @Test
    void testDataIntegrityAfterMultipleUpdates() throws InterruptedException {
        Item item = underTest.create(new Item("Axl Rose", "GNR", "###test audit"));
        LocalDateTime created = item.getCreatedDate();

        Thread.sleep(50);
        item.setCode("New Code");
        underTest.update(item);

        Thread.sleep(50);
        Item finalItem = underTest.update(item);

        assertEquals(created, finalItem.getCreatedDate());
        assertTrue(finalItem.getLastModifiedDate().isAfter(created));
    }
}