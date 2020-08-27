package common.validation;

interface IValidator
{
    String question();
    boolean validate(String input);
}
