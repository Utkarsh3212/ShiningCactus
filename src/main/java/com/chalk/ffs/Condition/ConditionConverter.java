package com.chalk.ffs.Condition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ConditionConverter implements AttributeConverter<ConditionNode,String> {
    private static final ObjectMapper mapper =new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ConditionNode attribute){
        try {
            return attribute == null ? null : mapper.writeValueAsString(attribute);
        } catch(JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting ConditionNode to JSON", e);
        }
    }

    @Override
    public ConditionNode convertToEntityAttribute(String dbData){
        try {
            return dbData==null?null:mapper.readValue(dbData,ConditionNode.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error reading ConditionNode from JSON",e);
        }
    }
}
