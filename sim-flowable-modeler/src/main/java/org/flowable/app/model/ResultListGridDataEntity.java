package org.flowable.app.model;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: cyp
 * Date: 2018-02-23
 * Time: 10:55
 */
public class ResultListGridDataEntity {

    protected Integer page;
    protected Integer pageCount;
    protected Integer totalpage;
    protected Long total;
    protected List<? extends Object> rows;

    public ResultListGridDataEntity() {
    }

    public ResultListGridDataEntity(List<? extends Object> rows) {
        this.rows = rows;
        if(rows != null){
            this.pageCount = rows.size();
        }
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotalpage() {
        return totalpage;
    }

    public void setTotalpage(Integer totalpage) {
        this.totalpage = totalpage;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<? extends Object> getRows() {
        return rows;
    }

    public void setRows(List<? extends Object> rows) {
        this.rows = rows;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }
}
