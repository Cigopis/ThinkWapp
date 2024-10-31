package com.wongcoco.thinkwapp;

public class DoubleLinkedList {
    private Node head;
    private Node tail;

    private class Node {
        RegistrationData data;
        Node next;
        Node prev;

        Node(RegistrationData data) {
            this.data = data;
        }
    }

    public void add(RegistrationData data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }

    public RegistrationData getLast() {
        return tail != null ? tail.data : null;
    }
}
