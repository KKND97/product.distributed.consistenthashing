package org.dreams.product.distributed.consistenthashing.impls;

import org.dreams.product.distributed.consistenthashing.Node;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * 此类主要目的:
 * 0、实现了redis的node节点
 * 1、提供jredis连接池维护
 * 
 * @author <a href="mailto:liujingwei@neusoft.com">Dreams Liu </a>
 * @version $Revision 1.1 $ 2018年8月10日 上午8:40:44
 */
public class RedisNode implements Node<Jedis> {
    /**
     * node名称
     */
    private String name;

    /**
     * node地址
     */
    private String address;

    /**
     * node端口
     */
    private int port = 0;

    /**
     * 权重（虚拟node的个数）
     */
    private int weight = 1;

    /**
     * node的索引顺序
     * 保留未使用
     */
    private String index = "";

    /**
     * 失败率
     * 保留未使用
     */
    private float failureRate = 0;
    
  /**
   * 访问密码
   */
    private String auth = "admin";

    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private int max_active = 1024;

    /**
     * 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
     */
    private int max_idle = 200;

    /**
     * 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
     */
    private int max_wait = 10000;
    /**
     * 
     */
    private int timeout = 10000;

    /**
     * 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
     */
    private boolean test_on_borrow = true;
    
    /**
     * 
     */
    private JedisPoolConfig config = new JedisPoolConfig();
    
    /**
     * 
     */
    private JedisPool jedisPool = null;
    
    /**
     * 初始化方法
     */
    public void init() {
        if (jedisPool == null) {
            config.setMaxTotal(max_active);
            config.setMaxIdle(max_idle);
            config.setMaxWaitMillis(max_wait);
            config.setTestOnBorrow(test_on_borrow);
            jedisPool = new JedisPool(this.config, this.address, this.port, this.timeout);
        }
    }
    

    public String getVirtualNodeName(int index) {
        String rv = "";
        rv = this.getName() + "#" + index;
        return rv;
    }

    @Override
    public String toString() {
        return this.name + "-" + this.address + ":" + this.port + "#" + this.weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getWeight() {
        return weight;
    }

    
    public float getFailureRate() {
        return failureRate;
    }

    public void setFailureRate(float failureRate) {
        this.failureRate = failureRate;
    }

    @Override
    public Jedis getResource() {
        return jedisPool.getResource();
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }


    public String getAuth() {
        return auth;
    }


    public void setAuth(String auth) {
        this.auth = auth;
    }


    public int getMax_active() {
        return max_active;
    }


    public void setMax_active(int max_active) {
        this.max_active = max_active;
    }


    public int getMax_idle() {
        return max_idle;
    }


    public void setMax_idle(int max_idle) {
        this.max_idle = max_idle;
    }


    public int getMax_wait() {
        return max_wait;
    }


    public void setMax_wait(int max_wait) {
        this.max_wait = max_wait;
    }


    public int getTimeout() {
        return timeout;
    }


    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }


    public boolean isTest_on_borrow() {
        return test_on_borrow;
    }


    public void setTest_on_borrow(boolean test_on_borrow) {
        this.test_on_borrow = test_on_borrow;
    }


    public void setWeight(int weight) {
        this.weight = weight;
    }
}