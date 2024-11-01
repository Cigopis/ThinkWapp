package com.wongcoco.thinkwapp;

public class DoubleLinkedList<R> {
    private Node head;
    private Node tail;
    private int size = 0; // Menyimpan jumlah node dalam list

    private class Node {
        RegistrationData data;
        Node next;
        Node prev;

        Node(RegistrationData data) {
            this.data = data;
        }
    }

    // Menambahkan elemen ke akhir list
    public void add(RegistrationData data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++; // Perbarui ukuran
    }

    // Mendapatkan elemen terakhir dalam list
    public RegistrationData getLast() {
        return tail != null ? tail.data : null;
    }

    // Mengupdate data pada elemen terakhir
    public void updateLast(RegistrationData data) {
        if (tail != null) {
            tail.data = data;
        }
    }

    // Menghapus elemen terakhir dari list
    public void removeLast() {
        if (tail != null) {
            if (tail == head) { // Jika hanya ada satu elemen
                head = tail = null;
            } else {
                tail = tail.prev;
                tail.next = null;
            }
            size--; // Kurangi ukuran
        }
    }

    // Mendapatkan jumlah elemen dalam list
    public int size() {
        return size;
    }

    // Mengecek apakah list kosong
    public boolean isEmpty() {
        return size == 0;
    }
}
