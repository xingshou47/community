package com.zzq.community.service;

import com.zzq.community.Mapper.CommentMapper;
import com.zzq.community.Mapper.QuestionExtMapper;
import com.zzq.community.Mapper.QuestionMapper;
import com.zzq.community.Mapper.UserMapper;
import com.zzq.community.dto.CommentDTO;
import com.zzq.community.dto.CommentPaginationDTO;
import com.zzq.community.dto.QuestionDTO;
import com.zzq.community.dto.QuestionPaginationDTO;
import com.zzq.community.enums.CommentTypeEnum;
import com.zzq.community.exception.CustomizeErrorCode;
import com.zzq.community.exception.CustomizeException;
import com.zzq.community.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * @Transactional :回滚注解，当出现异常时，对整个方法进行回滚
     * @param comment 回复的实体类
     */
    @Transactional
    public void insert(Comment comment) {
        //判断评论的问题是否存在
        if (comment.getParentId() == null || comment.getParentId() == 0){
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        //判断评论类型是否存在
        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())){
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }
        if (comment.getType() == CommentTypeEnum.COMMENT.getType()){
            //回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null){
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insertSelective(comment);
        }else {
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question == null){
                throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
            }
            commentMapper.insertSelective(comment);
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);
        }
    }

    /**
     *
     * @param id 问题的id
     * @return 该问题的回复列表
     */
    public List<CommentDTO> listByQuestionId(Long id) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(CommentTypeEnum.QUESTION.getType());
        //拼接sql语句（以时间倒叙为排列顺序）
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        if (comments.size() == 0){
            return new ArrayList<>();
        }
        //获取所有评论者的id(除去了重复的id)
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(commentators);
        //根据id查询所有用户信息
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        //将List类型的user转换为Map类型k为id v是user
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));
        //将所有的回复封装成一个回复的传输类
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOS;
    }

    //未完成
    public CommentPaginationDTO list(Long QuestionId,Integer page, Integer size) {
        //这是再传输过程中的问题的实体类 除了问题List的集合 还有关于分页的属性
        CommentPaginationDTO paginationDTO = new CommentPaginationDTO();
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andParentIdEqualTo(QuestionId);
        Integer totalCount = Math.toIntExact(commentMapper.countByExample(commentExample));
        Integer totalPage =judgePagination(totalCount,page,size)[0];
        page = judgePagination(totalCount,page,size)[1];
        paginationDTO.setPagination(totalPage,page);
        //判断sql语句中的分页的初始值
        int offset = size * (page-1);
        if(offset <0){
            offset = 1;
        }
        CommentExample example = new CommentExample();
        example.createCriteria()
                .andParentIdEqualTo(QuestionId)
                .andTypeEqualTo(CommentTypeEnum.QUESTION.getType());
        example.setOrderByClause("gmt_create desc");
        List<Comment> comments = commentMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));
        paginationDTO.setComments(packaging(comments));
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
    //将拼装List<commentDTO>封装成一个方法
    public List<CommentDTO> packaging(List<Comment> comments){
        List<CommentDTO> commentDTOS = new ArrayList<>();
        for (Comment comment : comments){
            User user = userMapper.selectByPrimaryKey(comment.getCommentator());
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO);
            commentDTO.setUser(user);
            commentDTOS.add(commentDTO);
        }
        return commentDTOS;
    }
    //拼接单个commentDTO的方法
    public CommentDTO GetById(Long id) {
        Comment comment = commentMapper.selectByPrimaryKey(id);
        if (comment == null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        CommentDTO commentDTO = new CommentDTO();
        BeanUtils.copyProperties(comment,commentDTO);
        User user = userMapper.selectByPrimaryKey(comment.getCommentator());
        commentDTO.setUser(user);
        return commentDTO;
    }
}
