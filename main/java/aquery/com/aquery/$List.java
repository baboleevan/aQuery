package aquery.com.aquery;

/**
 * A class to handle C++-like linked list, with head and tail
 */
public class $List<E> {
    E head;
    $List<E> tail;

    public $List() {
    }
    public $List(E head, $List<E> tail) {
        this.head = head;
        this.tail = tail;
    }

    public $List(E[] array) {
        for (int i=array.length-1;i>=0;i--)
            add(array[i]);
    }

    public E head() {
        return head;
    }
    public $List<E> tail() {
        return tail;
    }

    public boolean add(E object) {
        tail = new $List<E>(head,tail);
        head = object;
        return true;
    }

    public void clear() {
        head = null;
        tail = null;
    }

    public boolean contains(Object object) {
        if (object == head)
            return true;
        if (isEmpty())
            return false;
        return tail.contains(object);
    }

    public boolean isEmpty() {
        return (tail == null);
    }

    public E remove() {
        E res = head;
        head = tail.head;
        tail = tail.tail;
        return res;
    }

    public int size() {
        if (isEmpty())
            return 0;
        return 1+tail.size();
    }
}
