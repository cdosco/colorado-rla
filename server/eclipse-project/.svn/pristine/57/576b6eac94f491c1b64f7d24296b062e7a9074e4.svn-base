package us.freeandfair.corla.endpoint;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import us.freeandfair.corla.asm.CountyDashboardASM;
import us.freeandfair.corla.model.County;
import us.freeandfair.corla.model.CountyDashboard;
import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.Setup;

@Test(groups = {"integration"})
public class StartAuditRoundTest {

  private StartAuditRoundTest() {};


  @BeforeTest()
  public void setUp() {
    Setup.setProperties();
    Persistence.beginTransaction();
  }

  @AfterTest()
  public void tearDown() {
    try {
      Persistence.rollbackTransaction();
    } catch (Exception e) {
    }
  }

  // this test doesn't do much yet
  @Test()
  public void testReadyToStartFalse() {
    StartAuditRound sar = new StartAuditRound();
    County county = new County("c1", 1L);
    CountyDashboard cdb = new CountyDashboard(county);
    CountyDashboardASM cdbAsm = new CountyDashboardASM(cdb.id().toString());

    assertEquals(false, (boolean)sar.isReadyToStartAudit(cdb));
  }

}
