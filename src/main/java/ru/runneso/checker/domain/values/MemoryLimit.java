package ru.runneso.checker.domain.values;

import ru.runneso.checker.domain.exceptions.ValidationException;

public class MemoryLimit extends BaseValueObject<Integer>{
    private final static Integer MIN_KB = 65536;
    private final static Integer MAX_KB = 1024 * 1024;

    public MemoryLimit(Integer value) {
        super(value);
    }

    @Override
    protected void validate() {
        Integer value = getAsGenericType();

        if (value == null) {
            return;
        }

        if (value < MIN_KB){
            throw new ValidationException("Memory limit is less than " + MIN_KB);
        }

        if (value > MAX_KB){
            throw new ValidationException("Memory limit is greater than " + MAX_KB);
        }
    }


}