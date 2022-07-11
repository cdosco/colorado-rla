/*
 * Colorado RLA System
 *
 * @title ColoradoRLA
 * @copyright 2018 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-LGO3.0-or-later
 * @creator Democracy Works, Inc. <dev@democracy.works>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.endpoint;

import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import spark.Request;
import spark.Response;
import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent;
import us.freeandfair.corla.json.CanonicalUpdate;
import us.freeandfair.corla.json.CanonicalUpdate.ChoiceChange;
import us.freeandfair.corla.model.AuditInfo;
import us.freeandfair.corla.model.Contest;
import us.freeandfair.corla.model.CountyContestResult;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CastVoteRecordQueries;
import us.freeandfair.corla.query.CountyContestResultQueries;

import javax.persistence.PersistenceException;
import java.lang.reflect.Type;
import java.util.List;

import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.COMPLETE_AUDIT_INFO_EVENT;
import static us.freeandfair.corla.asm.ASMEvent.DoSDashboardEvent.PARTIAL_AUDIT_INFO_EVENT;

/**
 * The endpoint for renaming contests.
 *
 * This allows allows the state to rename contests uploaded by counties that
 * may not conform to the state specifications.
 *
 * @author Democracy Works, Inc. <dev@democracy.works>
 */
// TODO: This rule and checkstyle conflict. We need to pick one or the other,
// but with both we need a suppression rule for one of them.
@SuppressWarnings({"PMD.AtLeastOneConstructor"})
public class SetContestNames extends AbstractDoSDashboardEndpoint {

  /**
   * Class-wide logger
   */
  public static final Logger LOGGER =
    LogManager.getLogger(SetContestNames.class);

  /**
   * The event to return for this endpoint.
   */
  private final ThreadLocal<ASMEvent> asmEvent = new ThreadLocal<ASMEvent>();

  /**
   * Type information for the new contest names.
   */
  private static final Type TYPE_TOKEN =
    new TypeToken<List<CanonicalUpdate>>(){}.getType();

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
    return "/set-contest-names";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ASMEvent endpointEvent() {
    return asmEvent.get();
  }

  /**
   * Updates contest names based on the DoS-preferred contest names.
   *
   * @param request HTTP request
   * @param response HTTP response
   */
  @Override
  public String endpointBody(final Request request, final Response response) {

    try {
      final List<CanonicalUpdate> canons = Main.GSON.fromJson(request.body(), TYPE_TOKEN);
       if (canons == null) {
        badDataContents(response, "malformed contest mappings");
      } else {
        final DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
        if (dosdb == null) {
          serverError(response, "could not set contest mappings");
        }
        final int updateCount = changeNames(canons);
         asmEvent.set(nextEvent(dosdb));
        ok(response, String.format("re-mapped %d contest names", updateCount));
      }
    } catch (final PersistenceException e) {
      serverError(response, "unable to re-map contest names");
    } catch (final JsonParseException e) {
      LOGGER.error("JsonParseException causing malformed error", e);
      badDataContents(response, "malformed contest mapping");
    } catch (final Exception e) {
      badDataContents(response, "Exception");
    }
    return my_endpoint_result.get();
  }

  private int changeNames(final List<CanonicalUpdate> canons) {

    int updateCount = 0;
    for (final CanonicalUpdate canon : canons) {


      final Long id = Long.parseLong(canon.contestId);
      final Contest contest = Persistence.getByID(id, Contest.class);
      // change contest name
      if (null != canon.name) {
        contest.setName(canon.name);
      }
      // change choice names
      if (null == canon.choices) {
        LOGGER.info("canon.choices IS NULL");
      }
      if (null != canon.choices) {
        for (final ChoiceChange choiceChange: canon.choices) {
          if (null != choiceChange.oldName
              && null != choiceChange.newName
              && !choiceChange.oldName.equals(choiceChange.newName)) {
            
            LOGGER.info("changing choice name as part of canonicalization:\n   "
                        + choiceChange.oldName +" -> "+ choiceChange.newName
                        + " contest: " + contest.name() + " county: " + contest.county());
            contest.updateChoiceName(choiceChange.oldName, choiceChange.newName);
            
            CastVoteRecordQueries.updateCVRContestInfos(contest.county().id(),
                                                        contest.id(),
                                                        choiceChange.oldName,
                                                        choiceChange.newName);
            final CountyContestResult ccr = CountyContestResultQueries.matching(contest.county(), contest);
            ccr.updateChoiceName(choiceChange.oldName, choiceChange.newName);
            Persistence.update(ccr);
          }
        }
      }

      updateCount += 1;
    }
    return updateCount;
  }

  /**
   * Computes the event of this endpoint based on audit info completeness.
   *
   * @param dosDashboard The DoS dashboard.
   */
  private ASMEvent nextEvent(final DoSDashboard dosDashboard) {
    final ASMEvent result;
    final AuditInfo info = dosDashboard.auditInfo();

    if (info.electionDate() == null || info.electionType() == null ||
        info.publicMeetingDate() == null || info.riskLimit() == null ||
        info.seed() == null || dosDashboard.contestsToAudit().isEmpty()) {
      result = PARTIAL_AUDIT_INFO_EVENT;
    } else {
      result = COMPLETE_AUDIT_INFO_EVENT;
    }

    return result;
  }
}
