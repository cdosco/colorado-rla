package us.freeandfair.corla.util;

import static org.testng.Assert.assertEquals;

import java.sql.BatchUpdateException;
import java.sql.SQLException;
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;
import org.testng.annotations.Test;

public class DBExceptionUtilTest {
  
  private PersistenceException getPersistenceExceptionWithPSQLForContest() {
    String reason = "Batch entry 0 update contest set county_id=1, name='Regent of the University of Colorado - At Large', version=1 where id=167 and version=0 was aborted: ERROR: duplicate key value violates unique constraint \"ukdv45ptogm326acwp45hm46uaf\"\n" + 
        "  Detail: Key (name, county_id, description, votes_allowed)=(Regent of the University of Colorado - At Large, 1, , 1) already exists.  Call getNextException to see";
    int vendorCode = 0;
    int[] updateCounts = {-3};
    Throwable cause = new PSQLException("ERROR: duplicate key value violates unique constraint \"ukdv45ptogm326acwp45hm46uaf\"\n" + 
        "  Detail: Key (name, county_id, description, votes_allowed)=(Regent of the University of Colorado - At Large, 1, , 1) already exists.",     PSQLState.DATA_ERROR);
    SQLException sqlexception = new BatchUpdateException(reason, "23505", vendorCode, updateCounts, cause);
    SQLException nextException = sqlexception.getNextException();
    ConstraintViolationException cve = new ConstraintViolationException("constraint violation", sqlexception, "co1234");
    return new PersistenceException(cve); 
  }
  
  
  private PersistenceException getPersistenceExceptionWithPSQLForChoice() {
    String reason = "Batch entry 0 update contest_choice set description='REP', fictitious='FALSE', name='Janet Lee Cook', qualified_write_in='FALSE' where contest_id=167 and index=0 was aborted: ERROR: duplicate key value violates unique constraint \"x_contest_choice_name\"\n" + 
        "  Detail: Key (contest_id, name)=(167, Janet Lee Cook) already exists.  Call getNextException to see other errors in the batch.";
    int vendorCode = 0;
    int[] updateCounts = {-3};
    Throwable cause = new PSQLException("ERROR: duplicate key value violates unique constraint \"x_contest_choice_name\"\n" + 
        "  Detail: Key (contest_id, name)=(167, Janet Lee Cook) already exists.",     PSQLState.DATA_ERROR);
    SQLException sqlexception = new BatchUpdateException(reason, "23505", vendorCode, updateCounts, cause);
    ConstraintViolationException cve = new ConstraintViolationException("constraint violation", sqlexception, "co1234");
    return new PersistenceException(cve); 
  }
  
  
  @Test
  public void verifyReason() {
    String reason = DBExceptionUtil.getConstraintFailureReason(getPersistenceExceptionWithPSQLForContest());
    assertEquals(reason, "(Regent of the University of Colorado - At Large, 1, , 1) already exists");
    reason = DBExceptionUtil.getConstraintFailureReason(getPersistenceExceptionWithPSQLForChoice());
    assertEquals(reason, "(167, Janet Lee Cook) already exists");
  }
  

}
