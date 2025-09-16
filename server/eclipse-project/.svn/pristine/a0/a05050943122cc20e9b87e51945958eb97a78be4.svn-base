package us.freeandfair.corla.controller;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent;
import us.freeandfair.corla.asm.ASMUtilities;
import us.freeandfair.corla.asm.CountyDashboardASM;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.DoSDashboard;
import us.freeandfair.corla.model.ImportStatus;
import us.freeandfair.corla.model.ImportStatus.ImportState;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.BallotManifestInfoQueries;
import us.freeandfair.corla.query.CastVoteRecordQueries;
import us.freeandfair.corla.query.CountyContestResultQueries;

/**
 *
 */
public abstract class DeleteFileController {

  /**
   * Class-wide logger
   */
  public static final Logger LOGGER =
    LogManager.getLogger(DeleteFileController.class);

  /**
   * Perform all the steps to undo a file upload.
   * fileType can be either "bmi" or "cvr"
   * returns true if all the steps succeeded, false if one or more failed
   * if any steps don't succeed they will throw a DeleteFileFail exception
   */
  public static Boolean deleteFile(final Long countyId, final String fileType)
    throws DeleteFileFail {
    if ("cvr".equals(fileType)) {
      LOGGER.info("deleting a CVR file for countyId: " + countyId);

      // deleteCastVoteRecords will also delete cvr_contest_infos due to
      // constraints, also, deleteCastVoteRecords needs to be called before
      // contests are deleted due to constraints
      deleteCastVoteRecords(countyId);
      deleteResultsAndContests(countyId);
    } else if ("bmi".equals(fileType)) {
      LOGGER.info("deleting a BMI file for countyId: " + countyId);
      deleteBallotManifestInfos(countyId);
    } else {
      throw new DeleteFileFail("Did not recognize fileType: " + fileType);
    }

    resetDashboards(countyId, fileType);
    return true;
  }

  /** reset cvr file info or bmi info on the county dashboard **/
  public static Boolean resetDashboards(final Long countyId, final String fileType)
    throws DeleteFileFail {
    final CountyDashboard cdb = Persistence.getByID(countyId, CountyDashboard.class);
    if ("cvr".equals(fileType)) {
      resetDashboardCVR(cdb);
      final DoSDashboard dosdb = Persistence.getByID(DoSDashboard.ID, DoSDashboard.class);
      dosdb.removeContestsToAuditForCounty(cdb.county());
      LOGGER.debug("Removed contests to audit for county");
    } else if ("bmi".equals(fileType)) {
      resetDashboardBMI(cdb);
    } else {
      throw new DeleteFileFail("Did not recognize fileType: " + fileType);
    }

    // this must come after the other resetDashboard*s
    reinitializeCDB(cdb);
    return true;
  }

  /** reset cvr file info on the county dashboard **/
  public static Boolean resetDashboardCVR(final CountyDashboard cdb) {
    Persistence.delete(cdb.cvrFile());
    cdb.setCVRFile(null);
    cdb.setCVRsImported(0);
    cdb.setCVRImportStatus(new ImportStatus(ImportState.NOT_ATTEMPTED));
    LOGGER.debug("Updated the county dashboard to remove CVR stuff");
    return true;
  }

  /**
   * Re-initialize county dashboard ASM state based on newly-deleted files
   *
   * Uses an ASM shortcut if both are deleted, otherwises assumes that before
   * the deletion, both the CVR and ballot manifests were uploaded successfully,
   * and transitions the ASM backward to indicate removal of the respective
   * files.
   *
   * @param cdb the county dashboard to modify
   */
  public static void reinitializeCDB(final CountyDashboard cdb) {
    final CountyDashboardASM cdbASM =
        ASMUtilities.asmFor(CountyDashboardASM.class, String.valueOf(cdb.id()));

    // no CVR, no manifest
    if (null == cdb.cvrFile() && null == cdb.manifestFile()) {
      cdbASM.reinitialize();
      ASMUtilities.save(cdbASM);
    // no CVR, yes manifest
    } else if (null == cdb.cvrFile() && null != cdb.manifestFile()) {
      cdbASM.stepEvent(CountyDashboardEvent.DELETE_CVRS_EVENT);
      ASMUtilities.save(cdbASM);
    // yes CVR, no manifest
    } else if (null != cdb.cvrFile() && null == cdb.manifestFile()) {
      cdbASM.stepEvent(CountyDashboardEvent.DELETE_BALLOT_MANIFEST_EVENT);
      ASMUtilities.save(cdbASM);
    }
  }

  /** reset bmi info on the county dashboard **/
  public static Boolean resetDashboardBMI(final CountyDashboard cdb) {
    Persistence.delete(cdb.manifestFile());
    cdb.setManifestFile(null);
    cdb.setBallotsInManifest(0);
    LOGGER.debug("Updated the county dashboard to remove BMI stuff");
    return true;
  }

  /**
   * Remove all CountyContestResults and Contests for a county
   */
  public static void deleteResultsAndContests(final Long countyId)
    throws DeleteFileFail {
    // this will also delete the contests - surprise!
    final Integer rowsDeleted = CountyContestResultQueries.deleteForCounty(countyId);
    LOGGER.info(String.format("%d ContestResults and Contests deleted!", rowsDeleted));
  }

  /**
   * Remove all CastVoteRecords for a county
   */
  public static void deleteCastVoteRecords(final Long countyId)
    throws DeleteFileFail {
    final Integer rowsDeleted = CastVoteRecordQueries.deleteAll(countyId);
    LOGGER.info(String.format("%d cvrs deleted!", rowsDeleted));
  }

  /**
   * Remove all BallotManifestInfo for a county
   * @param countyId
   */
  public static void deleteBallotManifestInfos(final Long countyId)
    throws DeleteFileFail {
    final Integer rowsDeleted = BallotManifestInfoQueries.deleteMatching(countyId);
    LOGGER.info(String.format("%d bmis deleted!", rowsDeleted));
  }

  /** used to abort the set of operations (transaction) **/
  public static class DeleteFileFail extends Exception {

    /** used to abort the set of operations (transaction) **/
    public DeleteFileFail(final String message) {
      super(message);
    }
  }
}
