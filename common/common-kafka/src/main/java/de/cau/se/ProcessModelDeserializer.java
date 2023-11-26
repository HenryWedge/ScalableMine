package de.cau.se;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.cau.se.model.MinedProcessModel;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ProcessModelDeserializer implements Deserializer<MinedProcessModel> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public MinedProcessModel deserialize(String topic, byte[] data) {
        try {
            if (data == null){
                System.out.println("Null received at deserializing");
                return null;
            }
            return objectMapper.readValue(new String(data, StandardCharsets.UTF_8), MinedProcessModel.class);
        } catch (Exception e) {
            e.printStackTrace();
            //throw new RuntimeException("Error when deserializing byte[] to MessageDto");
        }
        return null;
    }

    @Override
    public void close() {
    }
}
