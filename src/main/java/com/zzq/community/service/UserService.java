package com.zzq.community.service;

import com.zzq.community.Mapper.UserMapper;
import com.zzq.community.model.User;
import com.zzq.community.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    //创建或更新数据库中的用户信息
    public void createOrUpdate(User user) {
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(userExample);
        if (users.size() == 0){
            //插入
//            System.currentTimeMillis()获取当前时间
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        }else {
            //获取需要更新的用户id
            User dbUser = users.get(0);
            //创建需要更新的信息
            User updateUser = new User();
            updateUser.setGmtModified(System.currentTimeMillis());
            updateUser.setAvatarUrl(user.getAvatarUrl());
            updateUser.setName(user.getName());
            updateUser.setToken(user.getToken());

            UserExample example = new UserExample();
            example.createCriteria()
                    .andIdEqualTo(dbUser.getId());
            //此方法是更新User中不为空的信息 是一个动态SQL语句 example是 where id = #{id}
            userMapper.updateByExampleSelective(updateUser, example);
            // 更新

        }
    }
}
