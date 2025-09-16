package us.freeandfair.corla.controller;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class AuditReportTest {

  @Test
  public void getUniqueNameUsingCountyTest() {
    String countyName = AuditReport.getFirst31Characters("District Race 117th Judicial for Some County");
    assertEquals(countyName, "District Race 117th Judi Some");
  }
  
  @Test
  public void getUniqueNameUsingCountyTest2() {
    String countyName = AuditReport.getFirst31Characters("Alamosa County Clerk and Recorder - DEM");
    assertEquals(countyName, "Alamosa County Clerk Recorder");
  }
  
  @Test
  public void getUniqueNameUsingCountyTest3() {
    String countyName = AuditReport.getFirst31Characters("District Attorney - 11th Judicial District - REP - Chaffee");
    assertEquals(countyName, "District Atrney - 11th - REP -");
  }

  @Test
  public void getUniqueNameUsingCountyTest4() {
    String countyName = AuditReport.getFirst31Characters("District Attorney - 11th Judicial District - REP - Custer County");
    assertEquals(countyName, "District Atrney - 11th Custer");
  }
  
  @Test
  public void getUniqueNameUsingCountyTest5() {
    String countyName = AuditReport.getFirst31Characters("Regent of the University of Colorado - Congressional District 2 - DEM - Clear Creek County");
    assertEquals(countyName, "Regent University Color Creek");
  }
  
  
  @Test
  public void getUniqueNameUsingCountyTest6() {
    String countyName = AuditReport.getFirst31Characters("Representative to the 117th United States Congress-District 3 - DEM - Archuleta County");
    assertEquals(countyName, "Representative 117t Archuleta");
  }

  

  

}
