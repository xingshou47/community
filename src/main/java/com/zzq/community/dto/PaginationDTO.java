package com.zzq.community.dto;

import java.util.ArrayList;
import java.util.List;

public class PaginationDTO {
    public boolean showPrevious;
    public boolean showFirstPage;
    public boolean showNext;
    public boolean showEndPage;
    public int page;
    public List<Integer> pages = new ArrayList<>();
    public int totalPage;

    public void setPagination(Integer totalPage, Integer page) {
        this.totalPage =totalPage;
        this.page = page;

        pages.add(page);
        for ( int i=1; i<=3; i++){
            if (page-i>0){
                pages.add(0,page-i);
            }

            if (page +i <= this.totalPage){
                pages.add(page+i);
            }
        }

        if (page == 1){
            showPrevious = false;
        }else {
            showPrevious =true;
        }
        if (page == this.totalPage){
            showNext = false;
        }else {
            showNext =true;
        }

        if (pages.contains(1)){
            showFirstPage = false;
        }else {
            showFirstPage = true;
        }

        if (pages.contains(this.totalPage)){
            showEndPage = false;
        }else {
            showEndPage = true;
        }
    }
}
