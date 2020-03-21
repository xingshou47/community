package com.zzq.community.service;

import com.zzq.community.Mapper.QuestionMapper;
import com.zzq.community.Mapper.UserMapper;
import com.zzq.community.dto.PaginationDTO;
import com.zzq.community.dto.QuestionDTO;
import com.zzq.community.model.Question;
import com.zzq.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;
//    这个是进行分页的功能的实现
    public PaginationDTO list(Integer page, Integer size) {
        //这是再传输过程中的问题的实体类 除了问题List的集合 还有关于分页的属性
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalCount = questionMapper.count();
        paginationDTO.setPagination(totalCount,page,size);
        //这是防止数据溢出
        if (page<1){
            page=1;
        }
        if (page>paginationDTO.getTotalPage()){
            page=paginationDTO.getTotalPage();
        }

        int offset = size * (page-1);
        List<Question> questions = questionMapper.list(offset,size);
        List<QuestionDTO> questionDTOS = new ArrayList<>();
        for (Question question : questions){
            User user = userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOS);
        return paginationDTO;
    }
}
