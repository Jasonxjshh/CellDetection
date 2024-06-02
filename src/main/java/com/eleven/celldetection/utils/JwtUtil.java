package com.eleven.celldetection.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.eleven.celldetection.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class JwtUtil {
    /**
     * 静态方法调用非静态接口层(Service层)
     */
    public static JwtUtil jwtUtil; //声明对象

    @PostConstruct //初始化
    public void init() {
        jwtUtil = this;
        jwtUtil.userService = this.userService;
    }

    @Autowired //注入
    UserService userService;


    /**
     * 过期时间30分钟
     */
    private static final long EXPIRE_TIME = 60L * 60 * 1000 * 100000;//自定义修改
    /**
     * jwt 密钥
     */
    private static final String SECRET = "Jason";//自定义修改

    /**
     * 生成签名，30分钟后过期
     *
     * @param userId
     * @return
     */
    public static String sign(Long userId, int role) {
        try {
            //过期时间
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            //私钥及加密算法
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            //附带username和userID生成签名，可自定义附带信息，这里是用户ID和用户名称
            return JWT.create()
                    // 将 user id 保存到 token 里面
                    .withClaim("id", userId)
                    .withClaim("role", role)
                    // 分钟后token过期
                    .withExpiresAt(date)
                    // token 的密钥
                    .sign(algorithm);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 校验token
     *
     * @param token
     * @return
     */
    public static boolean checkSign(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            // 验证token
            DecodedJWT jwt = verifier.verify(token);
            String subject = jwt.getSubject();
            List<String> audience = jwt.getAudience();
			// 获取附带信息，并进行自定义验证，这里是验证根据userid查出来的用户名与token中附带的用户名是否一致
            Map<String, Claim> claims = jwt.getClaims();
            Long userId = claims.get("id").asLong();
            int role = claims.get("role").asInt();
            Long id = jwtUtil.userService.getById(userId).getId();
            int r = jwtUtil.userService.getById(userId).getRole();
            if (!Objects.equals(id, userId)) {
                throw new RuntimeException("token无效，请重新登录");
            }
            if (role < r) {
                throw new RuntimeException("权限不足，请重新登录");
            }
            return true;
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("无效token，请重新获取");
        }
    }

	 /**
      * 获取token携带信息
      *
      * @param token
      * @param name  附带信息名称
      * @return
      */
	public static Claim getTokenClaims(String token, String name){
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            Map<String, Claim> claims = jwt.getClaims();
            return claims.get(name);
        }catch (JWTVerificationException exception) {
            return null;
        }
    }

    public static String parseToken(String token){
        if (token == null){
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(token);
        return  (String) jsonObject.get("token");
    }
}
