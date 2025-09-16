/*
 * Free & Fair Colorado RLA System
 *
 * @title ColoradoRLA
 * @created Jul 25, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Joey Dodds <jdodds@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.OptionalInt;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import us.freeandfair.corla.model.BallotManifestInfo;
import us.freeandfair.corla.persistence.Persistence;

/**
 * The parser for Colorado ballot manifests.
 *
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
public class ColoradoBallotManifestParser {
  /**
   * Class-wide logger
   */
  public static final Logger LOGGER =
      LogManager.getLogger(ColoradoBallotManifestParser.class);

  /**
   * The size of a batch of ballot manifests to be flushed to the database.
   */
  private static final int BATCH_SIZE = 50;

  /**
   * The column containing the scanner ID.
   */
  private static final int SCANNER_ID_COLUMN = 1;

  /**
   * The column containing the batch number.
   */
  private static final int BATCH_NUMBER_COLUMN = 2;

  /**
   * The column containing the number of ballots in the batch.
   */
  private static final int NUM_BALLOTS_COLUMN = 3;

  /**
   * The column containing the storage location.
   */
  private static final int BATCH_LOCATION_COLUMN = 4;

  /**
   * The parser to be used.
   */
  private final CSVParser my_parser;

  /**
   * The county ID to apply to the parsed manifest lines.
   */
  private final Long my_county_id;

  /**
   * The number of ballots represented by the parsed records.
   */
  private int my_ballot_count = -1;

  /**
   * The set of parsed ballot manifests that haven't yet been flushed to the
   * database.
   */
  private final Set<BallotManifestInfo> my_parsed_manifests = new HashSet<>();

  /**
   * Construct a new Colorado ballot manifest parser using the specified Reader.
   *
   * @param the_reader The reader from which to read the CSV to parse.
   * @param the_timestamp The timestamp to apply to the parsed records.
   * @param the_county_id The county ID for the parsed records.
   * @exception IOException if an error occurs while constructing the parser.
   */
  public ColoradoBallotManifestParser(final Reader the_reader,
                                      final Long the_county_id)
      throws IOException {
    my_parser = new CSVParser(the_reader, CSVFormat.DEFAULT);
    my_county_id = the_county_id;
  }

  /**
   * Construct a new Colorado ballot manifest parser using the specified String.
   *
   * @param the_string The CSV string to parse.
   * @param the_timestamp The timestamp to apply to the parsed records.
   * @param the_county_id The county ID for the parsed records.
   * @exception IOException if an error occurs while constructing the parser.
   */
  public ColoradoBallotManifestParser(final String the_string,
                                      final Long the_county_id)
      throws IOException {
    my_parser = CSVParser.parse(the_string, CSVFormat.DEFAULT);
    my_county_id = the_county_id;
  }

  /**
   * Checks to see if the set of parsed manifests needs flushing, and does so
   * if necessary.
   */
  private void checkForFlush() {
    if (my_parsed_manifests.size() % BATCH_SIZE == 0) {
      Persistence.flush();
      for (final BallotManifestInfo bmi : my_parsed_manifests) {
        Persistence.evict(bmi);
      }
      my_parsed_manifests.clear();
    }
  }

  /**
   * Extracts ballot manifest information from a single CSV line.
   *
   * @param the_line The CSV line.
   * @param the_timestamp The timestamp to apply to the result.
   * @return the extracted information.
   */
  private BallotManifestInfo extractBMI(final CSVRecord the_line) {
    BallotManifestInfo bmi = null;

    final int batch_size = Integer.parseInt(the_line.get(NUM_BALLOTS_COLUMN));
    final Long sequence_start;
    if (my_ballot_count == 0) {
      // this is the first row. also, sequence is not zero based
      sequence_start = 1L;
    } else {
      // rest of the rows. also, batch sequences don't overlap or touch
      sequence_start = Long.valueOf(my_ballot_count) + 1L;
    }
    // this is used to set my_ballot_count below
    final Long sequence_end = sequence_start + Long.valueOf(batch_size) - 1L;
    // TODO: should we check for mismatched county IDs between the
    // one we were passed at construction and the county name string
    // in the file?
    bmi = new BallotManifestInfo(my_county_id,
                                    Integer.parseInt(the_line.get(SCANNER_ID_COLUMN)),
                                    the_line.get(BATCH_NUMBER_COLUMN),
                                    batch_size,
                                    the_line.get(BATCH_LOCATION_COLUMN),
                                    sequence_start,
                                    sequence_end);
    Persistence.saveOrUpdate(bmi);
    my_parsed_manifests.add(bmi);
    checkForFlush();
    LOGGER.debug("parsed ballot manifest: " + bmi);

    return bmi;
  }

  /**
   * Parse the supplied data export. If it has already been parsed, this
   * method returns immediately.
   *
   * @return true if the parse was successful, false otherwise
   */
  public synchronized Result parse() {
    final Result result = new Result();
    final Iterator<CSVRecord> records = my_parser.iterator();

    int my_record_count = 0;
    my_ballot_count = 0;
    // bmi line may not have been initialized
    CSVRecord bmi_line = null;
    BallotManifestInfo bmi;

    try {
      // we expect the first line to be the headers, which we currently discard
      records.next();
      // subsequent lines contain ballot manifest info
      while (records.hasNext()) {
        bmi_line = records.next();
        bmi = extractBMI(bmi_line);
        my_record_count = my_record_count + 1;
        my_ballot_count = Math.toIntExact(bmi.sequenceEnd());
      }

      result.success = true;
      result.importedCount = my_record_count;
    } catch (final IllegalStateException | NoSuchElementException e) {
      result.success = false;
      result.errorMessage = e.getClass().toString() + " " + e.getMessage();
      result.errorRowNum = my_record_count;
      if (null != bmi_line) {
        final List<String> values = new ArrayList<>();
        bmi_line.iterator().forEachRemaining(values::add);
        result.errorRowContent = String.join(",", values);
      }
      // this log message is partially here to make findbugs happy. For some
      // reason URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD would not be suppressed.
      LOGGER.error(e.getClass().toString() + " " + e.getMessage()
                   + "\n line number: " + result.errorRowNum
                   + "\n content:" + result.errorRowContent);
    }

    return result;
  }

  /**
   * {@inheritDoc}
   */
  public synchronized OptionalInt ballotCount() {
    if (my_ballot_count < 0) {
      return OptionalInt.empty();
    } else {
      return OptionalInt.of(my_ballot_count);
    }
  }
}
