package ru.runneso.checker.domain.values;

public abstract class BaseValueObject<T> {
    private final T value;

    public BaseValueObject(T value) {
        this.value = value;
        validate();
    }

    protected abstract void validate();

    public T getAsGenericType(){
        return value;
    }
}