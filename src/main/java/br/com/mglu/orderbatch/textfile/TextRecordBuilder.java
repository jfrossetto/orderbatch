package br.com.mglu.orderbatch.textfile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Component
@Slf4j
public class TextRecordBuilder {

  public <T extends Record> T buildRecord(String record, Class<T> type) {
    try {
      List<String> fieldValues = new ArrayList<>();
      for(Field field : type.getDeclaredFields()) {
        if(Objects.isNull(field.getDeclaredAnnotation(TextField.class))) {
          continue;
        }
        TextField textField = field.getDeclaredAnnotation(TextField.class);
        int start = textField.startAt();
        int end = textField.endAt()+1 < record.length() ? textField.endAt()+1 : record.length();
        String fieldValue = record.substring(start, end);
        fieldValues.add(fieldValue);
      }
      Constructor<T> constructor = getCanonicalConstructor(type);
      return constructor.newInstance(fieldValues.toArray());
    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }

  }

  private <T extends Record> Constructor<T> getCanonicalConstructor(Class<T> type) throws NoSuchMethodException {
    Class<?>[] componentTypes = Arrays.stream(type.getRecordComponents())
            .map(rc -> rc.getType())
            .toArray(Class<?>[]::new);
    return type.getDeclaredConstructor(componentTypes);
  }

}
