package org.dreams.product.distributed.consistenthashing;

import org.dreams.product.distributed.consistenthashing.Node;

/**
 * 
 * 此类主要目的:
 * 0、类封装了机器节点的信息 ，如name、password、host、port等
 * 
 * @author <a href="mailto:liu_jing_wei@sohu.com">Dreams Liu </a>
 * @version $Revision 1.1 $ 2018年8月10日 上午8:40:44
 */
public class MockNode implements Node<String> {
    /**
     * node名称
     */
    private String name;

    /**
     * node地址
     */
    private String host;

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

    public String getVirtualNodeName(int index) {
        String rv = "";
        rv = this.getName() + "#" + index;
        return rv;
    }
    
    @Override
    public String getResource() {
        return toString();
    }

    @Override
    public String toString() {
        return this.name + "-" + this.host + ":" + this.port + "#" + this.weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public float getFailureRate() {
        return failureRate;
    }

    public void setFailureRate(float failureRate) {
        this.failureRate = failureRate;
    }

    
}