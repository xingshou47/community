package com.zzq.community.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Question implements Serializable {
    private int id;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private int creator;
    private int commentCount;
    private int viewCount;
    private int likeCount;
}
