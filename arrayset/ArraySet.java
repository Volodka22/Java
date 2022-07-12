package info.kgeorgiy.ja.smaglii.arrayset;

import java.util.*;

public class ArraySet<T> extends AbstractSet<T> implements NavigableSet<T> {

    private final Comparator<T> comparator;
    private final List<T> list;
    private final List<T> reverseList;

    private ArraySet(List<T> list, List<T> reverseList, Comparator<T> comparator) {
        this.comparator = comparator;
        this.list = list;
        this.reverseList = reverseList;
    }

    public ArraySet(Collection<T> list, Comparator<T> comparator) {
        TreeSet<T> set = new TreeSet<>(comparator);
        set.addAll(list);
        this.list = List.copyOf(set);
        this.comparator = comparator;
        this.reverseList = new ReverseList<>(this.list);
    }


    public ArraySet(SortedSet<T> set) {
        this(set, null);
    }

    public ArraySet(SortedSet<T> set, Comparator<T> comparator) {
        this.list = List.copyOf(set);
        this.comparator = comparator;
        this.reverseList = new ReverseList<>(this.list);
    }

    public ArraySet(Collection<T> list) {
        this(list, null);
    }

    public ArraySet(Comparator<T> comparator) {
        this(List.of(), comparator);
    }

    public ArraySet() {
        this(List.of(), null);
    }

    private int binarySearch(T element) {
        return Collections.binarySearch(list, element, comparator);
    }

    private int getIndex(T element, int addIfFound, int addIfNotFound) {
        int ans = binarySearch(element);
        if (ans < 0){
            ans = -ans - 1 + addIfNotFound;
        } else {
            ans = ans + addIfFound;
        }
        return ans;
    }

    private int lowerIndex(T element) {
        return getIndex(element, -1, -1);
    }

    private int floorIndex(T element) {
        return getIndex(element, 0, -1);
    }

    private int cellingIndex(T element) {
        return getIndex(element, 0, 0);
    }

    private int higherIndex(T element) {
        return getIndex(element, 1, 0);
    }

    private boolean isIndexIncorrect(int index) {
        return index < 0 || index >= size();
    }

    private T getElement(int index) {
        if (isIndexIncorrect(index)) {
            return null;
        } else {
            return list.get(index);
        }
    }

    @Override
    public T lower(T t) {
        return getElement(lowerIndex(t));
    }

    @Override
    public T floor(T t) {
        return getElement(floorIndex(t));
    }

    @Override
    public T ceiling(T t) {
        return getElement(cellingIndex(t));
    }

    @Override
    public T higher(T t) {
        return getElement(higherIndex(t));
    }

    @Override
    public T pollFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T pollLast() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.unmodifiableList(list).iterator();
    }

    @Override
    public NavigableSet<T> descendingSet() {
        return new ArraySet<>(reverseList, list, Collections.reverseOrder(comparator));
    }

    @Override
    public Iterator<T> descendingIterator() {
        return descendingSet().iterator();
    }

    private NavigableSet<T> subSet(
            T fromElement,
            boolean fromInclusive,
            T toElement,
            boolean toInclusive,
            boolean withIllegalArgumentException
    ) {
        int from = fromInclusive ? cellingIndex(fromElement) : higherIndex(fromElement);
        int to = toInclusive ? higherIndex(toElement) : cellingIndex(toElement);

        if (from >= to && withIllegalArgumentException) {
            throw new IllegalArgumentException("fromElement > toElement");
        }

        return new ArraySet<>(list.subList(from, to), reverseList, comparator);
    }

    @Override
    public NavigableSet<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive) {
        return subSet(fromElement, fromInclusive, toElement, toInclusive, true);
    }

    private NavigableSet<T> getEmptyArraySet() {
        return new ArraySet<>(comparator);
    }

    @Override
    public NavigableSet<T> headSet(T toElement, boolean inclusive) {
        if (isEmpty()) return getEmptyArraySet();
        return subSet(first(), true, toElement, inclusive, false);
    }

    @Override
    public NavigableSet<T> tailSet(T fromElement, boolean inclusive) {
        if (isEmpty()) return getEmptyArraySet();
        return subSet(fromElement, inclusive, last(), true, false);
    }

    @Override
    public Comparator<? super T> comparator() {
        return comparator;
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        return subSet(fromElement, true, toElement, false);
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        return headSet(toElement, false);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        return tailSet(fromElement, true);
    }

    private void requireNonEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public T first() {
        requireNonEmpty();
        return list.get(0);
    }

    @Override
    public T last() {
        requireNonEmpty();
        return list.get(size() - 1);
    }

    @Override
    public int size() {
        return list.size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        return binarySearch((T) o) >= 0;
    }
}
