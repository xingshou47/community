package com.zzq.community.Controller;

import com.zzq.community.Mapper.QuestionMapper;
import com.zzq.community.Mapper.UserMapper;
import com.zzq.community.dto.PaginationDTO;
import com.zzq.community.dto.QuestionDTO;
import com.zzq.community.model.Question;
import com.zzq.community.model.User;
import com.zzq.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class IndexController {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    public String index(HttpServletRequest request,
                        Model model,
                        @RequestParam(name ="page",defaultValue = "1") Integer page,
                        @RequestParam(name ="size",defaultValue = "5") Integer size
                        ){
        //获取客户端的cookie的集合
        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies){
                //查询cookies中是否存在名为token的cookie
                if (cookie.getName().equals("token")){
                    String token = cookie.getValue();
                    //根据获得token到数据库中进行查询
                    User user = userMapper.findByToken(token);
                    if (user != null){
                        request.getSession().setAttribute("user",user);
                    }
                    break;
                }
            }
        }

        //获取列表信息
        PaginationDTO pagination = questionService.list(page,size);
        model.addAttribute("pagination",pagination);

        return "index";
    }
}
