package com.richfit.common_lib.utils;

import android.util.Log;

import com.richfit.domain.bean.MenuNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 树形菜单帮助类，封装生成树形节点的方法
 * Created by monday on 2017/1/3.
 */

public class MenuTreeHelper {

    public static List<MenuNode> convertDatas2Nodes(List<MenuNode> datas) {
        /**
         * 设置Node间的节点关系
         */
        for (int i = 0; i < datas.size(); i++) {
            MenuNode n = datas.get(i);
            for (int j = i + 1; j < datas.size(); j++) {
                MenuNode m = datas.get(j);
                if (m.getParentId().equals(n.getId())) {
                    n.getChildren().add(m);
                    m.setParent(n);
                } else if (m.getId().equals(n.getParentId())) {
                    m.getChildren().add(n);
                    n.setParent(m);
                }
            }
        }
        return datas;
    }

    public static List<MenuNode> getSortedNodes(List<MenuNode> datas,
                                                int defaultExpandLevel) {
        List<MenuNode> result = new ArrayList<MenuNode>();
        List<MenuNode> nodes = convertDatas2Nodes(datas);
        // 获得树的根结点
        List<MenuNode> rootNodes = getRootNodes(nodes);

        for (MenuNode node : rootNodes) {
            addNode(result, node, defaultExpandLevel, 1);
        }

        Log.e("TAG", result.size() + "");
        return result;
    }

    public static List<MenuNode> getNodesByLevel(List<MenuNode> nodes,String parentId) {
        List<MenuNode> menuNodes = new ArrayList<>();
        for (MenuNode node : nodes) {
            if(node.getParentId().equals(parentId)) {
                menuNodes.add(node);
            }
        }
        return menuNodes;
    }

    /**
     * 把一个节点的所有孩子节点都放入result
     *
     * @param result
     * @param node
     * @param defaultExpandLevel
     * @param currentLevel
     */
    private static void addNode(List<MenuNode> result, MenuNode node,
                                int defaultExpandLevel, int currentLevel) {
        result.add(node);
        if (defaultExpandLevel >= currentLevel) {
            node.setExpand(true);
        }
        if (node.isLeaf())
            return;

        for (int i = 0; i < node.getChildren().size(); i++) {
            addNode(result, node.getChildren().get(i), defaultExpandLevel,
                    currentLevel + 1);
        }
    }

    /**
     * 过滤出可见的节点
     *
     * @param nodes
     * @return
     */
    public static List<MenuNode> filterVisibleNodes(List<MenuNode> nodes) {
        List<MenuNode> result = new ArrayList<>();

        for (MenuNode node : nodes) {
            if (node.isRoot() || node.isParentExpand()) {
//                setNodeIcon(node);
                result.add(node);
            }
        }
        return result;
    }

    /**
     * 从所有节点中过滤出根节点
     *
     * @param nodes
     * @return
     */
    private static List<MenuNode> getRootNodes(List<MenuNode> nodes) {
        List<MenuNode> root = new ArrayList<>();
        for (MenuNode node : nodes) {
            if (node.isRoot()) {
                root.add(node);
            }
        }
        return root;
    }

}
