package com.richfit.domain.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 树形结构的节点
 * Created by monday on 2016/12/30.
 */

public abstract class TreeNode {
    //实现树形结构的字段以及方法
    private int treeId;
    /*根节点的父节点pTreeId=0*/
    private int pTreeId = 0;
    private String name;
    /*树的层级*/
    private int level;
    /*是否是展开*/
    /*该行的类型，包括了父节点抬头(0),父节点(1),子节点抬头(2),子节点(3)*/
    private int ViewType;
    private boolean isExpand = false;
    private int icon;
    private TreeNode parent;
    private boolean hasChild = false;
    private List<TreeNode> children = new ArrayList<>();

    public boolean isHasChild() {
        return hasChild;
    }

    public void setHasChild(boolean hasChild) {
        this.hasChild = hasChild;
    }

    /**
     * 属否是根节点
     *
     * @return
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * 判断当前父节点的收缩状态
     *
     * @return
     */
    public boolean isParentExpand() {
        if (parent == null)
            return false;
        return parent.isExpand();
    }

    /**
     * 是否是叶子节点
     *
     * @return
     */
    public boolean isLeaf() {
        return children.size() == 0;
    }

    /**
     * 得到当前节点的层级
     *
     * @return
     */
    public int getLevel() {
        return parent == null ? 0 : parent.getLevel() + 1;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;

        if (!isExpand) {
            for (TreeNode node : children) {
                node.setExpand(false);
            }
        }
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getTreeId() {
        return treeId;
    }

    public void setTreeId(int treeId) {
        this.treeId = treeId;
    }

    public int getpTreeId() {
        return pTreeId;
    }

    public void setpTreeId(int pTreeId) {
        this.pTreeId = pTreeId;
    }

    public int getViewType() {
        return ViewType;
    }

    public void setViewType(int viewType) {
        ViewType = viewType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

}
