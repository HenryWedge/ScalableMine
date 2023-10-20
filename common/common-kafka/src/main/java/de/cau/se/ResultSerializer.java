package de.cau.se;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.cau.se.datastructure.Result;
import org.apache.commons.lang3.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;
public class ResultSerializer implements Serializer<Result> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, Result data) {
        try {
            if (data == null) {
                System.out.println("Null received at serializing");
                return null;
            }
            return objectMapper.writeValueAsBytes(data);
        } catch ( Exception e ) {
            throw new SerializationException("Error when serializing MessageDto to byte[]", e);
        }
    }

    @Override
    public void close() {
    }
}
