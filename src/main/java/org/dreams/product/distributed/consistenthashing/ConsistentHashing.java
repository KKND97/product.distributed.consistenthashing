package org.dreams.product.distributed.consistenthashing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * 此类主要目的:
 * 0、实现一致性hashcode的工具类
 * 1、允许动态增加、移除一致性算法中的node节点
 * 
 * @author <a href="mailto:liu_jing_wei@sohu.com">Dreams Liu </a>
 * @version $Revision 1.1 $ 2018年8月10日 上午8:40:23
 */
public class ConsistentHashing<T> { 

    /**
     * 日志输出类
     */
    private final static Log log = LogFactory.getLog(ConsistentHashing.class);

    /**
     * 任务锁
     */
    private ReentrantReadWriteLock rennlock = new ReentrantReadWriteLock();

    /**
     * 虚拟节点到真实节点的映射
     */
    private TreeMap<Long, Node<T>> nodeMap = new TreeMap<Long, Node<T>>();

    /**
     * 真实Node
     */
    private List<Node<T>> nodeList = new ArrayList<Node<T>>();

    /**
     * hash算法服务
     */
    private HashAlgorithm hashAlgorithm = new DefaultHashAlgorithm();

    /**
     * 构造函数
     */
    public ConsistentHashing() {
    }

    /**
     * 返回节点个数
     * @return
     */
    public int getNodeSize() {
        return nodeList.size();
    }

    /**
     * 返回虚拟节点个数
     * @return
     */
    public int getVirtualNodeSize() {
        return nodeMap.size();
    }

    /**
     * 动态增加Node
     * @param node
     */
    public void addHostNode(Node<T> node) {
        rennlock.writeLock()
                .lock();
        try {
            log.info("增加主机" + node + "的变化:");
            for (int i = 0; i < node.getWeight(); i++)
                nodeMap.put(hashAlgorithm.hashcode(node.getVirtualNodeName(i)), node);
            nodeList.add(node);
        } finally {
            rennlock.writeLock()
                    .unlock();
        }//end try

    }

    


    /**
     * 删除真实节点是
     * @param node
     */
    public void removeNode(Node<T> node) {
        if (node == null) {
            return;
        }
        rennlock.writeLock()
                .lock();

        try {

            log.info("删除主机" + node + "的变化:");
            for (int i = 0; i < node.getWeight(); i++) {
                //定位s节点的第i的虚拟节点的位置
                String virtualNodeName = node.getVirtualNodeName(i);
                nodeMap.remove(hashAlgorithm.hashcode(virtualNodeName));
            }//end for i
            nodeList.remove(node);

        } finally {
            rennlock.writeLock()
                    .unlock();
        }//end try
    }

    /**
     * 映射key到node
     * @param key
     * @return
     */
    public Node<T> getNodeByKey(String key) {
        
        rennlock.readLock()
                .lock();
        try {

            /*
             * 沿环的顺时针找到一个虚拟节点
             */
            SortedMap<Long, Node<T>> tail = nodeMap.tailMap(hashAlgorithm.hashcode(key));
            /*
             * 如果没有比当前key大的节点，因为是环形结构，就使用第一个节点
             */
            if (tail.size() == 0) {
                Entry<Long, Node<T>> firstEntry = nodeMap.firstEntry();
                if (firstEntry != null) {
                    return firstEntry.getValue();
                } else {
                    return null;
                }
            } else {
                return tail.get(tail.firstKey());
            }

        } finally {
            rennlock.readLock()
                    .unlock();
        }//end try
    }
    
    /**
     * 根据指定key获取node提供的资源
     * @param key
     * @return
     */
    public T getResourceByKey(String key) {
        return getNodeByKey(key).getResource();
    }

    public HashAlgorithm getHashAlgorithm() {
        return hashAlgorithm;
    }

    public void setHashAlgorithm(HashAlgorithm hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

}
