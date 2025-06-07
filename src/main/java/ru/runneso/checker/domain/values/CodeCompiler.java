package ru.runneso.checker.domain.values;

import ru.runneso.checker.domain.exceptions.ValidationException;

import java.util.List;

public class CodeCompiler extends BaseValueObject<String>{
    private static final List<String> ALLOWED_COMPILERS = List.of("g++-11", "python3.10", "javac-17");

    public CodeCompiler(String value){
        super(value);
    }

    @Override
    protected void validate() {
        String value = getAsGenericType();


        if (value == null){
            throw new ValidationException("Compiler is null");
        }

        if(!ALLOWED_COMPILERS.contains(value)){
            throw new ValidationException("Compiler is not valid");
        }
    }

    public String getExtension(){
        return switch (getAsGenericType()) {
            case "javac-17" -> ".java";
            case "python3.10" -> ".py";
            case "g++-11" -> ".cpp";
            default -> null;
        };
    }


}