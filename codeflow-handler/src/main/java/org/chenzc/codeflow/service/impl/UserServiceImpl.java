package org.chenzc.codeflow.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.chenzc.codeflow.constant.RedisConstants;
import org.chenzc.codeflow.domain.BasicResult;
import org.chenzc.codeflow.domain.User;
import org.chenzc.codeflow.mapper.UserMapper;
import org.chenzc.codeflow.service.UserService;
import jakarta.annotation.Resource;
import org.chenzc.codeflow.constant.CommonConstant;
import org.chenzc.codeflow.utils.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisUtils redisUtils;

    @Override
    public BasicResult login(User user) {
        List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("username", user.getUsername())
                .eq("password", user.getPassword()));
        if (CollUtil.isEmpty(users)) {
            return BasicResult.fail("Invalid username or password");
        }
        redisUtils.set(StringUtils.join(RedisConstants.USER_SESSION, CommonConstant.INFIX, IdUtil.simpleUUID()), CollUtil.getFirst(users));
        return BasicResult.success("Succeeded");
    }
}
