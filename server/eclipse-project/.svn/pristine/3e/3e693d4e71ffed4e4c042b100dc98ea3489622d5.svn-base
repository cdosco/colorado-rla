package us.freeandfair.corla.query;

import java.io.ByteArrayOutputStream;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import static org.testng.Assert.*;

import us.freeandfair.corla.persistence.Persistence;
import us.freeandfair.corla.query.Setup;
import us.freeandfair.corla.query.ExportQueries;

import org.hibernate.Session;
import org.hibernate.query.Query;



@Test(groups = {"integration"})
public class ExportQueriesTest {

  @BeforeTest()
  public void setUp() {
    Setup.setProperties();
    Persistence.beginTransaction();
    insertSeed();
  }

  @AfterTest()
  public void tearDown() {
    try {
    Persistence.rollbackTransaction();
    } catch (Exception e) {
    }
  }


  private void insertSeed() {
    final Session s = Persistence.currentSession();
    String query = "insert into dos_dashboard (id,seed) values (99,'1234');";
    s.createNativeQuery(query).executeUpdate();
  }

  @Test()
  public void jsonRowsTest() {
    String q = "SELECT seed FROM dos_dashboard";
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    ExportQueries.jsonOut(q, os);

    assertEquals(os.toString(), "[{\"seed\":\"1234\"}]");
  }

  @Test()
  public void csvOutTest() {
    String q = "SELECT seed FROM dos_dashboard";
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    ExportQueries.csvOut(q, os);

    assertEquals(os.toString(), "seed\n1234\n");
  }

  @Test()
  public void sqlFilesTest()
    throws java.io.IOException {
    Map<String,String> files = ExportQueries.sqlFiles();
    assertTrue(files.get("seed").contains("SELECT seed FROM dos_dashboard"));
  }
}
