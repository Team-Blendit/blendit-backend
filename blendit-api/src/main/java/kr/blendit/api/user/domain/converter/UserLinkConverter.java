package kr.blendit.api.user.domain.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.ArrayList;
import java.util.List;
import kr.blendit.api.user.domain.UserLink;

@Converter
public class UserLinkConverter implements AttributeConverter<List<UserLink>, String> {

  private static final ObjectMapper mapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(List<UserLink> list) {
    try {
      return list == null ? "[]" : mapper.writeValueAsString(list);
    } catch (JsonProcessingException e) {
      return "[]";
    }
  }

  @Override
  public List<UserLink> convertToEntityAttribute(String json) {
    try {
      return json == null ? new ArrayList<>() : mapper.readValue(json, new TypeReference<>() {
      });
    } catch (JsonProcessingException e) {
      return new ArrayList<>();
    }
  }
}
