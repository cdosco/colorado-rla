/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 8, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.query;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.Gson;

import org.hibernate.Session;
import org.hibernate.query.Query;


import us.freeandfair.corla.Main;
import us.freeandfair.corla.json.UploadedFileDTO;
import us.freeandfair.corla.model.UploadedFile;
import us.freeandfair.corla.persistence.Persistence;

/**
 * Queries having to do with UploadedFile entities.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public final class UploadedFileQueries {
  /**
   * Private constructor to prevent instantiation.
   */
  private UploadedFileQueries() {
    // do nothing
  }
  
  /**
   * Obtain the UploadedFile object with a specific database ID and county 
   * identifier, if one exists.
   * 
   * @param the_county_id The county identifier.
   * @param the_database_id The database ID.
   */
  // we are checking to see if exactly one result is in a list, and
  // PMD doesn't like it
  @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
  public static UploadedFile matching(final Long the_county_id, 
                                      final Long the_database_id) {
    UploadedFile result = null;
    
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<UploadedFile> cq = cb.createQuery(UploadedFile.class);
      final Root<UploadedFile> root = cq.from(UploadedFile.class);
      final List<Predicate> conjuncts = new ArrayList<Predicate>();
      conjuncts.add(cb.equal(root.get("my_county_id"), the_county_id));
      conjuncts.add(cb.equal(root.get("my_id"), the_database_id));
      cq.select(root).where(cb.and(conjuncts.toArray(new Predicate[conjuncts.size()])));
      final TypedQuery<UploadedFile> query = s.createQuery(cq);
      final List<UploadedFile> query_results = query.getResultList();
      // if there's exactly one result, return that
      if (query_results.size() == 1) {
        result = query_results.get(0);
      } 
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not query database for uploaded file");
    }
    if (result == null) {
      Main.LOGGER.debug("found no uploaded file for county " + the_county_id + 
                        ", id " + the_database_id);
    } else {
      Main.LOGGER.debug("found uploaded file " + result);
    }
    return result;    
  }
  
  /**
   * Obtain the UploadedFile object with a specific county identifier, 
   * timestamp, and status, if one exists.
   *
   * @param the_county_id The county ID.
   * @param the_timestamp The timestamp.
   * @param the_status The status.
   * @return the matched UploadedFile, if one exists, or null otherwise.
   */
  // we are checking to see if exactly one result is in a list, and
  // PMD doesn't like it
  @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
  public static UploadedFile matching(final Long the_county_id,
                                      final Instant the_timestamp,
                                      final UploadedFile.FileStatus the_status) {
    UploadedFile result = null;
    
    try {
      final Session s = Persistence.currentSession();
      final CriteriaBuilder cb = s.getCriteriaBuilder();
      final CriteriaQuery<UploadedFile> cq = cb.createQuery(UploadedFile.class);
      final Root<UploadedFile> root = cq.from(UploadedFile.class);
      final List<Predicate> conjuncts = new ArrayList<Predicate>();
      conjuncts.add(cb.equal(root.get("my_county_id"), the_county_id));
      conjuncts.add(cb.equal(root.get("my_timestamp"), the_timestamp));
      conjuncts.add(cb.equal(root.get("my_status"), the_status));
      cq.select(root).where(cb.and(conjuncts.toArray(new Predicate[conjuncts.size()])));
      final TypedQuery<UploadedFile> query = s.createQuery(cq);
      final List<UploadedFile> query_results = query.getResultList();
      // if there's exactly one result, return that
      if (query_results.size() == 1) {
        result = query_results.get(0);
      } 
    } catch (final PersistenceException e) {
      Main.LOGGER.error("could not query database for uploaded file");
    }
    if (result == null) {
      Main.LOGGER.debug("found no uploaded file for county " + the_county_id + 
                        ", timestamp " + the_timestamp + ", status " + 
                        the_status);
    } else {
      Main.LOGGER.debug("found uploaded file " + result);
    }
    return result;
  }


  public static UploadedFileDTO getAttrs(final UploadedFileDTO upF) {
    final Session s = Persistence.currentSession();
    final Query q =
      s.createNativeQuery("select id, status, county_id "
                          + " from uploaded_file up "
                          + " where up.id = :id ");

    q.setParameter("id", upF.getFileId());
    Object[] row = (Object[])q.getSingleResult();

    if (null == row) {
      return null;
    } else {
      upF.setStatus((String)row[1]);
      upF.setCountyId(((java.math.BigInteger)row[2]).longValue());
      return upF;
    }
  }

  /** having to go around hibernate for cross-thread updates **/
  public static int updateStatus(final UploadedFileDTO upF) {
    final Session s = Persistence.currentSession();
    final Query q =
      s.createNativeQuery("update uploaded_file up "
                    + " set status = :status"
                    + " where up.id = :id");

    q.setParameter("id", upF.getFileId());
    q.setParameter("status", upF.getStatus());

    return q.executeUpdate();
  }

  /** having to go around hibernate for cross-thread updates **/
  public static int updateStatusAndResult(final UploadedFileDTO upF) {
    final Session s = Persistence.currentSession();
    final Query q =
      s.createNativeQuery("update uploaded_file up "
                          + " set status = :status,"
                          + " result = :result, "
                          + " version = version + 1 "
                          + " where up.id = :id");

    q.setParameter("id", upF.getFileId());
    q.setParameter("status", upF.getStatus());
    q.setParameter("result", (new Gson()).toJson(upF.getResult()));

    return q.executeUpdate();
  }

  /** having to go around hibernate for cross-thread updates **/
  public static int setCVRFileOnCounty(final UploadedFileDTO upF) {
    final Session s = Persistence.currentSession();
    final Query q =
      s.createNativeQuery("update county_dashboard cdb "
                          + " set cvr_file_id = :id "
                          + " where cdb.id = :countyId");

    q.setParameter("id", upF.getFileId());
    q.setParameter("countyId", upF.getCountyId());

    return q.executeUpdate();
  }




}
