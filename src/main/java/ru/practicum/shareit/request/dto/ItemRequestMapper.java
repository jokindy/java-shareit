package ru.practicum.shareit.request.dto;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.request.ItemRequest;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemRequestMapper {

    private final ModelMapper modelMapper;

    public ItemRequestMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        setUp();
    }

    public ItemRequestDto toDto(ItemRequest itemRequest) {
        return modelMapper.map(itemRequest, ItemRequestDto.class);
    }

    public ItemRequest toDomain(ItemRequestDto itemRequestDto) {
        return modelMapper.map(itemRequestDto, ItemRequest.class);
    }

    public List<ItemRequestDto> toDtoList(Collection<ItemRequest> list) {
        return list.stream()
                .map(request -> modelMapper.map(request, ItemRequestDto.class))
                .collect(Collectors.toList());
    }

    private void setUp() {
        modelMapper.createTypeMap(ItemRequest.class, ItemRequestDto.class).setPostConverter(
                ctx -> {
                    ItemRequestDto dto = ctx.getDestination();
                    List<Item> items = ctx.getSource().getItems();
                    if (items != null) {
                        List<ItemShortDto> itemsShortDto = items.stream()
                                .map(item -> modelMapper.map(item, ItemShortDto.class))
                                .collect(Collectors.toList());
                        dto.setItems(itemsShortDto);
                    }
                    return dto;
                }
        );
    }
}
