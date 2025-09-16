/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 13, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@freeandfair.us>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.json;

/**
 * App Info returned by the result
 * 
 * @author Dogan Cibiceli <dogan.cibiceli@sos.state.co.us>
 * @version 1.0.0
 */
public class AppInfoJSON {

  /* ~semantic versioning such as 2.3.33  */
  private final String versionInfo;
  
  /**
   * Constructs a new AppInfo.
   * 
   * @param versionInfo Only one field for now.
   */
  public AppInfoJSON(final String versionInfo) {
    this.versionInfo = versionInfo;
  }
  
  /**
   * @return the version info.
   */
  public String getVersionInfo() {
    return versionInfo;
  }
}
