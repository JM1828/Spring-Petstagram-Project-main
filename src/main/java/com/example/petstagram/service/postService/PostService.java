package com.example.petstagram.service.postService;

import com.example.petstagram.dto.postDto.PostDTO;
import com.example.petstagram.entity.postEntity.PostEntity;
import com.example.petstagram.entity.userEntity.UserEntity;
import com.example.petstagram.repository.postRepository.PostRepository;
import com.example.petstagram.repository.userRepository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // 게시글 리스트 조회
    @Transactional(readOnly = true)
    public List<PostDTO> getPostList() {
        List<PostEntity> postEntityList = postRepository.findAllByOrderByIdDesc();

        return postEntityList.stream().map(PostDTO::toDTO).collect(Collectors.toList());
    }

    // 게시글 작성
    public void writePost(PostDTO dto) {
        // 현재 인증된 사용자의 이름(또는 이메일 등의 식별 정보) 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 현재 로그인한 사용자의 이름을 DB 에서 가져옴
        UserEntity userEntity = userRepository.findByEmail(username);


        // DTO -> Entity
        PostEntity postEntity = PostEntity.toEntity(dto);

        // 게시글에 사용자 할당
        userEntity.addPost(postEntity);

        // DB에 저장
        postRepository.save(postEntity);
    }

    // 게시글 상세보기
    @Transactional(readOnly = true)
    public PostDTO readPost(Long postId) {
        // 게시글 ID로 게시물 찾기
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시물을 찾을 수 없습니다. ID: " + postId));

        return PostDTO.toDTO(postEntity);
    }

    // 게시글 수정
    public PostDTO updatePost(Long postId, PostDTO postDTO) {
        // 게시글 ID로 게시물 찾기
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시물을 찾을 수 없습니다. ID: " + postId));

        // 현재 인증된 사용자의 이름(또는 이메일 등의 식별 정보) 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!postEntity.getPostAuthorId().getEmail().equals(username)) {
            throw new IllegalStateException("게시글 수정 권한이 없습니다.");
        }

        // 찾은 게시글의 내용을 업데이트
        postEntity.setPostContent(postDTO.getPostContent());

        postRepository.save(postEntity);

        return PostDTO.toDTO(postEntity);
    }

    // 게시글 삭제
    public void deletePost(Long postId) {
        // 현재 인증된 사용자의 이름(또는 이메일 등의 식별 정보) 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 게시글 ID로 게시물 찾기
        PostEntity postEntity = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("해당 게시물을 찾을 수 없습니다. ID: " + postId));

        // 게시글 소유자가 현재 인증된 사용자인지 확인
        if (!postEntity.getPostAuthorId().getEmail().equals(username)) {
            throw new IllegalStateException("게시글 삭제 권한이 없습니다.");
        }

        // 인증된 사용자가 소유자일 경우, 게시글 삭제
        postRepository.deleteById(postId);
    }
}
