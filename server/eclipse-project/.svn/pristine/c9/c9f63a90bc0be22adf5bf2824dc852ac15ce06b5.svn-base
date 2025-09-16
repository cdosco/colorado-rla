/*
 * Free & Fair Colorado RLA System
 *
 * @title ColoradoRLA
 * 
 * @created Aug 12, 2017
 * 
 * @copyright 2017 Colorado Department of State
 * 
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * 
 * @creator Joseph R. Kiniry <kiniry@freeandfair.us>
 * 
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import static us.freeandfair.corla.asm.ASMEvent.AuditBoardDashboardEvent.*;
import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.*;
import static us.freeandfair.corla.asm.ASMState.AuditBoardDashboardState.*;

import static us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent.COUNTY_AUDIT_COMPLETE_EVENT;
import static us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent.COUNTY_START_AUDIT_EVENT;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;

import javax.persistence.PersistenceException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import com.google.gson.reflect.TypeToken;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.Main;

import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.asm.ASMState;
import us.freeandfair.corla.asm.ASMUtilities;
import us.freeandfair.corla.asm.AuditBoardDashboardASM;
import us.freeandfair.corla.asm.CountyDashboardASM;
import us.freeandfair.corla.asm.DoSDashboardASM;

import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.model.AuditReason;
import us.freeandfair.corla.model.AuditStatus;
import us.freeandfair.corla.model.ComparisonAudit;
import us.freeandfair.corla.model.Elector;
import us.freeandfair.corla.model.Round;

import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.report.ReportRows;

/**
 * Signs off on the current audit round for a county.
 *
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.CyclomaticComplexity",
    "PMD.ModifiedCyclomaticComplexity", "PMD.NPathComplexity", "PMD.StdCyclomaticComplexity"})
public class SignOffAuditRound extends AbstractAuditBoardDashboardEndpoint {

  boolean updateAll = true;
  /**
   * The type of the JSON request.
   */
  private static final Type AUDIT_BOARD = new TypeToken<List<Elector>>() {
  }.getType();

  /**
   * Class-wide logger
   */
  public static final Logger LOGGER = LogManager.getLogger(SignOffAuditRound.class);

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
    return "/sign-off-audit-round";
  }

  /**
   * @return COUNTY authorization is required for this endpoint.
   */
  public AuthorizationType requiredAuthorization() {
    return AuthorizationType.COUNTY;
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
   * Signs off on the current audit round, regardless of its state of
   * completion.
   *
   * @param request The request.
   * @param response The response.
   */
  @Override
  @SuppressWarnings({"PMD.ExcessiveMethodLength"})
  public String endpointBody(final Request request, final Response response) {
    final County county = Main.authentication().authenticatedCounty(request);

    if (county == null) {
      LOGGER.error("could not get authenticated county");
      unauthorized(response, "not authorized to sign off on the round");
    }

    final JsonParser parser = new JsonParser();
    final JsonObject o;

    try {
      o = parser.parse(request.body()).getAsJsonObject();
      final int auditBoardIndex = o.get("index").getAsInt();
      final List<Elector> signatories = Main.GSON.fromJson(o.get("audit_board"), AUDIT_BOARD);

      if (signatories.size() < CountyDashboard.MIN_ROUND_SIGN_OFF_MEMBERS) {
        LOGGER.error("[signoff: too few signatories for round sign-off]");
        invariantViolation(response, "too few signatories for round sign-off sent");
      }

      final CountyDashboard cdb = Persistence.getByID(county.id(), CountyDashboard.class);

      if (cdb == null) {
        LOGGER.error(String
            .format("[signoff: Could not get county dashboard for %s County id=%d]",
                    county.name(), county.id()));
        serverError(response, "could not get county dashboard");
      }

      if (cdb.currentRound() == null) {
        LOGGER.error(String.format("[signoff: No current round for %s County]",
                                   cdb.county().name()));
        invariantViolation(response, "no current round on which to sign off");
      }

      final Round currentRound = cdb.currentRound();

      currentRound.setSignatories(auditBoardIndex, signatories);

      if (cdb.auditBoardCount() == null) {
        LOGGER.error(String.format("[signoff: Audit board count unset for %s County]",
                                   cdb.county().name()));
        invariantViolation(response, "audit board count unset");
      }

      // If we have not seen all the boards sign off yet, we do not want to end
      // the round.
      if (currentRound.signatories().size() < cdb.auditBoardCount()) {
        LOGGER.info(String.format("%d of %d audit boards have signed off for county %d",
                                  currentRound.signatories().size(), cdb.auditBoardCount(),
                                  cdb.id()));
      } else {
        // We're done!
        cdb.endRound();

        final AuditBoardDashboardASM asm =
            ASMUtilities.asmFor(AuditBoardDashboardASM.class, String.valueOf(cdb.id()));

        if (null != asm && asm.currentState() == ROUND_IN_PROGRESS) {
          ASMUtilities.step(ROUND_COMPLETE_EVENT, AuditBoardDashboardASM.class,
                            String.valueOf(cdb.id()));
        }

        logAuditsForCountyDashboard(cdb);

        // update the ASM state for the county and maybe DoS
        if (!DISABLE_ASM) {
          final boolean auditComplete;
          LOGGER.info(String
              .format("[signoff for %s County: cdb.estimatedSamplesToAudit()=%d," +
                      " cdb.auditedSampleCount()=%d," + " cdb.ballotsAudited()=%d]",
                      cdb.county().name(), cdb.estimatedSamplesToAudit(),
                      cdb.auditedSampleCount(), cdb.ballotsAudited()));

          if (cdb.allAuditsComplete()) {
            my_event.set(RISK_LIMIT_ACHIEVED_EVENT);
            // In this case, we'd be terminating single county audits
            // for opportunistic benefits only.
            final List<ComparisonAudit> terminated = cdb.endSingleCountyAudits();
            LOGGER.debug(String.format("[signoff: all targeted audits finished in %s County." +
                                       " Terminated these audits: %s]", cdb.county().name(),
                                       terminated));
            my_event.set(ROUND_SIGN_OFF_EVENT);
            if (participatesInStateAudit(cdb)) {
              auditComplete = allCountyAuditBoardsSignedOff();
            } else {
              auditComplete = true;
            }
          } else if (cdb.cvrsImported() <= cdb.ballotsAudited()) {
            // In this case, we'd be terminating targeted and
            // opportunistic single county audits.
            final List<ComparisonAudit> terminated = cdb.endSingleCountyAudits();
            auditComplete = cdb.allAuditsComplete();
            LOGGER.debug(String
                .format("[signoff: no more ballots; terminated single-county audits" +
                        " %s in %s County. All complete? (%b)]", terminated,
                        cdb.county().name(), auditComplete));
            my_event.set(ROUND_SIGN_OFF_EVENT);
          } else {
            LOGGER.debug("[signoff: the round ended normally]");
            auditComplete = false;
            my_event.set(ROUND_SIGN_OFF_EVENT);
          }

          if (auditComplete) {
            LOGGER.info(String.format("[signoff: round complete in %s County]",
                                      cdb.county().name()));

            LOGGER.info(String.format("[signoff: audit complete in %s County]",
                                      cdb.county().name()));
            notifyAuditCompleteForDoS();
            notifyRoundCompleteForDoS(cdb.id());
          } 
        }
      }
    } catch (final PersistenceException e) {
      LOGGER.error("[signoff: unable to sign off round.]");
      serverError(response, "unable to sign off round: " + e);
    } catch (final JsonParseException e) {
      LOGGER.error("[signoff: bad data sent in an attempt to sign off on round]", e);
      badDataContents(response, "invalid request body attempting to sign off on round");
    }
    LOGGER.debug("[signoff: a-ok]");
    ok(response, "audit board signed off");

    return my_endpoint_result.get();
  }

  /**
   * Notifies the DoS dashboard that the round is over if all the counties
   * _except_ for the one identified in the parameter have completed their audit
   * round, or are not auditing (the excluded county is not counted because its
   * transition will not happen until this endpoint returns).
   *
   * @param the_id The ID of the county to exclude.
   */
  private void notifyRoundCompleteForDoS(final Long the_id) {
    boolean finished = true;
    for (final CountyDashboard cdb : Persistence.getAll(CountyDashboard.class)) {
      if (cdb.id().equals(the_id)) {
        continue; // <- sneaky filter for all but this county
        // ROUND_COMPLETE_EVENT has already happened for this county above, and
        // the notifyAuditComplete will handle COUNTY_AUDIT_COMPLETE_EVENT for
        // this county
      }

      if (!cdb.id().equals(the_id)) {
        finished &= cdb.currentRound() == null;
      }
    }

    if (finished) {
      for (final CountyDashboard cdb : Persistence.getAll(CountyDashboard.class)) {
        if (cdb.id().equals(the_id)) {
          continue;
        }
        markCountyAsDone(cdb);
      }

      DoSDashboardASM dashboardASM = ASMUtilities.asmFor(DoSDashboardASM.class, DoSDashboardASM.IDENTITY);

      if (dashboardASM.currentState().equals(DoSDashboardState.DOS_AUDIT_ONGOING)) {
        ASMUtilities.step(DOS_ROUND_COMPLETE_EVENT, DoSDashboardASM.class,
                          DoSDashboardASM.IDENTITY);
        LOGGER.debug("[notifyRoundComplete stepped DOS_ROUND_COMPLETE_EVENT]");
      }
    }
  }

  /**
   * Notifies the county and DoS dashboards that the audit is complete.
   */
  private void notifyAuditCompleteForDoS() {
    ASMUtilities.step(COUNTY_AUDIT_COMPLETE_EVENT, CountyDashboardASM.class,
                      my_asm.get().identity());
    // check to see if all counties are complete
    boolean all_complete = true;
    for (final County c : Persistence.getAll(County.class)) {
      final CountyDashboardASM asm =
          ASMUtilities.asmFor(CountyDashboardASM.class, String.valueOf(c.id()));
      all_complete &= asm.isInFinalState();
    }
    if (all_complete) {
      ASMUtilities.step(DOS_AUDIT_COMPLETE_EVENT, DoSDashboardASM.class,
                        DoSDashboardASM.IDENTITY);
    }
  }

  /**
   * 
   * Marks a county as done, marks the risk limit achieved event and
   * county as audit complete.
   * 
   * Technically the county should be done at this point. The If check
   * is a bit redundant but it was in the code so kept for an additional
   * check.
   * 
   * @param cdb County dashboard who signed off
   */
  private void markCountyAsDone(CountyDashboard cdb) {
    final CountyDashboardASM countyDashboardASM =
        ASMUtilities.asmFor(CountyDashboardASM.class, String.valueOf(cdb.id()));
    final AuditBoardDashboardASM auditBoardASM =
        ASMUtilities.asmFor(AuditBoardDashboardASM.class, String.valueOf(cdb.id()));
    final Boolean inProgress =
        auditBoardASM.currentState().equals(ROUND_IN_PROGRESS) || auditBoardASM.currentState()
            .equals(ROUND_IN_PROGRESS_NO_AUDIT_BOARD);

    if (countyDashboardASM.currentState().equals(CountyDashboardState.COUNTY_AUDIT_UNDERWAY) &&
        !inProgress && cdb.allAuditsComplete()) {
      final List<ComparisonAudit> terminated = cdb.endSingleCountyAudits();
      LOGGER.debug(String
          .format("[markCountyAsDone: all audits finished in %s County." +
                  " Terminated these audits: %s]", cdb.county().name(), terminated));
      auditBoardASM.stepEvent(RISK_LIMIT_ACHIEVED_EVENT);
      countyDashboardASM.stepEvent(COUNTY_AUDIT_COMPLETE_EVENT);

      ASMUtilities.save(auditBoardASM);
      ASMUtilities.save(countyDashboardASM);
    }
  }

  private boolean allCountyAuditBoardsSignedOff() {

    for (CountyDashboard cdb : Persistence.getAll(CountyDashboard.class)) {
      final CountyDashboardASM countyDashboardASM =
          ASMUtilities.asmFor(CountyDashboardASM.class, String.valueOf(cdb.id()));
      LOGGER.debug(MessageFormat
          .format("County={0} dashboard state={1} auditBoardCount={2} currentRound={3}",
                  cdb.county().name(), countyDashboardASM.currentState(),
                  cdb.auditBoardCount(), cdb.currentRound()));

      if (!countyDashboardASM.currentState()
          .equals(CountyDashboardState.COUNTY_AUDIT_UNDERWAY)) {
        continue;
      } // do not include counties where audit is not underway

      final boolean currentRoundNotSignedOff =
          cdb.currentRound() != null && ((cdb.currentRound().signatories().size() == 0) ||
                                         (cdb.currentRound().signatories().size() < cdb.auditBoardCount()));

      if (currentRoundNotSignedOff) {
        LOGGER.info("allCountyAuditBoardsSignedOff: false");
        return false;
      }
    }
    LOGGER.info("allCountyAuditBoardsSignedOff: true");
    return true;
  }

  private boolean participatesInStateAudit(CountyDashboard currentCountyDashboard) {
    boolean returnVal = (currentCountyDashboard.getAudits().stream()
        .filter(audit -> audit.getCounties().size() > 1)
        .filter(ca -> ca.auditReason() != AuditReason.OPPORTUNISTIC_BENEFITS)
        .filter(audit -> !audit.isHandCount()).count() > 0);
    LOGGER.debug(MessageFormat.format("participatesInStateAudit: {0}", returnVal));
    return returnVal;
  }

  private void logAuditsForCountyDashboard(CountyDashboard cd) {
    LOGGER.debug(MessageFormat.format("{0} {1} {2} {3}", "Audit Name", "Audit Reason",
                                     "Audit Status", "Targeted"));
    for (ComparisonAudit ca : cd.getAudits()) {
      LOGGER.debug(MessageFormat.format("{0} {1} {2} {3}", ca.getContestName(),
                                       ca.auditReason(), ca.auditStatus(), ca.isTargeted()));
    }
  }

}
