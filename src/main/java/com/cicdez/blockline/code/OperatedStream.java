package com.cicdez.blockline.code;

import java.util.ArrayList;
import java.util.List;

public class OperatedStream<T> {
    private final List<T> list;
    private final List<Operator<T>> operators;
    
    public OperatedStream() {
        list = new ArrayList<>();
        operators = new ArrayList<>();
    }
    
    public void put(T t) {
        list.add(t);
    }
    public void putOperator(Operator<T> operator) {
        operators.add(operator);
    }
    
    
    @FunctionalInterface
    public interface IOperator<K, M> {
        M accept(K previous, K next);
    }
    
    @FunctionalInterface
    public interface Operator<K> extends IOperator<K, K> {
    }
    
    public boolean isAccepted() {
        return list.size() == operators.size() + 1;
    }
    
    public T solve() throws BLException {
        if (!isAccepted()) throw new BLException("List.Size != Operators.Size + 1", null);
        T result = list.get(0);
        for (int index = 0; index < operators.size(); index++) {
            result = operators.get(index).accept(result, list.get(index + 1));
        }
        return result;
    }
}
