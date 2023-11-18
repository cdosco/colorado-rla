/*
 * Free & Fair Colorado RLA System
 * 
 * @title corla-server
 * 
 * @created Sep 10, 2020
 * 
 * @copyright 2020 Free & Fair
 * 
 * @license GNU General Public License 3.0
 * 
 * @creator name <email>
 * 
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.util;

import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.PersistenceException;

import org.postgresql.util.PSQLException;

/**
 * @description <description>
 * @explanation <explanation>
 * @bon OPTIONAL_BON_TYPENAME
 */
public class DBExceptionUtil {

  private static final int MAX_DEPTH = 10;

  /**
   * 
   * Returns the first PSQL Exception found in the chain. If none found, return
   * null.
   * 
   * @param PersistenceException Persistence Exception Data
   */

  private static PSQLException getFirstPSQLException(PersistenceException pe) {
    SQLException nextException = null;
    Throwable innerException = null;
    if (pe != null) {
      innerException = pe.getCause();
    }
    int i = 0;
    while (innerException != null && i < MAX_DEPTH) {
      if (innerException instanceof PSQLException) {
        return (PSQLException) innerException;
      }
      i++;
      innerException = innerException.getCause();
    }
    return null;
  }

  /**
   * 
   * Gets the reason why set contest names fails. Currently only exception
   * supported is a duplication such as a contest that's already mapped. The
   * error message is PSQLException in the chain.
   * 
   * ERROR: duplicate key value violates unique constraint "XysisConstraintName"
   * Detail: Key (name, county_id, description, votes_allowed)=(NameOfContest,
   * 1, , 1) already exists
   * 
   * @param PersistenceException will be used to display a meaningful error
   *          message to user
   * 
   */
  public static String getConstraintFailureReason(PersistenceException pe) {

    PSQLException firstPSQLException = getFirstPSQLException(pe);
    if (firstPSQLException != null) {

      Pattern pattern = Pattern.compile("Detail: Key .*=([(].*?)[.]");
      Matcher matcher = pattern.matcher(firstPSQLException.getMessage());
      if (matcher.find()) {
        return matcher.group(1) ;
      }
      return pe.toString();
    }
    return pe.toString();
  }

}
