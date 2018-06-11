/*
 * Free & Fair Colorado RLA System
 * 
 * @title ColoradoRLA
 * @created Aug 2, 2017
 * @copyright 2017 Colorado Department of State
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 * @creator Daniel M. Zimmerman <dmz@galois.com>
 * @description A system to assist in conducting statewide risk-limiting audits.
 */

package us.freeandfair.corla.model;

import static us.freeandfair.corla.util.EqualsHashcodeHelper.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.google.gson.annotations.JsonAdapter;

import us.freeandfair.corla.json.CVRContestInfoJsonAdapter;
import us.freeandfair.corla.persistence.StringListConverter;

/**
 * A cast vote record contains information about a single ballot, either 
 * imported from a tabulator export file or generated by auditors.
 * 
 * @author Daniel M. Zimmerman <dmz@freeandfair.us>
 * @version 1.0.0
 */
@Embeddable
//this class has many fields that would normally be declared final, but
//cannot be for compatibility with Hibernate and JPA.
@SuppressWarnings("PMD.ImmutableField")
@JsonAdapter(CVRContestInfoJsonAdapter.class)
public class CVRContestInfo implements Serializable {
  /**
   * The serialVersionUID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The contest in this record.
   */
  @ManyToOne(optional = false, fetch = FetchType.EAGER)
  private Contest my_contest;
  
  /** 
   * The comment for this contest.
   */
  @Column(updatable = false)
  private String my_comment;
  
  /**
   * The consensus value for this contest
   */
  @Column(updatable = false)
  @Enumerated(EnumType.STRING)
  private ConsensusValue my_consensus;
  
  /**
   * The choices for this contest.
   */
  @Column(name = "choices", columnDefinition = "text")
  @Convert(converter = StringListConverter.class)
  private List<String> my_choices = new ArrayList<>();

  /**
   * Constructs an empty CVRContestInfo, solely for persistence.
   */
  public CVRContestInfo() {
    super();
  }
  
  /**
   * Constructs a CVR contest information record with the specified 
   * parameters.
   * 
   * @param the_contest The contest.
   * @param the_comment The comment.
   * @param the_consensus The consensus value.
   * @param the_choices The choices.
   * @exception IllegalArgumentException if any choice is not a valid choice
   * for the specified contest.
   */
  public CVRContestInfo(final Contest the_contest, final String the_comment,
                        final ConsensusValue the_consensus,
                        final List<String> the_choices) {
    super();
    my_contest = the_contest;
    my_comment = the_comment;
    my_consensus = the_consensus;
    my_choices.addAll(the_choices);
    for (final String s : my_choices) {
      if (!my_contest.isValidChoice(s)) {
        throw new IllegalArgumentException("invalid choice " + s + 
                                           " for contest " + my_contest);
      }
    }
  }
  
  /**
   * @return the contest in this record.
   */
  public Contest contest() {
    return my_contest;
  }
  
  /**
   * @return the comment in this record.
   */
  public String comment() {
    return my_comment;
  }
  
  /**
   * @return the consensus flag in this record.
   */
  public ConsensusValue consensus() {
    return my_consensus;
  }
  
  /**
   * @return the choices in this record.
   */
  public List<String> choices() {
    return Collections.unmodifiableList(my_choices);
  }
  
  /**
   * @return a String representation of this cast vote record.
   */
  @Override
  public String toString() {
    return "CVRContestInfo [contest=" + my_contest.id() + ", comment=" + 
           my_comment + ", consensus=" + my_consensus + ", choices=" +
           my_choices + "]";
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
    if (the_other instanceof CVRContestInfo) {
      final CVRContestInfo other_info = (CVRContestInfo) the_other;
      result &= nullableEquals(other_info.contest(), contest());
      result &= nullableEquals(other_info.comment(), comment());
      result &= nullableEquals(other_info.consensus(), consensus());
      result &= nullableEquals(other_info.choices(), choices());
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
    return nullableHashCode(choices());
  }

  /**
   * The possible values for consensus.
   */
  public enum ConsensusValue {
    YES,
    NO
  }
}