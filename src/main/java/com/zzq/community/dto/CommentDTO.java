package com.zzq.community.dto;

import com.zzq.community.model.User;
import lombok.Data;

import java.time.Instant;
@Data
public class CommentDTO {
    private Long id;
    private Long parentId;
    private Integer type;
    private String content;
    private Long commentator;
    private Long gmtCreate;
    private Long gmtModified;
    private Integer likeCount;
    private User user;
}
