package com.github.homeant.data.shield.dataMasking;

import com.github.homeant.data.shield.util.DataMaskingUtils;
import org.springframework.stereotype.Component;

@Component
public class DataMaskingImpl implements DataMasking {
    @Override
    public String apply(String value) {
        return DataMaskingUtils.around(value,2,2);
    }
}
