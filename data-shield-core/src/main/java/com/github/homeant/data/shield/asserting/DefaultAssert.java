package com.github.homeant.data.shield.asserting;

public class DefaultAssert implements IAssert {
    @Override
    public boolean encrypt(String value, Object object){return true;}

    @Override
    public boolean decode(String value, Object object){return true;}


}
