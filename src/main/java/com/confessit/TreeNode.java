package com.confessit;

import java.util.ArrayList;
import java.util.List;

/***
 * Data Structure for handling batch-post removal
 * A tree graph structure of related posts are constructed
 */
public class TreeNode<T> {
    private T tagid = null;
    private List<TreeNode> children = new ArrayList<>();
    private TreeNode parent = null;

    /***
     * Constructor of the TreeNode
     * @param tagid is the id of the post
     */
    public TreeNode(T tagid) {
        this.tagid = tagid;
    }

    /***
     * Add a child node to the tree
     * @param tagid is the id of the post
     */
    public void addChild(T tagid) {
        TreeNode<T> newChild = new TreeNode<>(tagid);
        this.children.add(newChild);
    }

    /***
     * Add multiple child nodes to the tree
     * @param children is a list of child node
     */
    public void addChildren(List<TreeNode> children) {
        for (TreeNode node: children) {
            node.setParent(this);
        }
        this.children.addAll(children);
    }

    /***
     * Get the children of the tree
     * @return a list of child nodes
     */
    public List<TreeNode> getChildren() {
        return children;
    }

    /***
     * Get the tag id of the child node
     * @return tag id of the post
     */
    public T getTagID() {
        return tagid;
    }

    /***
     * Set the tag id of the child node
     * @param tagid is the id of the post
     */
    public void setTagID(T tagid) {
        this.tagid = tagid;
    }

    /***
     * Set the parent of the child nodes
     * @param parent is the node with higher hierarchy
     */
    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    /***
     * Get the parent of the child nodes
     * @return the higher hierarchy node of the children nodes
     */
    public TreeNode getParent() {
        return parent;
    }

    /***
     * Get the number of the child nodes in a parent node
     * @return the size of the array list consisting of child node
     */
    public int getChildrenSize() {
        return children.size();
    }

    /***
     * Check the parent node has child nodes or not
     * @return boolean value of parent node having child nodes or not
     */
    public boolean isEmptyChild() {
        return children.isEmpty();
    }

    /***
     * Get the child of the node by index
     * @param i is the index position of the child node in the array list
     * @return the child node
     */
    public TreeNode getChildByIndex(int i) {
        return children.get(i);
    }

}
