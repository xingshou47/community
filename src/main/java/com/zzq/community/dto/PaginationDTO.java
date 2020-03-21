package com.zzq.community.dto;

import com.zzq.community.model.Question;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class PaginationDTO {
    private List<QuestionDTO> questions;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showEndPage;
    private int page;
    private List<Integer> pages = new ArrayList<>();
    private int totalPage;

    public void setPagination(Integer totalCount, Integer page, Integer size) {
        if (totalCount % size == 0){
            totalPage = totalCount/size;
        }else {
            totalPage = totalCount/size +1;
        }

        this.page = page;

        pages.add(page);
        for ( int i=1; i<=3; i++){
            if (page-i>0){
                pages.add(0,page-i);
            }

            if (page +i <= totalPage){
                pages.add(page+i);
            }
        }

        if (page == 1){
            showPrevious = false;
        }else {
            showPrevious =true;
        }
        if (page == totalPage){
            showNext = false;
        }else {
            showNext =true;
        }

        if (pages.contains(1)){
            showFirstPage = false;
        }else {
            showFirstPage = true;
        }

        if (pages.contains(totalPage)){
            showEndPage = false;
        }else {
            showEndPage = true;
        }
    }
}
