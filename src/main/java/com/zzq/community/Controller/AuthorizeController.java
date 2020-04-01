package com.zzq.community.Controller;

import com.zzq.community.model.User;
import com.zzq.community.dto.AccessTokenDTO;
import com.zzq.community.dto.GithubUser;
import com.zzq.community.prbvider.GithubProvider;
import com.zzq.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 授权Controller
 */
@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;
    @Autowired
    private UserService userService;


//    将配置文件中的信息导入Controller中
    @Value("${github.client.id}")
    private  String clientId;
    @Value("${github.client.secret}")
    private  String clientSecret;
    @Value("${github.redirect.uri}")
    private  String redirectUri;


    /**
     * Github OAuth的回调地址
     * @param code Github的用户的信息
     * @param state 不可猜测的随机字符串。它用于防止跨站点请求伪造攻击。
     * @return 返回主页
     */
    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String  state,
                           HttpServletRequest request,
                           HttpServletResponse response){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        String accessToken2 =  githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken2);
//        System.out.println(githubUser.getName());
        if (githubUser != null && githubUser.getId() != null){
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setAvatarUrl(githubUser.getAvatarUrl());
            userService.createOrUpdate(user);
            /**
             * Cookie和Session的区别
             * cookie是存储在本地浏览器
             * session存储在服务器
             *
             * cookie和session结合使用：
             * 1、存储在服务端：通过cookie存储一个session_id，
             *      然后具体的数据则是保存在session中。如果用户已经登录，则服务器会在cookie中保存一个session_id，
             *      下次再次请求的时候，会把该session_id携带上来，服务器根据session_id在session库中获取用户的session数据。
             *      就能知道该用户到底是谁，以及之前保存的一些状态信息。这种专业术语叫做server side session。
             *
             * 2、将session数据加密，然后存储在cookie中。这种专业术语叫做client side session。flask采用的就是这种方式，但是也可以替换成其他形式。
             * 优势：不会因为服务器的重启而导致再次重新登录
             */
            //向cookie中存储名字为token的信息
            response.addCookie(new Cookie("token",token));
            //登录成功，写cookie和session
//            request.getSession().setAttribute("user",githubUser);
//            如果想要重定向成功需要填写的是路径而不是html文件的名字
            return "redirect:/";
        }else {
            //登录失败，重新登录
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response){
        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "redirect:/";
    }
}
