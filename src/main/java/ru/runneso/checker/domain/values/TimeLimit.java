package ru.runneso.checker.domain.values;

import ru.runneso.checker.domain.exceptions.ValidationException;

public class TimeLimit extends BaseValueObject<Integer>{
    private final static Integer MIN_MS = 500;
    private final static Integer MAX_MS = 60 * 1000;

    public TimeLimit(Integer value) {
        super(value);
    }

    @Override
    protected void validate() {
        Integer value = getAsGenericType();

        if (value == null) {
            return;
        }

        if (value < MIN_MS){
            throw new ValidationException("Time limit is less than " + MIN_MS + " ms");
        }

        if (value > MAX_MS){
            throw new ValidationException("Time limit is greater than " + MAX_MS + " ms");
        }
    }


}