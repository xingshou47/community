package com.zzq.community.Controller;

import com.zzq.community.dto.CommentCreateDTO;
import com.zzq.community.dto.CommentDTO;
import com.zzq.community.dto.QuestionDTO;
import com.zzq.community.service.CommentService;
import com.zzq.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    private QuestionService questionService;
    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name ="id") Long id, Model model){
        QuestionDTO questionDTO = questionService.GetById(id);
        List<CommentDTO> comments = commentService.listByQuestionId(id);
        //增加浏览数
        questionService.inView(id);
        model.addAttribute("question",questionDTO);
        model.addAttribute("comments",comments);
        return "question";
    }

}
