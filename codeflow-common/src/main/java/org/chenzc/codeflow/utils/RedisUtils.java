package org.chenzc.codeflow.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.chenzc.codeflow.constant.CommonConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * 二次封装redisTemplate
 *
 * @author chenz
 * @date 2024/05/29
 */
@Component
@Slf4j
public class RedisUtils {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * set
     *
     * @param k
     * @param v
     * @return {@link Boolean }
     */
    public void set(String k, Object v) {
        redisTemplate.opsForValue().set(k, JSON.toJSONString(v));
    }

    /**
     * get
     * @param k
     * @return {@link String }
     */
    public String get(String k) {
        return redisTemplate.opsForValue().get(k);
    }

    /**
     * mGet将结果封装为Map
     *
     * @param keys
     */
    public Map<String, String> mGet(List<String> keys) {
        HashMap<String, String> result = new HashMap<>(keys.size());
        try {
            List<String> value = redisTemplate.opsForValue().multiGet(keys);
            if (CollUtil.isNotEmpty(value)) {
                for (int i = 0; i < keys.size(); i++) {
                    if (Objects.nonNull(value.get(i))) {
                        result.put(keys.get(i), value.get(i));
                    }

                }
            }
        } catch (Exception e) {
            log.error("RedisUtils#mGet fail! e:{}", Throwables.getStackTraceAsString(e));
        }
        return result;
    }

    /**
     * hGetAll
     *
     * @param key
     */
    public Map<Object, Object> hGetAll(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            log.error("RedisUtils#hGetAll fail! e:{}", Throwables.getStackTraceAsString(e));
        }
        return new HashMap<>(2);
    }

    /**
     * lRange
     *
     * @param key
     */
    public List<String> lRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("RedisUtils#lRange fail! e:{}", Throwables.getStackTraceAsString(e));
        }
        return new ArrayList<>();
    }

    /**
     * pipeline 设置 key-value 并设置过期时间
     */
    public void pipelineSetEx(Map<String, String> keyValues, Long seconds) {
        try {
            redisTemplate.executePipelined((RedisCallback<String>) connection -> {
                for (Map.Entry<String, String> entry : keyValues.entrySet()) {
                    connection.setEx(entry.getKey().getBytes(), seconds,
                            entry.getValue().getBytes());
                }
                return null;
            });
        } catch (Exception e) {
            log.error("RedisUtils#pipelineSetEx fail! e:{}", Throwables.getStackTraceAsString(e));
        }
    }


    /**
     * lpush 方法 并指定 过期时间
     */
    public void lPush(String key, String value, Long seconds) {
        try {
            redisTemplate.executePipelined((RedisCallback<String>) connection -> {
                connection.lPush(key.getBytes(), value.getBytes());
                connection.expire(key.getBytes(), seconds);
                return null;
            });
        } catch (Exception e) {
            log.error("RedisUtils#lPush fail! e:{}", Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * lLen 方法
     */
    public Long lLen(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("RedisUtils#lLen fail! e:{}", Throwables.getStackTraceAsString(e));
        }
        return 0L;
    }

    /**
     * lPop 方法
     */
    public String lPop(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            log.error("RedisUtils#lPop fail! e:{}", Throwables.getStackTraceAsString(e));
        }
        return "";
    }

    /**
     * pipeline 设置 key-value 并设置过期时间
     *
     * @param seconds 过期时间
     * @param delta   自增的步长
     */
    public void pipelineHashIncrByEx(Map<String, String> keyValues, Long seconds, Long delta) {
        try {
            redisTemplate.executePipelined((RedisCallback<String>) connection -> {
                for (Map.Entry<String, String> entry : keyValues.entrySet()) {
                    connection.hIncrBy(entry.getKey().getBytes(), entry.getValue().getBytes(), delta);
                    connection.expire(entry.getKey().getBytes(), seconds);
                }
                return null;
            });
        } catch (Exception e) {
            log.error("redis pipelineSetEX fail! e:{}", Throwables.getStackTraceAsString(e));
        }
    }

    /**
     * 执行指定的lua脚本返回执行结果
     * --KEYS[1]: 限流 key
     * --ARGV[1]: 限流窗口
     * --ARGV[2]: 当前时间戳（作为score）
     * --ARGV[3]: 阈值
     * --ARGV[4]: score 对应的唯一value
     *
     * @param redisScript
     * @param keys
     * @param args
     * @return
     */

//    运行lua脚本逻辑的真实方法
    public Boolean execLimitLua(RedisScript<Long> redisScript, List<String> keys, String... args) {

        try {
            Long execute = redisTemplate.execute(redisScript, keys, args);
            if (Objects.isNull(execute)) {
                return false;
            }
            return CommonConstant.TRUE.equals(execute.intValue());
        } catch (Exception e) {

            log.error("redis execLimitLua fail! e:{}", Throwables.getStackTraceAsString(e));
        }
        return false;
    }

    /**
     * 初始化lua脚本的配置
     *
     * @param resultType
     * @param path
     * @return {@link RedisScript }
     */
    public <T> DefaultRedisScript<T> initialRedisScript(Class<T> resultType, String path) {
        DefaultRedisScript<T> script = new DefaultRedisScript();
        script.setResultType(resultType);
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource(path)));
        return script;
    }
}
