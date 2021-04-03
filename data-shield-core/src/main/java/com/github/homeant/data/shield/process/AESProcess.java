package com.github.homeant.data.shield.process;

import com.github.homeant.data.shield.util.AESUtils;
import com.github.homeant.data.shield.domain.DataShieldProperties;

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
