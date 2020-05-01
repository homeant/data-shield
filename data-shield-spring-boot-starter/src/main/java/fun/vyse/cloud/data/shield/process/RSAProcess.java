package fun.vyse.cloud.data.shield.process;

import fun.vyse.cloud.data.shield.domain.DataShieldProperties;
import fun.vyse.cloud.data.shield.util.RSAUtils;

public class RSAProcess extends AbstractDataProcess {

    public RSAProcess(DataShieldProperties properties) {
        super(properties);
    }

    @Override
    public String encrypt(String content) throws Exception {
        return RSAUtils.encryptByPublicKey(content,getPublicKey());
    }

    @Override
    public String decode(String content) throws Exception {
        return RSAUtils.decryptByPrivateKey(content,getPrivateKey());
    }
}
