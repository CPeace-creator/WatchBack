package com.cjh.watching.watchback.utils;

/**
 * - @author Cjh。
 * - @date 2025/8/27 10:32。
 **/
/**
 * 分页参数封装类
 */
public class PageRequest {
    /**
     * 默认页面大小
     */
    private static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * 默认页码
     */
    private static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 页面大小
     */
    private Integer pageSize;

    /**
     * 无参构造函数，使用默认值
     */
    public PageRequest() {
        this.pageNum = DEFAULT_PAGE_NUM;
        this.pageSize = DEFAULT_PAGE_SIZE;
    }

    /**
     * 带参构造函数
     *
     * @param pageNum 页码
     * @param pageSize 页面大小
     */
    public PageRequest(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum != null && pageNum > 0 ? pageNum : DEFAULT_PAGE_NUM;
        this.pageSize = pageSize != null && pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE;
    }

    /**
     * 获取页码
     *
     * @return 页码
     */
    public Integer getPageNum() {
        return pageNum == null || pageNum <= 0 ? DEFAULT_PAGE_NUM : pageNum;
    }

    /**
     * 设置页码
     *
     * @param pageNum 页码
     */
    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    /**
     * 获取页面大小
     *
     * @return 页面大小
     */
    public Integer getPageSize() {
        return pageSize == null || pageSize <= 0 ? DEFAULT_PAGE_SIZE : pageSize;
    }

    /**
     * 设置页面大小
     *
     * @param pageSize 页面大小
     */
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 获取偏移量（用于数据库查询）
     *
     * @return 偏移量
     */
    public int getOffset() {
        return (getPageNum() - 1) * getPageSize();
    }

    /**
     * 获取限制数量（用于数据库查询）
     *
     * @return 限制数量
     */
    public int getLimit() {
        return getPageSize();
    }

    @Override
    public String toString() {
        return "PageRequest{" +
                "pageNum=" + getPageNum() +
                ", pageSize=" + getPageSize() +
                '}';
    }
}

