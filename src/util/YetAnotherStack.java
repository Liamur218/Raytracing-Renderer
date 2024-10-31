package util;

import java.util.Stack;

public class YetAnotherStack<T> extends Stack<T>  {
    public T getFromEnd(int index) {
        return get(size() - index - 1);
    }

    public void removeLast(T element) {
        for (int i = size() - 1; i > -1; i--) {
            if (element.equals(get(i))) {
                remove(i);
                return;
            }
        }
    }
}
