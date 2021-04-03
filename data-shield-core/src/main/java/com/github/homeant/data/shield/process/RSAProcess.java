package com.github.homeant.data.shield.process;


import com.github.homeant.data.shield.util.RSAUtils;
import com.github.homeant.data.shield.domain.DataShieldProperties;

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
