package com.example.blog.service;

import com.example.blog.constaint.BanAction;
import com.example.blog.dto.request.PostRequest;
import com.example.blog.dto.response.PostAdminResponse;
import com.example.blog.dto.response.PostListResponse;
import com.example.blog.dto.response.PostUserResponse;
import com.example.blog.entity.Category;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.entity.UserBan;
import com.example.blog.exception.AppException;
import com.example.blog.exception.ErrorCode;
import com.example.blog.mapper.PostMapper;
import com.example.blog.repository.CategoryRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.repository.VoteRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {
    PostRepository postRepository;
    PostMapper postMapper;
    UserRepository userRepository;
    CategoryRepository categoryRepository;
    VoteRepository voteRepository;
    RedisService redisService;

    @CacheEvict(value = "post-all", allEntries = true)//Xóa cache sau khi tạo hoặc cập nhật
    public PostUserResponse createPost(PostRequest request){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Category category = categoryRepository.findById(request.getCategory())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_EXISTED));
        List<UserBan> userBans = new ArrayList<>(user.getBans());
        userBans.stream()
                .filter(ban -> ban.getBanAction() == BanAction.POST)
                .filter(ban -> ban.getExpiredAt() == null || ban.getExpiredAt().isAfter(LocalDateTime.now()))
                .findAny()
                .ifPresent(ban -> {
                    System.out.println("Now:" + LocalDateTime.now());
                    System.out.println("Expired_at:" + ban.getExpiredAt());
                    throw new AppException(ErrorCode.USER_BANNED_FROM_POSTING);
                });

        Post post = postMapper.toPost(request);
        post.setUser(user);
        post.setCategory(category);
        post.setCreateAt(LocalDateTime.now());
        post.setUpdateAt(null);

        post = postRepository.save(post);
//        redisService.deleteValue("post::all::*");//23/6 Không cần gọi redisService.deleteValue() vì @CacheEvict đã xử lý
        return postMapper.toPostUserResponse(post);
    }

    @Cacheable(value = "post-all", key = "'page::' + #page + '::size::' + #size", unless = "#result == null")//Lưu kết quả vào redis
    public PostListResponse getAllPostsForUser(int page, int size){
        String redisKey = "post::all::page::" + page + "::size::" + size;
        PostListResponse cached = redisService.getValue(redisKey, PostListResponse.class);
        if(cached != null){
            return cached;
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findAll(pageable);
        Page<PostUserResponse> responsePage = postPage.map(this::buildPostUserResponse);

        PostListResponse responseList = PostListResponse.builder()
                .posts(responsePage.getContent())
                .currentPage(responsePage.getNumber())
                .totalPages(responsePage.getTotalPages())
                .pageSize(responsePage.getSize())
                .totalElements((int) responsePage.getTotalElements())
                .build();
//        List<PostUserResponse> responseList = postRepository.findAll().stream()
//                .map(this::buildPostUserResponse)
//                .toList();
        redisService.setValue(redisKey, responseList, 10);
        return responseList;
//        return postRepository.findAll().stream()
//                .map(this::buildPostUserResponse)//sử dụng hàm dưới
//                .toList();
    }

    public List<PostAdminResponse> getAllPostForAdmin(){
        return postRepository.findAll().stream()
                .map(postMapper::toPostAdminResponse)
                .toList();
    }

    public PostUserResponse getPostForUserById(String id){
//        String redisKey = "post:" + id;
//        PostUserResponse cached = redisService.getValue(redisKey, PostUserResponse.class);
//        if(cached != null){
//            return cached;
//        }

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        PostUserResponse response = buildPostUserResponse(post);
//        redisService.setValue(redisKey, response, 10);
        return response;
//        return buildPostUserResponse(post); //Sử dụng hàm dưới, không dùng mapper???
    }

    public PostAdminResponse getPostForAdminById(String id){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        return postMapper.toPostAdminResponse(post);
    }

    public PostUserResponse updatePost(String id, PostRequest request){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!post.getUser().getUsername().equals(username)){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        User user = post.getUser();
        user.getBans().stream()
                .filter(ban -> ban.getBanAction() == BanAction.POST)
                .filter(ban -> ban.getExpiredAt() == null || ban.getExpiredAt().isAfter(LocalDateTime.now()))
                .findAny()
                .ifPresent(ban -> {
                    throw new AppException(ErrorCode.USER_BANNED_FROM_POSTING);
                });

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUpdateAt(LocalDateTime.now());

        return postMapper.toPostUserResponse(postRepository.save(post));
    }

    public void deletePost(String id){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean isOwner = post.getUser().getUsername().equals(username);
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));

        if(!isOwner && !isAdmin){
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        postRepository.delete(post);
    }

    public PostListResponse getAllPostsByTitle(String title, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findByTitle(title, pageable);
        if(posts.isEmpty()){
            throw new AppException(ErrorCode.POST_NOT_EXISTED);
        }
        List<PostUserResponse> responseList = posts.stream()
                .map(postMapper::toPostUserResponse)
                .collect(Collectors.toList());

        return PostListResponse.builder()
                .posts(responseList)
                .currentPage(posts.getNumber())
                .totalPages(posts.getTotalPages())
                .totalElements((int) posts.getTotalElements())
                .pageSize(posts.getSize())
                .build();
    }

    private PostUserResponse buildPostUserResponse(Post post){
        int upvotes = voteRepository.countByPostAndValue(post, 1);
        int downvotes = voteRepository.countByPostAndValue(post, -1);

        return PostUserResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .username(post.getUser().getUsername())
                .createAt(post.getCreateAt())
                .categoryName(post.getCategory() != null ? post.getCategory().getName() : null)
                .upvotes(upvotes)
                .downvotes(downvotes)
                .build();
    }
}