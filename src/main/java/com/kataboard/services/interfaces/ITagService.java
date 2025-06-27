package com.kataboard.services.interfaces;

import com.kataboard.dtos.tag.TagRequest;
import com.kataboard.dtos.tag.TagResponse;

import java.util.List;

public interface ITagService {
    TagResponse create(TagRequest request);
    List<TagResponse> getAll();
    TagResponse update(Long id, TagRequest request);
    void delete(Long id);
}