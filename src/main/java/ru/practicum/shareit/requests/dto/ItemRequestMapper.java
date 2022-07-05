package ru.practicum.shareit.requests.dto;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.requests.ItemRequest;

import java.time.LocalDateTime;
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
                .map(booking -> modelMapper.map(booking, ItemRequestDto.class))
                .collect(Collectors.toList());
    }

    private void setUp() {
        TypeMap<ItemRequestDto, ItemRequest> propertyMapper = modelMapper.createTypeMap(ItemRequestDto.class,
                ItemRequest.class);
        propertyMapper.addMappings(
                modelMapper -> modelMapper.map(src -> LocalDateTime.now(), ItemRequest::setCreated)
        );
    }
}
