package us.freeandfair.corla.persistence;

import java.lang.reflect.Type;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import us.freeandfair.corla.csv.Result;

/**
 * A converter for the Result class to json.
 *
 */
@Converter
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class ResultConverter
  implements AttributeConverter<Result, String> {
  /**
   * The type information for a map from AuditReason to Boolean.
   */
  private static final Type RESULT_TYPE = new TypeToken<Result>() { }.getType();

  /**
   * Our Gson instance, which does not do pretty-printing (unlike the global
   * one defined in Main).
   */
  private static final Gson GSON =
      new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

  /**
   * Converts the specified list of Strings to a database column entry.
   *
   * @param the_set The list of Strings.
   */
  @Override
  public String convertToDatabaseColumn(final Result result) {
    return GSON.toJson(result);
  }

  /**
   * Converts the specified database column entry to a list of strings.
   *
   * @param the_column The column entry.
   */
  @Override
  public Result convertToEntityAttribute(final String column) {
    return GSON.fromJson(column, RESULT_TYPE);
  }
}
