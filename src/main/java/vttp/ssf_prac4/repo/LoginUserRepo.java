package vttp.ssf_prac4.repo;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import vttp.ssf_prac4.util.Util;

@Repository
public class LoginUserRepo {
    
    @Autowired
    @Qualifier(Util.template)
    RedisTemplate<String, String> stringTemplate;

    public boolean hasKey(String key){
        return stringTemplate.hasKey(key);
    }

    public void storeLockedUser(String key){

        stringTemplate.opsForValue().setIfAbsent(key, key);
        Duration lockOutTime = Duration.ofSeconds(Util.durationSeconds);
        stringTemplate.opsForValue().getAndExpire(key, lockOutTime);
    }


}
