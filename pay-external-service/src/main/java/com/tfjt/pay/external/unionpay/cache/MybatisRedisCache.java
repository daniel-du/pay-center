package com.tfjt.pay.external.unionpay.cache;

import com.tfjt.pay.external.unionpay.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @class: MybatisRedisCache
 */
@Slf4j
public class MybatisRedisCache implements Cache {

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    // cache instance id
    private final String id;
    private RedisTemplate redisTemplate;
    // redis过期时间
    private static final long EXPIRE_TIME_IN_MINUTES = 30;

    public MybatisRedisCache(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    /**
     * Put query result to redis
     *
     * @param key
     * @param value
     */
    @Override
    public void putObject(Object key, Object value) {
        try {
            redisTemplate = getRedisTemplate();
            if (value != null) {
                redisTemplate.opsForValue().set(key.toString(), value, EXPIRE_TIME_IN_MINUTES, TimeUnit.MINUTES);
            }
            log.debug("Put query result to redis");
        } catch (Throwable t) {
            log.error("Redis put failed", t);
        }


    }

    /**
     * Get cached query result from redis
     *
     * @param key
     * @return
     */
    @Override
    public Object getObject(Object key) {
        try {
            redisTemplate = getRedisTemplate();
            log.debug("Get cached query result from redis");
            return redisTemplate.opsForValue().get(key.toString());
        } catch (Throwable t) {
            log.error("Redis get failed, fail over to db", t);
            return null;
        }
    }

    /**
     * Remove cached query result from redis
     *
     * @param key
     * @return
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object removeObject(Object key) {
        try {
            redisTemplate = getRedisTemplate();
            redisTemplate.delete(key.toString());
            log.debug("Remove cached query result from redis");
        } catch (Throwable t) {
            log.error("Redis remove failed", t);
        }
        return null;
    }

    /**
     * Clears this cache instance
     */
    @Override
    public void clear() {
        redisTemplate = getRedisTemplate();
        Set<String> keys = redisTemplate.keys("*:" + this.id + "*");
        if (!CollectionUtils.isEmpty(keys)) {
            redisTemplate.delete(keys);
        }
        log.debug("Clear all the cached query result from redis");
    }

    /**
     * This method is not used
     *
     * @return
     */
    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

    private RedisTemplate getRedisTemplate() {
        if (redisTemplate == null) {
            redisTemplate = (RedisTemplate) SpringUtil.getBean("redisTemplate");
        }
        return redisTemplate;
    }

}
