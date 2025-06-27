package com.kataboard.services.implement;

import com.kataboard.dtos.tag.TagRequest;
import com.kataboard.dtos.tag.TagResponse;
import com.kataboard.exceptions.NotFoundException;
import com.kataboard.models.Tag;
import com.kataboard.repositories.TagRepository;
import com.kataboard.services.interfaces.ITagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements ITagService {

    private final TagRepository tagRepository;

    @Override
    public TagResponse create(TagRequest request) {
        if (tagRepository.findByNameIgnoreCase(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una etiqueta con ese nombre");
        }
        String color = request.getColor() != null && !request.getColor().isBlank()
                ? request.getColor()
                : "#808080";
        Tag tag = Tag.builder()
                .name(request.getName())
                .color(color)
                .build();

        return toResponse(tagRepository.save(tag));
    }

    @Override
    public List<TagResponse> getAll() {
        return tagRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public TagResponse update(Long id, TagRequest request) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Etiqueta no encontrada"));

        tag.setName(request.getName());
        tag.setColor(request.getColor());

        return toResponse(tagRepository.save(tag));
    }

    @Override
    public void delete(Long id) {
        tagRepository.deleteById(id);
    }

    private TagResponse toResponse(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .color(tag.getColor())
                .build();
    }
}
