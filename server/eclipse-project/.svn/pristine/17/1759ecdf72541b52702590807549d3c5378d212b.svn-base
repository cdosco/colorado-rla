/*
 * Free & Fair Colorado RLA System
 *
 * @title ColoradoRLA
 * @copyright 2018 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Democracy Works, Inc <dev@democracy.works>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.query;

import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import org.hibernate.query.Query;
import org.hibernate.Session;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import us.freeandfair.corla.model.AuditStatus;
import us.freeandfair.corla.model.ComparisonAudit;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.util.SuppressFBWarnings;

/**
 * Queries having to do with ComparisonAudit entities.
 */
public final class ComparisonAuditQueries {
  /**
   * Class-wide logger
   */
  public static final Logger LOGGER =
      LogManager.getLogger(ComparisonAuditQueries.class);

  /**
   * Private constructor to prevent instantiation.
   */
  private ComparisonAuditQueries() {
    // do nothing
  }

  /** sort by targeted, then contest name **/
  // I disagree, FB
  @SuppressFBWarnings({"RV_NEGATING_RESULT_OF_COMPARETO", "SE_COMPARATOR_SHOULD_BE_SERIALIZABLE"})
  private static class TargetedSort implements Comparator<ComparisonAudit> {

    /** sort by targeted, then contest name **/
    @Override
    public int compare(final ComparisonAudit a, final ComparisonAudit b) {
      // negative to put true first
      final int t = -Boolean.compare(a.isTargeted(), b.isTargeted());
      if (0 == t) {
        return a.contestResult().getContestName().compareTo(b.contestResult().getContestName());
      } else {
        return t;
      }
    }
  }

  /** All the comparison audits for all the contests, sorted by targeted, then
   * alphabetical **/
  public static List<ComparisonAudit> sortedList() {
    // sorting by db doesn't stick for some reason
    final List<ComparisonAudit> results = Persistence.getAll(ComparisonAudit.class);
    Collections.sort(results, new TargetedSort());
    return results;
  }

  /**
   * Obtain the ComparisonAudit object for the specified contest name.
   *
   * @param contestName The contest name
   * @return the matched object
   */
  public static ComparisonAudit matching(final String contestName) {
    final Session s = Persistence.currentSession();
    final Query q =
      s.createQuery("select ca from ComparisonAudit ca "
                    + " join ContestResult cr "
                    + "   on ca.my_contest_result = cr "
                    + " where cr.contestName = :contestName");

    q.setParameter("contestName", contestName);

    try {
      return (ComparisonAudit) q.getSingleResult();
    } catch (javax.persistence.NoResultException e ) {
      return null;
    }
  }


  /**
   * Return the ContestResult with the contestName given or create a new
   * ContestResult with the contestName.
   **/
  public static Integer count() {
    final Session s = Persistence.currentSession();
    final Query q = s.createQuery("select count(ca) from ComparisonAudit ca");
    return ((Long)q.uniqueResult()).intValue();
  }

  /** setAuditStatus on matching contestName **/
  public static void updateStatus(final String contestName, final AuditStatus auditStatus) {
    final ComparisonAudit ca = matching(contestName);
    if (null != ca) {
      ca.setAuditStatus(auditStatus);
    }
  }
}
