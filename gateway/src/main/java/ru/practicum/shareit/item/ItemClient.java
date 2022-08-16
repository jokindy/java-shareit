package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .build()
        );
    }

    public Object addItem(ItemDto itemDto, int userId) {
        return post("", userId, itemDto);
    }

    public Object updateItem(ItemDto itemDto, int itemId, int userId) {
        return patch("/" + itemId, userId, itemDto);
    }

    public Object get(int userId, int itemId) {
        return get("/" + itemId, userId);
    }

    public Object getAll(int userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", (long) userId, parameters);
    }

    public Object getBySearch(String text, int from, int size, int userId) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", (long) userId, parameters);
    }

    public Object addComment(CommentDto commentDto, int itemId, int userId) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}