package com.richfit.domain.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单树形节点
 * Created by monday on 2017/1/3.
 */

public class MenuNode {
    //该叶子的id
    private String id;
    //该叶子的父节点
    private String parentId;
    //节点名称
    private String caption;
    //节点编码
    private String functionCode;
    private String businessType;
    private String refType;
    private int icon;
    /**
     * 树的层级
     */
    private int level;
    /**
     * 是否是展开
     */
    private boolean isExpand = false;

    private MenuNode parent;
    private List<MenuNode> children = new ArrayList<>();


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }


    public MenuNode getParent() {
        return parent;
    }

    public void setParent(MenuNode parent) {
        this.parent = parent;
    }

    public List<MenuNode> getChildren() {
        return children;
    }

    public void setChildren(List<MenuNode> children) {
        this.children = children;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
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

    public void setLevel(int level) {
        this.level = level;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;

        if (!isExpand) {
            for (MenuNode node : children) {
                node.setExpand(false);
            }
        }
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    @Override
    public String toString() {
        return "MenuNode{" +
                "id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
                ", caption='" + caption + '\'' +
                ", functionCode='" + functionCode + '\'' +
                ", businessType='" + businessType + '\'' +
                ", refType='" + refType + '\'' +
                ", icon=" + icon +
                ", level=" + level +
                ", isExpand=" + isExpand +
                ", parent=" + parent +
                ", children=" + children +
                '}';
    }
}
