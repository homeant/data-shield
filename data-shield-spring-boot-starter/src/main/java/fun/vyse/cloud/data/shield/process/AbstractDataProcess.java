package fun.vyse.cloud.data.shield.process;

import fun.vyse.cloud.data.shield.domain.DataShieldProperties;
import lombok.Data;

@Data
public abstract class AbstractDataProcess implements IDataProcess {
    private final DataShieldProperties properties;

    @Override
    public String encrypt(String content) throws Exception {
        return content;
    }

    @Override
    public String decode(String content) throws Exception {
        return content;
    }

    public String getKey(){
        return properties.getKey();
    }

    public String getPublicKey(){
        return properties.getPublicKey();
    }

    public String getPrivateKey(){
        return properties.getPrivateKey();
    }
}
