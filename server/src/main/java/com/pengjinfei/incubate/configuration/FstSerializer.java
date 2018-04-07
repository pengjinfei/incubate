package com.pengjinfei.incubate.configuration;

import org.apache.commons.lang3.ArrayUtils;
import org.nustaq.serialization.FSTConfiguration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Component;

/**
 * Created on 4/5/18
 *
 * @author Pengjinfei
 */
@Component
public class FstSerializer implements RedisSerializer<Object> {

    private FSTConfiguration fstConfiguration = FSTConfiguration.createDefaultConfiguration();
    @Override
    public byte[] serialize(Object o) throws SerializationException {
        if (o == null) {
            return null;
        }
        return fstConfiguration.asByteArray(o);
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (ArrayUtils.isEmpty(bytes)) {
            return null;
        }
        return fstConfiguration.asObject(bytes);
    }
}
