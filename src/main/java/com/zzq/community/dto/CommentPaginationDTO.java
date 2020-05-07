package com.zzq.community.dto;

import lombok.Data;
import java.util.List;

@Data
public class CommentPaginationDTO extends PaginationDTO{
    private List<CommentDTO> comments;
}
