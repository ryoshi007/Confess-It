package com.confessit;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Structure for handling batch-post removal
 * A tree graph structure of related posts are constructed
 */
public class TreeNode<T> {
    private T tagid = null;
    private List<TreeNode<T>> children = new ArrayList<>();
    private TreeNode<T> parent = null;

    /**
     * Constructor of the TreeNode
     * @param tagid is the id of the post
     */
    public TreeNode(T tagid) {
        this.tagid = tagid;
    }

    /**
     * Add a child node to the tree
     * @param tagid is the id of the post
     */
    public void addChild(T tagid) {
        TreeNode<T> newChild = new TreeNode<>(tagid);
        this.children.add(newChild);
    }

    /**
     * Add a child to the tree
     * @param child is the TreeNode object
     */
    public void addChild(TreeNode<T> child) {
        this.children.add(child);
    }

    /**
     * Add multiple child nodes to the tree
     * @param children is a list of child node
     */
    public void addChildren(List<TreeNode<T>> children) {
        for (TreeNode<T> node: children) {
            node.setParent(this);
        }
        this.children.addAll(children);
    }

    /**
     * Get the children of the tree
     * @return a list of child nodes
     */
    public List<TreeNode<T>> getChildren() {
        return children;
    }

    /**
     * Remove all children of the tree
     * Break the connection
     */
    public void removeAllChildren() {
        children = null;
    }

    /**
     * Get the tag id of the child node
     * @return tag id of the post
     */
    public T getTagID() {
        return tagid;
    }

    /**
     * Set the tag id of the child node
     * @param tagid is the id of the post
     */
    public void setTagID(T tagid) {
        this.tagid = tagid;
    }

    /**
     * Set the parent of the child nodes
     * @param parent is the node with higher hierarchy
     */
    public void setParent(TreeNode<T> parent) {
        this.parent = parent;
    }

    /**
     * Get the parent of the child nodes
     * @return the higher hierarchy node of the children nodes
     */
    public TreeNode<T> getParent() {
        return parent;
    }

    /**
     * Get the number of the child nodes in a parent node
     * @return the size of the array list consisting of child node
     */
    public int getChildrenSize() {
        return children.size();
    }

    /**
     * Check the parent node has child nodes or not
     * @return boolean value of parent node having child nodes or not
     */
    public boolean isEmptyChild() {
        return children.isEmpty();
    }

    /**
     * Get the child of the node by index
     * @param i is the index position of the child node in the array list
     * @return the child node
     */
    public TreeNode<T> getChildByIndex(int i) {
        return children.get(i);
    }

    /**
     * Get the tag ID
     * @return tag ID
     */
    public String toString() {
        return tagid.toString();
    }
}
