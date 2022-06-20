package com.confessit;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Class for own implementation of queue
 */
public class Queue {
    private LinkedList<Integer> list;

    /**
     * Accept an array of elements and then add each of them into the queue
     * @param e is an array of tag id (Integer)
     */
    public Queue(Integer[] e) {
        list = new LinkedList<>();
        for (int element: e) {
            enqueue(element);
        }
    }

    /**
     * Empty constructor of the Queue
     */
    public Queue() {
        list = new LinkedList<>();
    }

    /**
     * Add an element into the queue
     * @param e is the tag id that will be inserted
     */
    public void enqueue(int e) {
        list.add(e);
    }

    /**
     * Enqueue multiple elements into the queue
     * @param tagList is an array list of tag id consisting all approved but not yet displayed posts
     */
    public void enqueue(ArrayList<Integer> tagList) {
        for (Integer i : tagList) {
            list.add(i);
        }
    }

    /**
     * Remove and get the first element of the queue
     * @return the first tag id in the queue
     */
    public int dequeue() {
        return list.removeFirst();
    }

    /**
     * Get the size of the queue
     * @return the integer amount of the queue size
     */
    public int getSize() {
        return list.size();
    }

    /**
     * Check if the queue is empty
     * @return the boolean value in which true if queue is empty, false otherwise
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * Clear the queue
     */
    public void clear() {
        list.clear();
    }

    /**
     * Obtain the string representation of the queue
     * @return the string of the queue
     */
    public String toString() {
        return "Queue: " + list.toString();
    }

}
