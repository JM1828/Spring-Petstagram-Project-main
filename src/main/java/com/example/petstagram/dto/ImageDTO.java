package com.example.petstagram.dto;

import com.example.petstagram.entity.ImageEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageDTO {
    private Long id;
    private String imageUrl;
    private Long postId;

    // 민
    public static ImageDTO toDTO(ImageEntity imageEntity) {
        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setId(imageEntity.getId());
        imageDTO.setImageUrl(imageEntity.getImageUrl());
        imageDTO.setPostId(imageEntity.getPost().getId());
        return imageDTO;
    }
}
