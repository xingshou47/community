package com.zzq.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class QuestionPaginationDTO extends PaginationDTO{
    private List<QuestionDTO> questions;
}
