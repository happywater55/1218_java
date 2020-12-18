package utils;

import redis.clients.jedis.Jedis;

public class MyRedis {
    private String key;
    private String value;
    public static Jedis jedis = new Jedis("localhost");

    public MyRedis() {
    }

    public MyRedis(String key, String value) {
        this.key = key;
        this.value = value;
        System.out.println("key:"+key);
        jedis.setex(key,50,value);
    }

}
//query:苹果from:autoto:en
