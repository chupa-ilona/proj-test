package edu.pzks.projtest;

import edu.pzks.projtest.model.Item;
import edu.pzks.projtest.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        // Очищаємо колекцію перед кожним тестом для їхньої повної незалежності
        itemRepository.deleteAll();
    }

    @Test
    @DisplayName("1. Test save item operation")
    void givenItemObject_whenSave_thenReturnSavedItem() {
        // Given
        Item item = new Item("1", "Test Name", "CODE01", "Test Description");

        // When
        Item savedItem = itemRepository.save(item);

        // Then
        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getId()).isEqualTo("1");
        assertThat(savedItem.getName()).isEqualTo("Test Name");
    }

    @Test
    @DisplayName("2. Test find item by ID operation (Positive)")
    void givenItemObject_whenFindById_thenReturnItemObject() {
        // Given
        Item item = new Item("2", "Laptop", "LPT-01", "Gaming Laptop");
        itemRepository.save(item);

        // When
        Item foundItem = itemRepository.findById(item.getId()).orElse(null);

        // Then
        assertThat(foundItem).isNotNull();
        assertThat(foundItem.getCode()).isEqualTo("LPT-01");
    }

    @Test
    @DisplayName("3. Test find item by ID operation (Negative - Not Found)")
    void givenNonExistentId_whenFindById_thenReturnEmpty() {
        // Given
        String nonExistentId = "999";

        // When
        Optional<Item> itemOptional = itemRepository.findById(nonExistentId);

        // Then
        assertThat(itemOptional).isEmpty();
    }

    @Test
    @DisplayName("4. Test find all items operation")
    void givenItemsList_whenFindAll_thenItemsList() {
        // Given
        Item item1 = new Item("3", "Mouse", "MS-01", "Wireless Mouse");
        Item item2 = new Item("4", "Keyboard", "KB-01", "Mechanical Keyboard");
        itemRepository.saveAll(List.of(item1, item2));

        // When
        List<Item> itemList = itemRepository.findAll();

        // Then
        assertThat(itemList).isNotNull();
        assertThat(itemList.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("5. Test update item operation")
    void givenSavedItem_whenUpdateItem_thenReturnUpdatedItem() {
        // Given
        Item item = new Item("5", "Monitor", "MN-01", "24 inch");
        itemRepository.save(item);

        // When
        Item savedItem = itemRepository.findById("5").get();
        savedItem.setName("Updated Monitor");
        savedItem.setDescription("27 inch 4K");
        Item updatedItem = itemRepository.save(savedItem); // save працює як update, якщо ID вже існує

        // Then
        assertThat(updatedItem.getName()).isEqualTo("Updated Monitor");
        assertThat(updatedItem.getDescription()).isEqualTo("27 inch 4K");
    }

    @Test
    @DisplayName("6. Test delete item by ID operation")
    void givenItemObject_whenDeleteById_thenRemoveItem() {
        // Given
        Item item = new Item("6", "Tablet", "TB-01", "10 inch");
        itemRepository.save(item);

        // When
        itemRepository.deleteById("6");
        Optional<Item> itemOptional = itemRepository.findById("6");

        // Then
        assertThat(itemOptional).isEmpty();
    }

    @Test
    @DisplayName("7. Test check if item exists by ID")
    void givenSavedItem_whenExistsById_thenReturnTrue() {
        // Given
        Item item = new Item("7", "Phone", "PH-01", "Smartphone");
        itemRepository.save(item);

        // When
        boolean exists = itemRepository.existsById("7");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("8. Test check non-existing item by ID")
    void givenNonExistentId_whenExistsById_thenReturnFalse() {
        // Given / When
        boolean exists = itemRepository.existsById("non-existing-id");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("9. Test count operation")
    void givenItems_whenCount_thenReturnTotalNumber() {
        // Given
        itemRepository.save(new Item("8", "Item A", "A-01", "Desc A"));
        itemRepository.save(new Item("9", "Item B", "B-01", "Desc B"));
        itemRepository.save(new Item("10", "Item C", "C-01", "Desc C"));

        // When
        long count = itemRepository.count();

        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("10. Test delete all operation")
    void givenItems_whenDeleteAll_thenCollectionIsEmpty() {
        // Given
        itemRepository.saveAll(List.of(
                new Item("11", "Item X", "X-01", "Desc X"),
                new Item("12", "Item Y", "Y-01", "Desc Y")
        ));

        // When
        itemRepository.deleteAll();
        long count = itemRepository.count();

        // Then
        assertThat(count).isZero();
    }
}
