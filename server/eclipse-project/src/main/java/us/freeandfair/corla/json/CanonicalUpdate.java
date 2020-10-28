package us.freeandfair.corla.json;

import java.util.List;

/** json deserializer for SetContestNames **/
public class CanonicalUpdate {

  /** the contest db id **/
  public String contestId;

  /** the new name **/
  public String name;

  /** not needed, may be removed **/
  public Long countyId;

  /** list of choice changes **/
  public List<ChoiceChange> choices;

  /** json deserializer for SetContestNames **/
  public class ChoiceChange {

    /** aka current name **/
    public String oldName;

    /** the new name to change to **/
    public String newName;
  }
}
