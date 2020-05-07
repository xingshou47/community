package com.zzq.community.Controller;

import com.zzq.community.dto.*;
import com.zzq.community.service.CommentService;
import com.zzq.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name ="id") Long id, Model model,
                           @RequestParam(name ="page",defaultValue = "1") Integer page,
                           @RequestParam(name ="size",defaultValue = "15") Integer size){
        QuestionDTO questionDTO = questionService.GetById(id);
        //增加浏览数
        questionService.inView(id);
        model.addAttribute("question",questionDTO);
//        System.out.println(questionDTO);
        //获取列表信息
        CommentPaginationDTO pagination = commentService.list(id,page,size);
//        System.out.println(pagination);
        model.addAttribute("pagination",pagination);
        return "question";
    }

}
