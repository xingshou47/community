package com.zzq.community.service;

import com.zzq.community.Mapper.QuestionExtMapper;
import com.zzq.community.Mapper.QuestionMapper;
import com.zzq.community.Mapper.UserMapper;
import com.zzq.community.dto.QuestionPaginationDTO;
import com.zzq.community.dto.QuestionDTO;
import com.zzq.community.exception.CustomizeErrorCode;
import com.zzq.community.exception.CustomizeException;
import com.zzq.community.model.Question;
import com.zzq.community.model.QuestionExample;
import com.zzq.community.model.User;
import org.apache.ibatis.session.RowBounds;
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
    @Autowired
    private QuestionExtMapper questionExtMapper;

    //这个是主页面中的分页功能的实现
    public QuestionPaginationDTO list(Integer page, Integer size) {
        //这是再传输过程中的问题的实体类 除了问题List的集合 还有关于分页的属性
        QuestionPaginationDTO paginationDTO = new QuestionPaginationDTO();
        Integer totalCount = Math.toIntExact(questionMapper.countByExample(new QuestionExample()));
        Integer totalPage =judgePagination(totalCount,page,size)[0];
        page = judgePagination(totalCount,page,size)[1];
        paginationDTO.setPagination(totalPage,page);
        //判断sql语句中的分页的初始值
        int offset = size * (page-1);
        if(offset <0){
            offset = 1;
        }
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset, size));
        paginationDTO.setQuestions(packaging(questions));
        return paginationDTO;
    }
    //显示个人信息中的问题列表中的分页功能
    public QuestionPaginationDTO list(Long userId, Integer page, Integer size) {
        //这是再传输过程中的问题的实体类 除了问题List的集合 还有关于分页的属性
        //创建一个where语句
        QuestionPaginationDTO paginationDTO = new QuestionPaginationDTO();
        QuestionExample questionExample = new QuestionExample();
        questionExample.or()
                .andCreatorEqualTo(userId);
        Integer totalCount = Math.toIntExact(questionMapper.countByExample(questionExample));
        Integer totalPage =judgePagination(totalCount,page,size)[0];
        page = judgePagination(totalCount,page,size)[1];
        paginationDTO.setPagination(totalPage,page);
        int offset = size * (page-1);
        if(offset <0){
            offset = 1;
        }
        //这个与questionExample仅仅是名字的不同
//        QuestionExample example = new QuestionExample();
//        example.or()
//                .andCreatorEqualTo(userId);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(questionExample, new RowBounds(offset, size));
        paginationDTO.setQuestions(packaging(questions));
        return paginationDTO;
    }
    //判断页面的页数和页码的值（此操作可能是不必须的）
    public Integer[] judgePagination(Integer totalCount,Integer page, Integer size){
        Integer[]  pagination = new Integer[2];
        if (totalCount % size == 0){
            pagination [0] = totalCount/size;
        }else {
            pagination [0] = totalCount/size +1;
        }
        //这是防止数据溢出
        if (page<1){
            page=1;
        }
        if (page>pagination [0]){
            page=pagination [0];
        }
        pagination[1] = page;
        return pagination;
    }
    //将拼装List<QuestionDTO>封装成一个方法
    public List<QuestionDTO> packaging(List<Question> questions){
        List<QuestionDTO> questionDTOS = new ArrayList<>();
        for (Question question : questions){
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }
        return questionDTOS;
    }
    //拼接单个QuestionDTO的方法
    public QuestionDTO GetById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        //当将question的值赋给questionDTO时 creator值没有赋值上
        //原因：question的属性从int变成了Long而DTO中的没有改变还是int导致数据类似不同而能赋值
        BeanUtils.copyProperties(question,questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }
    //创建信息和更新问题出现问题
    public void createOrUpdate(Question question) {
        if ( question.getId() == null){
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insertSelective(question);
        }else {
            question.setGmtModified(System.currentTimeMillis());
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());

            QuestionExample questionExample = new QuestionExample();
            questionExample.createCriteria()
                    .andCreatorEqualTo(question.getId());
           int update = questionMapper.updateByExample(updateQuestion, questionExample);
            if (update != 1){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }
    //增加一个问题的阅读数
    public void inView(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }
}
