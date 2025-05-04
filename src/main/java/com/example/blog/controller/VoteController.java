package com.example.blog.controller;

import com.example.blog.dto.ApiResponse;
import com.example.blog.dto.request.VoteRequest;
import com.example.blog.dto.response.VoteResponse;
import com.example.blog.service.VoteService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@RequestMapping("/vote")
public class VoteController {
    VoteService voteService;

    @PostMapping("/{postId}")
    ApiResponse<VoteResponse> createVote(@PathVariable String postId, @RequestBody VoteRequest request){
        return ApiResponse.<VoteResponse>builder()
                .code(1000)
                .result(voteService.vote(postId, request))
                .build();
    }

    @GetMapping("/{postId}/upvotes")
    ApiResponse<Integer> countUpVotes(@PathVariable String postId){
        return ApiResponse.<Integer>builder()
                .code(1000)
                .result(voteService.countUpVotes(postId))
                .build();
    }

    @GetMapping("/{postId}/downvotes")
    ApiResponse<Integer> countDownVotes(@PathVariable String postId){
        return ApiResponse.<Integer>builder()
                .code(1000)
                .result(voteService.countDownVotes(postId))
                .build();
    }
}
