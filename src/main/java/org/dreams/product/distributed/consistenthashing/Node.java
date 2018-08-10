package org.dreams.product.distributed.consistenthashing;

/**
 * 
 * 此类主要目的:
 * 0、节点的信息和操作
 * 
 * @author <a href="mailto:liujingwei@neusoft.com">Dreams Liu </a>
 * @version $Revision 1.1 $ 2018年8月10日 上午8:40:44
 */
public interface Node {
    /**
     * 计算node的虚拟节点名称
     * @param node
     * @param num
     * @return
     */
    public String getVirtualNodeName(int index);
    
    public int getWeight();

}