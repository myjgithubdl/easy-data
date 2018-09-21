package com.easydata.head;

import java.util.List;

/**
 * Created by MYJ on 2017/6/9.
 */
public class TheadColumnTree extends TheadColumn {

    /**
     * 等级深度
     */
    private Integer depth;

    /**
     * 跨行数
     */
    private Integer rowspan = 1;

    /**
     * 跨列数
     */
    private Integer colspan = 1;

    /**
     * 列所在的索引
     */
    private Integer celIndex;

    /**
     * 是否有孩子
     */
    private String hasChild = "N";

    /**
     * 父亲节点
     */
    private TheadColumnTree parentTheadColumnTree;

    /**
     * 孩子节点
     */
    private List<TheadColumnTree> childTheadColumnTrees;

    public TheadColumnTree() {
        super();
    }

    public TheadColumnTree(String id, String pid, String name, String text) {
        super(id, pid, name, text);
    }


    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public Integer getRowspan() {
        return rowspan;
    }

    public void setRowspan(Integer rowspan) {
        this.rowspan = rowspan;
    }

    public Integer getColspan() {
        return colspan;
    }

    public void setColspan(Integer colspan) {
        this.colspan = colspan;
    }

    public Integer getCelIndex() {
        return celIndex;
    }

    public void setCelIndex(Integer celIndex) {
        this.celIndex = celIndex;
    }

    public String getHasChild() {
        return hasChild;
    }

    public void setHasChild(String hasChild) {
        this.hasChild = hasChild;
    }

    public TheadColumnTree getParentTheadColumnTree() {
        return parentTheadColumnTree;
    }

    public void setParentTheadColumnTree(TheadColumnTree parentTheadColumnTree) {
        this.parentTheadColumnTree = parentTheadColumnTree;
    }

    public List<TheadColumnTree> getChildTheadColumnTrees() {
        return childTheadColumnTrees;
    }

    public void setChildTheadColumnTrees(List<TheadColumnTree> childTheadColumnTrees) {
        this.childTheadColumnTrees = childTheadColumnTrees;
    }

    /**
     * 单元对换
     *
     * @return
     */
    public TheadColumnTree unitSwap() {
        int rowspan = this.rowspan;
        this.rowspan = this.colspan;
        this.colspan = rowspan;

        int depth = this.depth;
        this.depth = this.celIndex + 1;
        this.celIndex = depth - 1;
        return this;
    }

}
