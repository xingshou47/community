package com.zzq.community.Controller;

import com.zzq.community.pojo.AccessToken;
import com.zzq.community.pojo.GithubUser;
import com.zzq.community.prbvider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 授权Controller
 */
@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;
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
                           @RequestParam(name = "state") String  state){
        AccessToken accessToken = new AccessToken();
        accessToken.setClient_id(clientId);
        accessToken.setClient_secret(clientSecret);
        accessToken.setCode(code);
        accessToken.setRedirect_uri(redirectUri);
        accessToken.setState(state);
        String accessToken2 =  githubProvider.getAccessToken(accessToken);
        GithubUser user = githubProvider.getUser(accessToken2);
        System.out.println(user.getName());
        return "index";
    }
}
