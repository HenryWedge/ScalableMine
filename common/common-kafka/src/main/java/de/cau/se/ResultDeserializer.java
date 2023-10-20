package de.cau.se;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.cau.se.datastructure.Result;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class ResultDeserializer implements Deserializer<Result> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public Result deserialize(String topic, byte[] data) {
        try {
            if (data == null){
                System.out.println("Null received at deserializing");
                return null;
            }
            return objectMapper.readValue(new String(data, "UTF-8"), Result.class);
        } catch (Exception e) {
            throw new RuntimeException("Error when deserializing byte[] to MessageDto");
        }
    }

    @Override
    public void close() {
    }
}
