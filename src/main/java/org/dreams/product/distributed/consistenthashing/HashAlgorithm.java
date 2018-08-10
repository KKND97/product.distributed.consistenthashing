package org.dreams.product.distributed.consistenthashing;
/**
 * 
 * 此类主要目的:
 * 0、hashcode算法接口，具体计算方法由实现类实现
 * 1、一致性hash工具类使用此接口计算hashcode
 * 
 * @author <a href="mailto:liujingwei@neusoft.com">Dreams Liu </a>
 * @version $Revision 1.1 $ 2018年8月10日 上午8:41:26
 */

public interface HashAlgorithm {

    
    public abstract long hashcode(String key);

}