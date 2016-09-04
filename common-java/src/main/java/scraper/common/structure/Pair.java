package scraper.common.structure;

import scraper.common.Utils;

/**
 * Class representing pair of two objects.
 *
 * @param <T> first object type
 * @param <K> second object type
 */
public class Pair<T, K> {

    private T first;

    private K second;

    public Pair() {
    }

    public Pair(T first, K second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public K getSecond() {
        return second;
    }

    public void setSecond(K second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Pair<?, ?> other = (Pair<?, ?>) obj;

        return Utils.computeEq(this.first, other.first, this.second, other.second);
    }

    @Override
    public int hashCode() {
        return Utils.computeHash(first, second);
    }

    @Override
    public String toString() {
        return String.format("Pair[first=%s, second=%s]", first, second);
    }
}
