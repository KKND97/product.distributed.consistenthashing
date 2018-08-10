package org.dreams.product.distributed.consistenthashing;

/*
* Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
*   - Redistributions of source code must retain the above copyright
*     notice, this list of conditions and the following disclaimer.
*
*   - Redistributions in binary form must reproduce the above copyright
*     notice, this list of conditions and the following disclaimer in the
*     documentation and/or other materials provided with the distribution.
*
*   - Neither the name of Oracle or the names of its
*     contributors may be used to endorse or promote products derived
*     from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
* IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
* THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
* PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
* CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
* EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
* PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
* PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
* LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
* NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dreams.product.distributed.consistenthashing.impls.HostNode;

public class ForkHash extends RecursiveAction {
    /**
     * 日志输出类
     */
    private final static Log log = LogFactory.getLog(ForkHash.class);

    private static final long serialVersionUID = -8032915917030559798L;

    private int start;
    private int finish;
    private Map<Node, AtomicInteger> status;
    private ConsistentHashing consistentHashing = null;
    private int threshold = 1000000;

    // Average pixels from source, write results into destination.
    protected void computeDirectly() {
        for (int index = start; index <= finish; index++) {
            String key = "key" + index;
            Node keyToHostNode = consistentHashing.keyToNode(key);
            AtomicInteger i = status.get(keyToHostNode);
            i.addAndGet(1);
        } //end for i 
    }

    @Override
    protected void compute() {
        if (finish - start <= threshold) {
            log.info(Thread.currentThread() + ":" + start + ":" + finish);
            computeDirectly();
            return;
        }

        int split = (start + finish) / 2;
        ForkHash left = new ForkHash();
        left.setStart(start);
        left.setFinish(split);
        left.setShared(consistentHashing);
        left.setStatus(status);

        ForkHash right = new ForkHash();
        right.setStart(split + 1);
        right.setFinish(finish);
        right.setShared(consistentHashing);
        right.setStatus(status);

        invokeAll(left, right);
    }

    // Plumbing follows.
    public static void main(String[] args) throws Exception {
        LinkedList<Node> nodeList = new LinkedList<Node>();
        Map<Node, AtomicInteger> status = new HashMap<Node, AtomicInteger>();
        int hostSize = 100;
        //int finish = Integer.MAX_VALUE;
        int finish = 1000000000;
        int begin = 0;
        int threshold = finish / 100;

        Options options = new Options();
        options.addOption("begin", "begin", true, "开始key的大小");
        options.addOption("finish", "finish", true, "结束key的大小");
        options.addOption("hostSize", "hostSize", true, "服务器个数");
        options.addOption("threshold", "threshold", true, "fork最小处理块行数");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmdline = parser.parse(options, args);

        if (args == null || args.length == 0) {
            // 打印命令行定义
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("product.distributed.consistenthashing", options);
        }

        if (cmdline.hasOption("begin")) {
            begin = Integer.parseInt(cmdline.getOptionValue("begin"));
        }
        if (cmdline.hasOption("finish")) {
            finish = Integer.parseInt(cmdline.getOptionValue("finish"));
        }
        if (cmdline.hasOption("hostSize")) {
            hostSize = Integer.parseInt(cmdline.getOptionValue("hostSize"));
        }
        if (cmdline.hasOption("threshold")) {
            threshold = Integer.parseInt(cmdline.getOptionValue("threshold"));
        }
        ConsistentHashing chash = new ConsistentHashing();
        for (int j = 0; j < hostSize; j++) {
            HostNode node = new HostNode();
            node.setName("host" + j);
            node.setHost("10.10.127." + j);
            node.setPort(6789);
            node.setWeight(10);
            status.put(node, new AtomicInteger(0));
            nodeList.add(node);
            chash.addHostNode(node);
        } //end for j

        log.info("getNodeSize:" + chash.getNodeSize());
        log.info("getVirtualNodeSize:" + chash.getVirtualNodeSize());

        ForkJoinPool pool = new ForkJoinPool();
        ForkHash forkhash = new ForkHash();
        forkhash.setStart(begin);
        forkhash.setFinish(finish);
        forkhash.setShared(chash);
        forkhash.setStatus(status);
        forkhash.setThreshold(threshold);

        pool.invoke(forkhash);
        long sum = 0;
        for (Node index : nodeList) {
            sum += status.get(index)
                         .get();
            log.info("index:" + index + ":" + status.get(index));
        } //end for nodeList
        log.info("sum:" + sum);
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getFinish() {
        return finish;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

    public Map<Node, AtomicInteger> getStatus() {
        return status;
    }

    public void setStatus(Map<Node, AtomicInteger> status) {
        this.status = status;
    }

    public ConsistentHashing getShared() {
        return consistentHashing;
    }

    public void setShared(ConsistentHashing consistentHashing) {
        this.consistentHashing = consistentHashing;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

}
