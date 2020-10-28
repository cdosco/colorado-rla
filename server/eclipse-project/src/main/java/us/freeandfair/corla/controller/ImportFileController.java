package us.freeandfair.corla.controller;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;

import us.freeandfair.corla.Main;
import us.freeandfair.corla.asm.ASMEvent.CountyDashboardEvent;
import us.freeandfair.corla.asm.CountyDashboardASM;
import us.freeandfair.corla.asm.ASMUtilities;
import us.freeandfair.corla.csv.DominionCVRExportParser;
import us.freeandfair.corla.csv.Result;
import us.freeandfair.corla.json.UploadedFileDTO;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.model.ImportStatus;
import us.freeandfair.corla.model.ImportStatus.ImportState;
import us.freeandfair.corla.model.UploadedFile.FileStatus;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.CastVoteRecordQueries;
import us.freeandfair.corla.query.CountyContestResultQueries;
import us.freeandfair.corla.query.UploadedFileQueries;
import us.freeandfair.corla.util.UploadedFileStreamer;

public class ImportFileController implements Runnable {

  /**
   * Class-wide logger
   */
  public static final Logger LOGGER =
    LogManager.getLogger(ImportFileController.class);


  private UploadedFileDTO uploadedFileDTO;
  private Long countyId;

  /**
   * Constructs a new ImportFileController for the given file info which can be run
   * in a separate, independent, thread.
   *
   */
  public ImportFileController(final UploadedFileDTO upF) {
    this.uploadedFileDTO = upF;
    this.countyId = upF.getCountyId();
  }

  public void run() {
    LOGGER.debug("run()");
    try {
      // We need the endpoint transaction, which sets the cdb state to
      // "importing", to finish first. This is an easy way to dodge updates to
      // the same hibernate object across threads. I have no idea why this
      // didn't work for the UploadedFile object.
      Thread.sleep(1000);

      // There is lots of transaction management here because we want to be sure
      // that an error gets written to the database and not rolled back. We also
      // want to make sure hibernate does not overwrite any non-hibernate
      // updates. We have non-hibernate updates because we are in a thread that
      // shares a hibernate session with other threads which all want to use the
      // same object: the UploadedFile. Instead of that UploadedFile, we have
      // have a UploadedFileDTO(Data Transfer Object) which is not a hibernate
      // object and does not get updates across threads.
      Persistence.beginTransaction();
      cleanSlate();
      commit();

      setCVRFile();
      commit();

      runOnThread();
      Persistence.flush();
      Persistence.commitTransaction();
    } catch (final RuntimeException | java.lang.InterruptedException e) {
      final Result result = new Result();
      result.success = false;
      result.errorMessage = e.getClass() +" "+ e.getMessage();
      error(result);
      Persistence.flush();
      Persistence.commitTransaction();
    }
  }

  public void runOnThread() {
    LOGGER.debug("runOnThread()");
    Result result = parse();
    if (result.success) {
      success(result);
    } else {
      error(result);
    }
  }


  // Note: setCVRFile must come after the cdb is saved so it isn't overwritten.
  // We want CVRFile set, whether the import succeeds or fails, for CDOS to be able
  // to examine it
  public void setCVRFile() {
    UploadedFileQueries.setCVRFileOnCounty(this.uploadedFileDTO);
  }


  /**
   * Aborts the import with the specified error description.
   *
   * This does everything that deleteFile does, except for:
   *     cdb.setCVRFile(null)
   *  because the state may want to examine the file
   *
   * @param the_description The error description.
   */
  public void error(final Result result) {
    LOGGER.debug("error("+ result.errorMessage + ")");

    // record the result
    commit();
    this.uploadedFileDTO.setStatus(FileStatus.FAILED.toString());
    this.uploadedFileDTO.setResult(result);
    UploadedFileQueries.updateStatusAndResult(this.uploadedFileDTO);
    commit();


    // update status and state machine
    final CountyDashboard cdb =
      Persistence.getByID(this.countyId, CountyDashboard.class);
    final CountyDashboardASM cdb_asm =
      ASMUtilities.asmFor(CountyDashboardASM.class, this.countyId.toString());

    cdb.setCVRImportStatus(new ImportStatus(ImportState.FAILED, result.errorMessage));
    cdb.setCVRsImported(0);
    cdb_asm.stepEvent(CountyDashboardEvent.CVR_IMPORT_FAILURE_EVENT);
    ASMUtilities.save(cdb_asm);
    Persistence.saveOrUpdate(cdb);

    // then delete any imported cvrs
    cleanSlate();

    LOGGER.error(result.errorMessage + this.uploadedFileDTO.toString());
  }


  public void success(final Result result) {

    // update status and state machine
    final CountyDashboard cdb =
      Persistence.getByID(this.countyId, CountyDashboard.class);
    final CountyDashboardASM cdb_asm =
      ASMUtilities.asmFor(CountyDashboardASM.class, this.countyId.toString());

    cdb.setCVRImportStatus(new ImportStatus(ImportState.SUCCESSFUL));
    cdb.setCVRsImported(result.importedCount);
    cdb_asm.stepEvent(CountyDashboardEvent.CVR_IMPORT_SUCCESS_EVENT);
    ASMUtilities.save(cdb_asm);
    Persistence.saveOrUpdate(cdb);

    // record the result
    commit();
    this.uploadedFileDTO.setStatus(FileStatus.IMPORTED.toString());
    this.uploadedFileDTO.setResult(result);
    UploadedFileQueries.updateStatusAndResult(this.uploadedFileDTO);
    commit();

    LOGGER.info(result.importedCount + " CVRs parsed from file " + this.uploadedFileDTO.toString());
  }

  public void cleanSlate() {
    LOGGER.debug("cleanSlate()");

    CastVoteRecordQueries.deleteAll(this.countyId);
    //seems like an extra saftey gaurantee is needed here to protect
    //against foreign key violations, not sure why
    commit();
    CountyContestResultQueries.deleteForCounty(this.countyId);
    commit();
  }

  public void commit() {
    Persistence.flush();
    Persistence.commitTransaction();
    Persistence.beginTransaction();
 }

  /**
   * Parses an uploaded CVR export and attempts to persist it to the database.
   * with the default impl UploadedFileStreamer
   *
   */
  public Result parse() {
    UploadedFileStreamer ufs = new UploadedFileStreamer(this.uploadedFileDTO);
    try {
      (new Thread(ufs)).start();
      return parse(ufs.inputStream());
    } finally {
      ufs.stop();
    }
  }

  /**
   * Parses an uploaded CVR export and attempts to persist it to the database.
   * with the default given InputStreamReader
   *
   */
  public Result parse(final InputStream inputStream) {
    LOGGER.debug("parse()");
    try {
      InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                                                                  "UTF-8");
      final DominionCVRExportParser parser =
        new DominionCVRExportParser(inputStreamReader,
                                    Persistence.getByID(this.countyId,
                                                        County.class),
                                    Main.properties(),
                                    true);
      return parser.parse();
    } catch (final RuntimeException | java.io.IOException e) {
      // we could make parse() catch all possible exceptions because it already
      // catches some, but we'll keep this here for now as a short cut.
      LOGGER.error(e.getMessage());
      LOGGER.error(e.getClass());
      Result parseResult = new Result();
      parseResult.success = false;
      parseResult.errorMessage = "System Error";
      return parseResult;
    }
  }
}
