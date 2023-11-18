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

import java.util.List;

import spark.Request;
import spark.Response;

import us.freeandfair.corla.asm.AbstractStateMachine;
import us.freeandfair.corla.asm.AuditBoardDashboardASM;
import us.freeandfair.corla.asm.CountyDashboardASM;
import us.freeandfair.corla.asm.DoSDashboardASM;
import us.freeandfair.corla.asm.PersistentASMState;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyContestResult;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.DatabaseResetQueries;
import us.freeandfair.corla.query.PersistentASMStateQueries;

/**
 * Reset the database, except for authentication information and uploaded
 * artifact data (the latter is cleaned up at the database level, not by this 
 * code).
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
// the endpoint method here is long and has lots of loops, but is not
// at all difficult to understand
@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.ModifiedCyclomaticComplexity",
    "PMD.CyclomaticComplexity", "PMD.StdCyclomaticComplexity", "PMD.NPathComplexity"})
public class ResetAudit extends AbstractEndpoint {
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
    return "/reset-audit";
  }

  /** 
   * {@inheritDoc}
   */
  @Override
  public String asmIdentity(final Request the_request) {
    return null;
  }
  
  /**
   * {@inheritDoc} 
   */
  @Override
  public Class<AbstractStateMachine> asmClass() {
    return null;
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
  public String endpointBody(final Request the_request,
                         final Response the_response) {
    
    
    // create new dashboards
    List<DoSDashboard> dosdbList = Persistence.getAll(DoSDashboard.class);
    for(DoSDashboard dosdb : dosdbList) {
      Persistence.delete(dosdb);      
    }

    // create new dashboards
    final DoSDashboard dosdb = new DoSDashboard();
    Persistence.saveOrUpdate(dosdb);

    ok(the_response, "database audit");
    return my_endpoint_result.get();
  }
}
