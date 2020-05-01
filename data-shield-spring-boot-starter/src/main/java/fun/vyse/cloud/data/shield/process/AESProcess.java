package fun.vyse.cloud.data.shield.process;

import fun.vyse.cloud.data.shield.domain.DataShieldProperties;
import fun.vyse.cloud.data.shield.util.AESUtils;

public class AESProcess extends AbstractDataProcess {
    public AESProcess(DataShieldProperties properties) {
        super(properties);
    }

    @Override
    public String encrypt(String content) throws Exception {
        return AESUtils.encrypt(content,getKey());
    }

    @Override
    public String decode(String content) throws Exception {
        return AESUtils.decode(content,getKey());
    }
}
