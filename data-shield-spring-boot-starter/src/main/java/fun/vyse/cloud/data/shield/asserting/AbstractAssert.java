package fun.vyse.cloud.data.shield.asserting;

import java.util.Objects;

public abstract class AbstractAssert implements IAssert {
    protected Boolean nonEmpty(String value){
        return Objects.nonNull(value) && !Objects.equals(value,"");
    }
}
