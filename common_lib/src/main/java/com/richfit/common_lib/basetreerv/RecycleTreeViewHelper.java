package com.richfit.common_lib.basetreerv;


import com.richfit.domain.bean.TreeNode;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by monday on 2016/5/27.
 */

public class RecycleTreeViewHelper {
    /**
     * 设置各个节点之间的关系(只针对RefDetailEntity数据)
     */
    public static <T extends TreeNode> ArrayList<T> convertedTreeData(List<T> targetData) {
        ArrayList<T> nodes = new ArrayList<>();
        nodes.addAll(targetData);
        for (int i = 0; i < nodes.size(); i++) {
            T n = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); j++) {
                T m = nodes.get(j);

                if (m.getpTreeId() == n.getTreeId()) {
                    n.getChildren().add(m);
                    m.setParent(n);
                } else if (m.getTreeId() == n.getpTreeId()) {
                    m.getChildren().add(n);
                    n.setParent(m);
                }
            }
        }
        return nodes;
    }


    public static <T extends TreeNode>  ArrayList<T> getSortedNodes(List<T> datas,
                                                            int defaultExpandLevel) {
        ArrayList<T> result = new ArrayList<>();
        ArrayList<T> nodes = convertedTreeData(datas);
        // 获得树的根结点
        ArrayList<T> rootNodes = getRootNodes(nodes);
        for (T node : rootNodes) {
            addNode(result, node, defaultExpandLevel, 1);
        }
        return result;
    }

    /**
     * 把一个父节点的所有孩子节点都放入result
     *
     * @param result
     * @param node
     * @param defaultExpandLevel
     * @param currentLevel
     */
    private static <T extends TreeNode> void addNode(List<T> result, T node,
                                int defaultExpandLevel, int currentLevel) {
        result.add(node);
        if (defaultExpandLevel >= currentLevel) {
            node.setExpand(true);
        }
        if (node.isLeaf())
            return;

        for (int i = 0; i < node.getChildren().size(); i++) {
            addNode(result, (T) node.getChildren().get(i), defaultExpandLevel,
                    currentLevel + 1);
        }
    }

    /**
     * 过滤出可见的节点
     *
     * @param nodes
     * @return
     */
    public static <T extends TreeNode> List<T> filterVisibleNodes(List<T> nodes) {
        List<T> result = new ArrayList<>();
        for (T node : nodes) {
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
    private static <T extends TreeNode> ArrayList<T> getRootNodes(ArrayList<T> nodes) {
        ArrayList<T> root = new ArrayList<>();
        for (T node : nodes) {
            if (node.isRoot()) {
                root.add(node);
            }
        }
        return root;
    }


//    /**
//     * 为Node设置图标
//     *
//     * @param n
//     */
//    private static void setNodeIcon(RefDetailEntity n) {
//        if (n.getChildren().size() > 0 && n.isExpand()) {
//            n.setIcon(R.mipmap.tree_ex);
//        } else if (n.getChildren().size() > 0 && !n.isExpand()) {
//            n.setIcon(R.mipmap.tree_ec);
//        } else {
//            n.setIcon(-1);
//        }
//    }
}
