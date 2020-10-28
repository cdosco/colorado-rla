/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 12, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Joseph R. Kiniry <kiniry@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;

import com.google.gson.JsonParseException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.model.AuditType;
import us.freeandfair.corla.model.AuditStatus;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.ContestToAudit;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.ComparisonAuditQueries;

/**
 * The endpoint for indicating that a contest must be hand-counted.
 * 
 * @author Joseph R. Kiniry <kiniry@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public class IndicateHandCount extends AbstractDoSDashboardEndpoint {
  /**
   * Class-wide logger
   */
  public static final Logger LOGGER =
    LogManager.getLogger(IndicateHandCount.class);


  /**
   * The event to return for this endpoint.
   */
  private final ThreadLocal<ASMEvent> my_event = new ThreadLocal<ASMEvent>();
  
  /**
   * {@inheritDoc}
   */
  @Override
  public EndpointType endpointType() {
    return EndpointType.POST;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String endpointName() {
    return "/hand-count";
  }

  /**
   * @return STATE authorization is necessary for this endpoint.
   */
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.STATE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ASMEvent endpointEvent() {
    return my_event.get();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void reset() {
    my_event.set(null);
  }

  /**
   * Indicate that a contest must be hand-counted.
   * 
   * @param the_request The request.
   * @param the_response The response.
   */
  @Override
  public synchronized String endpointBody(final Request the_request, 
                                      final Response the_response) {
    try {
      final ContestToAudit[] supplied_ctas = 
          Main.GSON.fromJson(the_request.body(), ContestToAudit[].class);
      final DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
      if (dosdb == null) {
        serverError(the_response, "Could not select contests");
      } else {
        boolean hand_count = false;
        final Set<String> hand_count_contests = new HashSet<>();
        for (final ContestToAudit c : fixReasons(dosdb, supplied_ctas)) {
          if (c.audit() == AuditType.HAND_COUNT &&
              dosdb.updateContestToAudit(c)) {
            hand_count = true;
            hand_count_contests.add(c.contest().name());
          }
        }
        if (hand_count) {
          unTargetContests(dosdb, hand_count_contests);
          LOGGER.info("HAND_COUNT set for: " + String.join(",", hand_count_contests));
        } else {
          // bad data was submitted for hand count selection
          badDataContents(the_response, "Invalid contest selection data");
        }
      }
      Persistence.saveOrUpdate(dosdb);
      ok(the_response, "Contest selected for hand count");
    } catch (final JsonParseException e) {
      badDataContents(the_response, "Invalid contest selection data");
    } catch (final PersistenceException e) {
      serverError(the_response, "Unable to save contest selection");
    }
    return my_endpoint_result.get();
  }
  
  /**
   * Updates the supplied CTAs with the reasons that were originally specified 
   * on the DoS dashboard.
   * 
   * @param the_dosdb The DoS dashboard.
   * @param the_supplied_ctas The supplied CTAs. 
  */
  @SuppressWarnings("PMD.UseVarargs")
  private Set<ContestToAudit> fixReasons(final DoSDashboard the_dosdb,
                                         final ContestToAudit[] the_supplied_ctas) {
    final Set<ContestToAudit> result = new HashSet<>();
    final Set<ContestToAudit> existing_ctas = the_dosdb.contestsToAudit();
    final Map<Contest, ContestToAudit> contest_cta = new HashMap<>();
    
    // let's iterate over these only once, instead of once for each array element in 
    // the_supplied_ctas
    for (final ContestToAudit c : existing_ctas) {
      contest_cta.put(c.contest(), c);
    }
    
    // update the supplied CTAs with the dashboard reasons
    for (final ContestToAudit c : the_supplied_ctas) {
      ContestToAudit real_cta = c;
      if (contest_cta.containsKey(c.contest())) {
        real_cta = new ContestToAudit(c.contest(), 
                                      contest_cta.get(c.contest()).reason(), 
                                      c.audit());
      }
      result.add(real_cta);
    }
    
    return result;
  }

  private void unTargetContests(final DoSDashboard dosdb,
                                final Set<String> hand_count_contests) {
    for(final String contestName: hand_count_contests) {
      dosdb.removeContestToAuditByName(contestName);
      ComparisonAuditQueries.updateStatus(contestName, AuditStatus.HAND_COUNT);
    }
  }

}
