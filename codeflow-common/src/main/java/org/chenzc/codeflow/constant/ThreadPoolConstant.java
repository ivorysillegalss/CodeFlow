package org.chenzc.codeflow.constant;

/**
 * 线程池常见的常量信息
 *
 * @author chenz
 * @date 2024/11/13
 */
public class ThreadPoolConstant {
    /**
     * small
     */
    public static final Integer SINGLE_CORE_POOL_SIZE = 1;
    public static final Integer SINGLE_MAX_POOL_SIZE = 1;
    public static final Integer SMALL_KEEP_LIVE_TIME = 10;
    /**
     * medium
     */
    public static final Integer COMMON_CORE_POOL_SIZE = 2;
    public static final Integer COMMON_MAX_POOL_SIZE = 2;
    public static final Integer COMMON_KEEP_LIVE_TIME = 60;
    public static final Integer COMMON_QUEUE_SIZE = 128;
    /**
     * big queue size
     */
    public static final Integer BIG_QUEUE_SIZE = 1024;


    private ThreadPoolConstant() {
    }
}
