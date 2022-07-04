package ru.practicum.shareit.item.dto;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    private final ModelMapper modelMapper;

    public ItemMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        setUp();
    }

    public ItemDto toDto(Item item) {
        return modelMapper.map(item, ItemDto.class);
    }

    public Item toDomain(ItemDto itemDto) {
        return modelMapper.map(itemDto, Item.class);
    }

    public Item update(ItemDto itemDto, Item item) {
        modelMapper.map(itemDto, item);
        return item;
    }

    public List<ItemDto> toDtoList(Collection<Item> list) {
        return list.stream()
                .map(item -> modelMapper.map(item, ItemDto.class))
                .collect(Collectors.toList());
    }

    private void setUp() {
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
    }
}
