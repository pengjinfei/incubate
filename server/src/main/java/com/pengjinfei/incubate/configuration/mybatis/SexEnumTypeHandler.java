package com.pengjinfei.incubate.configuration.mybatis;

import com.pengjinfei.incubate.enums.SEX;
import org.apache.ibatis.type.EnumOrdinalTypeHandler;

/**
 * Created on 4/18/18
 *
 * @author jinfei
 */
public class SexEnumTypeHandler extends EnumOrdinalTypeHandler<SEX> {
    public SexEnumTypeHandler() {
        super(SEX.class);
    }
}
