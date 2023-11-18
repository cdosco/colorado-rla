package us.freeandfair.corla.csv;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.nullableEquals;

import java.util.Objects;


/** The result of parsing/importing a csv file **/
public class Result {
  public boolean success;
  public Integer importedCount;
  public String errorMessage;
  public Integer errorRowNum;
  public String errorRowContent;

  /**
   * The attributes determine whether a Hibernate update happens, not the
   * table-object (which has been the prevailing assumption in this project).
   * Since this class can be used as a Hibernate attribute or field, it is
   * important for it to be able to be checked for equality by
   * org.hibernate.internal.util.compare.EqualsHelper#equals. Otherwise
   * Hibernate will always think that an update has happened and keep
   * incrementing the version which can cause errors such as:
   *     "ERROR: could not serialize access due to concurrent update".
   **/
  public boolean equals(final Object other) {
    Result a = this;
    boolean result = true;
    if (other instanceof Result) {
      final Result b = (Result) other;
      result &= nullableEquals(a.success, b.success);
      result &= nullableEquals(a.importedCount, b.importedCount);
      result &= nullableEquals(a.errorMessage, b.errorMessage);
      result &= nullableEquals(a.errorRowNum, b.errorRowNum);
      result &= nullableEquals(a.errorRowContent, b.errorRowContent);
    } else {
      result = false;
    }
    return result;
  }

  public int hashCode() {
    return Objects.hash(success,
                        importedCount,
                        errorMessage,
                        errorRowNum,
                        errorRowContent);
  }
}
