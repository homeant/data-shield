package com.github.homeant.data.shield.process;

import com.github.homeant.data.shield.util.DESUtils;
import com.github.homeant.data.shield.domain.DataShieldProperties;

public class DESProcess extends AbstractDataProcess {
    public DESProcess(DataShieldProperties properties) {
        super(properties);
    }

    @Override
    public String encrypt(String content) throws Exception {
        return DESUtils.encrypt(content,getKey());
    }

    @Override
    public String decode(String content) throws Exception {
        return DESUtils.decode(content,getKey());
    }
}
