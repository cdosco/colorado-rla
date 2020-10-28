/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 1, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.*;

import java.sql.Blob;
import java.time.Instant;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import us.freeandfair.corla.persistence.PersistentEntity;
import us.freeandfair.corla.persistence.ResultConverter;
import us.freeandfair.corla.csv.Result;

/**
 * An uploaded file, kept in persistent storage for archival.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
// note that unlike our other entities, uploaded files are not Serializable
@Entity
@Cacheable(false) // uploaded files are explicitly not cacheable
@Table(name = "uploaded_file",
       indexes = { @Index(name = "idx_uploaded_file_county", columnList = "county_id") })
// this class has many fields that would normally be declared final, but
// cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
public class UploadedFile implements PersistentEntity {
  /**
   * The database ID.
   */
  @Id
  @Column(updatable = false, nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long my_id;
  
  /**
   * The version (for optimistic locking).
   */
  @Version
  private Long my_version;

  /**
   * The timestamp for this ballot manifest info, in milliseconds since the epoch.
   */
  @Column(updatable = false, nullable = false)
  private Instant my_timestamp;

  /**
   * The county that uploaded the file.
   */
  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn  
  private County my_county;
  
  /**
   * The status of the file.
   */
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private FileStatus status;
  
  /**
   * The orignal filename.
   */
  @Column(updatable = false)
  private String my_filename;
  
  /**
   * The computed hash of the file blob.
   */
  @Column(updatable = false, nullable = false)
  private String computed_hash;

  /**
   * The hash submitted with file upload.
   */
  @Column(updatable = false, nullable = false)
  private String submitted_hash;

  /** the parse result **/
  @Column(length = 65535, columnDefinition = "text")
  @Convert(converter = ResultConverter.class)
  private Result result;

  /**
   * The uploaded file. 
   */
  @Lob
  @Column(updatable = false, nullable = false)
  private Blob my_file;
  
  /**
   * The file size.
   */
  @Column(updatable = false, nullable = false)
  private Long my_size;
  
  /**
   * The approximate number of records in the file.
   */
  @Column(updatable = false, nullable = false)
  private Integer my_approximate_record_count;
  
  /**
   * Constructs an empty uploaded file, solely for persistence.
   */
  public UploadedFile() {
    super();
  }
  
  /**
   * Constructs an uploaded file with the specified information.
   * 
   * @param the_timestamp The timestamp.
   * @param the_county The county that uploaded the file.
   * @param the_filename The original filename.
   * @param the_status The file status.
   * @param computed_hash The computed hash of the file blob.
   * @param submitted_hash The hash entered at upload time.
   * @param the_file The file (as a Blob).
   * @param the_size The file size (in bytes).
   * @param the_approximate_record_count The approximate record count.
   */
  public UploadedFile(final Instant the_timestamp,
                      final County the_county,
                      final String the_filename,
                      final FileStatus status,
                      final String computed_hash,
                      final String submitted_hash,
                      final Blob the_file,
                      final Long the_size,
                      final Integer the_approximate_record_count) {
    super();
    my_timestamp = the_timestamp;
    my_county = the_county;
    my_filename = the_filename;
    this.status = status;
    this.computed_hash = computed_hash;
    this.submitted_hash = submitted_hash;
    my_file = the_file;
    my_size = the_size;
    my_approximate_record_count = the_approximate_record_count;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Long id() {
    return my_id;
  }
  
  /**
   * {@inheritDoc}.
   */
  @Override
  public final void setID(final Long the_id) {
    my_id = the_id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long version() {
    return my_version;
  }

  /**
   * @return the timestamp of this file.
   */
  public Instant timestamp() {
    return my_timestamp;
  }
  
  /**
   * @return the county that uploaded this file.
   */
  public County county() {
    return my_county;
  }
  
  /**
   * @return the original filename of this file.
   */
  public String filename() {
    return my_filename;
  }
  
  /**
   * @return the status of this file.
   */
  public FileStatus getStatus() {
    return this.status;
  }
  /**
   * Sets the file status.
   * 
   * @param the_status The new status.
   */
  public void setStatus(final FileStatus status) {
    this.status = status;
  }

  /**
   * Set the parse result
   * @param errorMessage of this file.
   */
  public void setResult(final Result result) {
    this.result = result;
  }

  /**
   * @return the parse result of this file.
   */
  public Result getResult() {
    return this.result;
  }

  /**
   * @return the computed hash of the file blob.
   */
  public String getHash() {
    return this.computed_hash;
  }

  /**
   * @return the computed hash of the file blob.
   */
  public String getSubmittedHash() {
    return this.submitted_hash;
  }

  /**
   * @return the file, as a binary blob.
   */
  public Blob file() {
    return my_file;
  }
  
  /**
   * @return the file size (in bytes).
   */
  public Long size() {
    return my_size;
  }
  
  /**
   * @return the approximate record count.
   */
  public Integer approximateRecordCount() {
    return my_approximate_record_count;
  }
  
  /**
   * @return a String representation of this elector.
   */
  @Override
  public String toString() {
    return "UploadedFile [id=" + my_id +
      ", county=" + my_county +
      ", filename=" + my_filename +
      "]";
  }

  /**
   * Compare this object with another for equivalence.
   * 
   * @param the_other The other object.
   * @return true if the objects are equivalent, false otherwise.
   */
  @Override
  public boolean equals(final Object the_other) {
    boolean result = true;
    if (the_other instanceof UploadedFile) {
      final UploadedFile other_file = (UploadedFile) the_other;
      result &= nullableEquals(other_file.timestamp(), timestamp());
      result &= nullableEquals(other_file.county(), county());
      result &= nullableEquals(other_file.filename(), filename());
      result &= nullableEquals(other_file.getStatus(), getStatus());
      result &= nullableEquals(other_file.getHash(), getHash());
      result &= nullableEquals(other_file.getSubmittedHash(), getSubmittedHash());
      result &= nullableEquals(other_file.size(), size());
      result &= nullableEquals(other_file.approximateRecordCount(), approximateRecordCount());
    } else {
      result = false;
    }
    return result;
  }
  
  /**
   * @return a hash code for this object.
   */
  @Override
  public int hashCode() {
    return nullableHashCode(getHash());
  }
  
  /** the possible statuses **/
  public enum FileStatus {
    HASH_VERIFIED,
    HASH_MISMATCH,
    IMPORTING,
    IMPORTED,
    FAILED
  }

}
