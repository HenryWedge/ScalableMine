package de.cau.se;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.cau.se.datastructure.Result;
import de.cau.se.datastructure.TaggedRelation;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TaggedRelationDeserializer implements Deserializer<TaggedRelation> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public TaggedRelation deserialize(String topic, byte[] data) {
        try {
            if (data == null){
                System.out.println("Null received at deserializing");
                return null;
            }
            return objectMapper.readValue(new String(data, StandardCharsets.UTF_8), TaggedRelation.class);
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
