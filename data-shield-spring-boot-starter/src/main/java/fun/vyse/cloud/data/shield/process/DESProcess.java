package fun.vyse.cloud.data.shield.process;

import fun.vyse.cloud.data.shield.domain.DataShieldProperties;
import fun.vyse.cloud.data.shield.util.DESUtils;

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
