package edu.pzks.projtest;

import edu.pzks.projtest.model.Item;
import edu.pzks.projtest.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItemIntegrationTest {

    @Autowired
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        // Очищуємо базу або готуємо специфічні дані перед кожним тестом
        List<Item> all = itemService.getAll();
        for (Item item : all) {
            itemService.deleteById(item.getId());
        }
    }

    @Test
    void shouldSaveAndRetrieveItemFromRealDatabase() {
        // given
        Item item = new Item("Integration Artist", "INT-01", "Testing real DB");

        // when
        Item savedItem = itemService.create(item);

        // then
        assertNotNull(savedItem.getId());
        Item fetchedItem = itemService.getAll().stream()
                .filter(i -> i.getId().equals(savedItem.getId()))
                .findFirst()
                .orElse(null);

        assertNotNull(fetchedItem);
        assertEquals("Integration Artist", fetchedItem.getName());
    }

    @Test
    void shouldUpdateItemAndReflectChangesInDatabase() {
        // given
        Item saved = itemService.create(new Item("Old Name", "CODE", "Desc"));

        // when
        saved.setName("New Brand Name");
        itemService.update(saved);

        // then
        Item updated = itemService.getAll().stream()
                .filter(i -> i.getId().equals(saved.getId()))
                .findFirst()
                .get();

        assertEquals("New Brand Name", updated.getName());
    }

    @Test
    void shouldDeleteRecordPhysicallyFromDatabase() {
        // given
        Item saved = itemService.create(new Item("To Delete", "DEL", "Desc"));
        String id = saved.getId();

        // when
        itemService.deleteById(id);

        // then
        boolean exists = itemService.getAll().stream().anyMatch(i -> i.getId().equals(id));
        assertFalse(exists, "Об'єкт повинен бути видалений з бази даних");
    }

    @Test
    void shouldHandleBulkCreationAndCounting() {
        // given
        List<Item> items = List.of(
                new Item("Item 1", "C1", "D1"),
                new Item("Item 2", "C2", "D2")
        );

        // when
        itemService.createAll(items);

        // then
        List<Item> allItems = itemService.getAll();
        assertTrue(allItems.size() >= 2);
    }

    @Test
    void shouldVerifyAuditFieldsPopulatedBySpringData() {
        // given
        Item item = new Item("Audit Test", "AUD-01", "Checking dates");

        // when
        Item saved = itemService.create(item);

        // then
        assertNotNull(saved.getCreatedDate(), "Дата створення має бути заповнена базою");
        assertNotNull(saved.getLastModifiedDate(), "Дата модифікації має бути заповнена базою");
    }
}