package com.zzq.community.Mapper;

import com.zzq.community.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionMapper {

    @Insert("insert into question(title,description,gmt_create,gmt_modified,creator,tag,avatar_url)values(#{title},#{description},#{gmtCreate},#{gmtModified},#{creator},#{tag},#{avatarUrl})")
    void insert(Question question);

    @Select("select * from question limit #{offset}, #{size}")
    List<Question> list(int offset, Integer size);

    @Select("select count(1) from question")
    Integer count();
}
