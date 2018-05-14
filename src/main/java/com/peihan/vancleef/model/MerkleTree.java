package com.peihan.vancleef.model;

import com.peihan.vancleef.exception.OperateFailedException;
import com.peihan.vancleef.exception.VerifyFailedException;
import com.peihan.vancleef.exception.base.ServiceException;
import com.peihan.vancleef.util.HashUtil;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class MerkleTree {

    private Node rootNode;

    private List<String> data;

    public static MerkleTree buildMerkleTree(List<String> data) throws ServiceException {
        MerkleTree merkleTree = new MerkleTree(data);
        merkleTree.constructMerkleTree();
        return merkleTree;
    }

    private MerkleTree(List<String> data) {
        this.data = data;
    }

    private void constructMerkleTree() throws ServiceException {
        if (CollectionUtils.isEmpty(data)) {
            throw new VerifyFailedException("交易数据不能为空");
        }

        //构建底部节点
        List<Node> nodes = constructBottomNodes();

        if (CollectionUtils.isEmpty(nodes) || nodes.size() < 1) {
            throw new OperateFailedException("节点构建异常");
        }
        //从下往上循环构建整棵树
        while (nodes.size() > 1) {
            nodes = constructUpperNodes(nodes);
        }
        this.rootNode = nodes.get(0);
    }

    private List<Node> constructUpperNodes(List<Node> nodes) {

        List<Node> newNodes = new ArrayList<>((nodes.size() + 1) / 2);

        //奇数个结点复制最后一个
        if (nodes.size() % 2 != 0) {
            nodes.add(copyNode(nodes.get(nodes.size() - 1)));
        }

        //每两个构建一个新结点
        for (int i = 0; i < nodes.size(); i += 2) {
            Node node = constructNode(nodes.get(i), nodes.get(i + 1));
            newNodes.add(node);
        }
        return newNodes;
    }

    private Node constructNode(Node left, Node right) {
        Node parentNode = new Node();
        parentNode.setValue(HashUtil.hashHex(left.getValue() + right.getValue()));
        parentNode.setLeftNode(left);
        parentNode.setRightNode(right);
        return parentNode;
    }

    private List<Node> constructBottomNodes() {

        List<Node> nodes = new ArrayList<>(data.size());

        for (String datum : this.data) {
            if (!StringUtils.isEmpty(datum)) {
                nodes.add(constructLeafNode(datum));
            }
        }
        return nodes;
    }


    //构建叶子节点， 只有hash，没有左右孩子
    private Node constructLeafNode(String datum) {
        Node node = new Node();
        node.setValue(datum);
        return node;
    }


    private Node copyNode(Node node) {
        Node copyNode = new Node();
        copyNode.setValue(node.getValue());
        copyNode.setLeftNode(node.getLeftNode());
        copyNode.setRightNode(node.getRightNode());
        return copyNode;
    }


    @Data
    public class Node {

        private String value;

        private Node leftNode;

        private Node rightNode;


    }


}
