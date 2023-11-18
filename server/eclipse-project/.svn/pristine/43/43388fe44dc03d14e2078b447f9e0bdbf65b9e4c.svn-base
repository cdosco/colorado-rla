package us.freeandfair.corla.query;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.model.Tribute;


/** find the tributes! **/
public final class TributeQueries {

  /**
   * Private constructor to prevent instantiation.
   */
  private TributeQueries() {
    // do nothing
  }

  /** select every acvr which has been submitted for the the given cvr ids,
   * excluding revisions(reaudits) **/
  public static List<Tribute> forContest(final String contestName) {
    final Session s = Persistence.currentSession();
    final Query q =
      s.createQuery("select t from Tribute t "
                    + " where t.contestName =:contestName");

    q.setParameter("contestName", contestName);

    return q.getResultList();
  }

}
