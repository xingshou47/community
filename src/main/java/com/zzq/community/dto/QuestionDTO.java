package com.zzq.community.dto;

import com.zzq.community.model.User;
import lombok.Data;

@Data
public class QuestionDTO {
    private Long id;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private Long creator;
    private int commentCount;
    private int viewCount;
    private int likeCount;
    private User user;
}
