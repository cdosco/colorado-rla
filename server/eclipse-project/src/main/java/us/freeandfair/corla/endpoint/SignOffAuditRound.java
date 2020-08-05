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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

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

        // update the ASM state for the county and maybe DoS
        if (!DISABLE_ASM) {
          final boolean auditComplete;
          LOGGER.debug(String
              .format("[signoff for %s County: cdb.estimatedSamplesToAudit()=%d," +
                      " cdb.auditedSampleCount()=%d," + " cdb.ballotsAudited()=%d]",
                      cdb.county().name(), cdb.estimatedSamplesToAudit(),
                      cdb.auditedSampleCount(), cdb.ballotsAudited()));
          List<List<String>> resultReports = ReportRows.genSumResultsReport();
          Set<ComparisonAudit> audits = cdb.getAudits();
          Set<String> contestStrs = new HashSet<>();
          for (ComparisonAudit audit : audits) {
            contestStrs.add(audit.getContestName());
          }
          boolean isRisk = false;
          for (int i = 0; i < resultReports.size(); i++) {
            List<String> name = resultReports.get(i);
            if (contestStrs.contains(name.get(0))) {
              if (name.get(3).equalsIgnoreCase("No")) {
                isRisk = true;
                for (ComparisonAudit ca : audits) {
                  if (ca.getContestName().equalsIgnoreCase(name.get(0))) {
                    ca.setAuditStatus(AuditStatus.IN_PROGRESS);
                    Persistence.saveOrUpdate(ca);
                    break;
                  }
                }
              }
            }
          }
          if ((cdb.allAuditsComplete()) && (!isRisk)) {
            // my_event.set(RISK_LIMIT_ACHIEVED_EVENT);
            // In this case, we'd be terminating single county audits
            // for opportunistic benefits only.
            final List<ComparisonAudit> terminated = cdb.endSingleCountyAudits();
            LOGGER.debug(String.format("[signoff: all targeted audits finished in %s County." +
                                       " Terminated these audits: %s]", cdb.county().name(),
                                       terminated));

            my_event.set(ROUND_SIGN_OFF_EVENT);
            auditComplete = true;
            // final CountyDashboardASM countyDashboardASM =
            // ASMUtilities.asmFor(CountyDashboardASM.class,
            // String.valueOf(cdb.id()));
            // countyDashboardASM.stepEvent(COUNTY_AUDIT_COMPLETE_EVENT);

            // ASMUtilities.save(countyDashboardASM);
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
            // Check if any County is in sign-off

          } else {
            LOGGER.debug("[signoff: the round ended normally]");
            auditComplete = false;
            my_event.set(ROUND_SIGN_OFF_EVENT);
          }

          if (auditComplete) {
            LOGGER.info(String.format("[signoff: round complete in %s County]",
                                      cdb.county().name()));
            // notifyRoundComplete(cdb.id());

            LOGGER.info(String.format("[signoff: audit complete in %s County]",
                                      cdb.county().name()));
            boolean foundSignOff = false;
            List<CountyDashboard> countyDashboardList =
                Persistence.getAll(CountyDashboard.class);
            if (countyDashboardList.size() == 0) {
              foundSignOff = true;
            }
            for (final CountyDashboard cdbIn : countyDashboardList) {
              if (cdbIn.id().intValue() == cdb.id().intValue()) {
                continue;
              }
              resultReports = ReportRows.genSumResultsReport();
              audits = cdbIn.getAudits();
              contestStrs = new HashSet<>();
              for (ComparisonAudit audit : audits) {
                contestStrs.add(audit.getContestName());
              }
              isRisk = false;
              for (int j = 0; j < resultReports.size(); j++) {
                List<String> name2 = resultReports.get(j);
                if (contestStrs.contains(name2.get(0))) {
                  if (name2.get(3).equalsIgnoreCase("No")) {
                    isRisk = true;
                    break;
                  }
                }
              }
              // ASMUtilities.asmFor(AuditBoardDashboardASM.class,
              // String.valueOf(cdbIn.id()));
              if (isRisk) {
                foundSignOff = true;
                break;
              } else if (!cdbIn.allAuditsComplete()) {
                final CountyDashboardASM countyDashboardASM =
                    ASMUtilities.asmFor(CountyDashboardASM.class, String.valueOf(cdbIn.id()));
                if (countyDashboardASM.currentState()
                    .equals(CountyDashboardState.COUNTY_AUDIT_UNDERWAY)) {
                  countyDashboardASM.stepEvent(COUNTY_AUDIT_COMPLETE_EVENT);

                  ASMUtilities.save(countyDashboardASM);
                }
                break;
              }
            }
            // No Dign Offs
            if (foundSignOff == false) {
              for (final CountyDashboard cdbIn : countyDashboardList) {
                if (cdb.id().equals(cdbIn.id())) {
                  final CountyDashboardASM countyDashboardASM = ASMUtilities
                      .asmFor(CountyDashboardASM.class, String.valueOf(cdbIn.id()));
                  ASMUtilities.asmFor(AuditBoardDashboardASM.class,
                                      String.valueOf(cdbIn.id()));
                  final List<ComparisonAudit> terminated = cdb.endSingleCountyAudits();
                  LOGGER.debug(String
                      .format("[notifyRoundComplete: all targeted audits finished in %s County." +
                              " Terminated these audits: %s]", cdbIn.county().name(),
                              terminated));
                  LOGGER.info(String
                      .format("[notifyRoundComplete: allAuditsComplete! %s County is FINISHED.]",
                              cdbIn.county().name()));
                  if (countyDashboardASM
                      .currentState() != ASMState.CountyDashboardState.COUNTY_AUDIT_COMPLETE) {
                    countyDashboardASM.stepEvent(COUNTY_AUDIT_COMPLETE_EVENT);
                    ASMUtilities.save(countyDashboardASM);
                  }
                  continue;
                }
                // In case this cdb was waiting for one of their cross-county
                // audits to
                // finish, we can say they are done now, and they won't have to
                // start a
                // round and sign in to find out that they have nothing to do.
                // -!- this is the same logic in StartAuditRound
                final CountyDashboardASM countyDashboardASM =
                    ASMUtilities.asmFor(CountyDashboardASM.class, String.valueOf(cdbIn.id()));
                final AuditBoardDashboardASM auditBoardASM = ASMUtilities
                    .asmFor(AuditBoardDashboardASM.class, String.valueOf(cdbIn.id()));
                final Boolean inProgress =
                    auditBoardASM.currentState().equals(ROUND_IN_PROGRESS);

                if (countyDashboardASM.currentState()
                    .equals(CountyDashboardState.COUNTY_AUDIT_UNDERWAY) && !inProgress &&
                    cdbIn.allAuditsComplete()) {
                  final List<ComparisonAudit> terminated = cdb.endSingleCountyAudits();
                  // LOGGER.debug(String.format("[notifyRoundComplete:
                  // finished=%b, the_id=%d, cdb=%s]",
                  // cdbIn.id(), cdbIn.toString()));
                  LOGGER.debug(String
                      .format("[notifyRoundComplete: all targeted audits finished in %s County." +
                              " Terminated these audits: %s]", cdbIn.county().name(),
                              terminated));
                  LOGGER.info(String
                      .format("[notifyRoundComplete: allAuditsComplete! %s County is FINISHED.]",
                              cdbIn.county().name()));

                  auditBoardASM.stepEvent(RISK_LIMIT_ACHIEVED_EVENT);
                  countyDashboardASM.stepEvent(COUNTY_AUDIT_COMPLETE_EVENT);

                  ASMUtilities.save(auditBoardASM);
                  ASMUtilities.save(countyDashboardASM);
                }
              }
              ASMUtilities.asmFor(DoSDashboardASM.class, DoSDashboardASM.IDENTITY);
              ASMUtilities.step(DOS_AUDIT_COMPLETE_EVENT, DoSDashboardASM.class,
                                DoSDashboardASM.IDENTITY);
              LOGGER.debug("[notifyRoundComplete stepped DOS_ROUND_COMPLETE_EVENT]");
            }
          } else {
            LOGGER.info(String.format("[signoff: round complete in %s County]",
                                      cdb.county().name()));
            // notifyRoundComplete(cdb.id());
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
}
