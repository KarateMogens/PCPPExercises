public interface BoundedBufferInteface<T> {
    public T take() throws Exception;

    public void insert(T elem) throws Exception;
}

