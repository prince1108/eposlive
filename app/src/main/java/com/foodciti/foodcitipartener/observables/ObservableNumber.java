package com.foodciti.foodcitipartener.observables;

import java.util.Observable;

public class ObservableNumber<E extends Number> extends Observable {
    private E value;

    public ObservableNumber() {
    }

    public ObservableNumber(E value) {
        this.value = value;
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        if (!value.equals(this.value)) {
            this.value = value;
            setChanged();
            notifyObservers(value);
        }
    }

}
