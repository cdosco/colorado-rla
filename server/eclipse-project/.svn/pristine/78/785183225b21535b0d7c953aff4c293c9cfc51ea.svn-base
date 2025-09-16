package us.freeandfair.corla.util;

import static org.testng.Assert.*;
import org.testng.annotations.*;
import java.util.Comparator;

public class NaturalOrderComparatorTest {
  private Comparator c;

  @BeforeClass()
  public void NaturalOrderComparatorTest() {
      this.c = NaturalOrderComparator.INSTANCE;
  }

  @Test()
  public void compareTest() {
      assertEquals(c.compare("Batch 1", "Batch 11"), -1);
      assertEquals(c.compare("Batch 1", "batch 1"), 0);
      assertEquals(c.compare("Batch 11", "Batch 2"), 1);
      assertEquals(c.compare("Batch 20", "batch 19"), 1);
      assertEquals(c.compare("Batch 123", "Batch 123.a"), -1);
      assertEquals(c.compare("Batch A", "Batch b"), 1);
  }


  @Test()
  public void compareDatesTest() {
    assertEquals(c.compare("Feb 12, 2019, 10:22 am", "Feb 12, 2019, 10:22 am"), 0, "match");
    assertEquals(c.compare("Feb 12, 2019, 10:22 am", "Feb 12, 2019, 10:23 am"), -1, "1 minute later");
    assertEquals(c.compare("Feb 13, 2019, 10:22 am", "Feb 12, 2019, 10:23 am"), 1, "1 day earlier");
    assertEquals(c.compare("Feb 13, 2019, 10:22 am", "Feb 12, 2019, 10:23 pm"), 1, "am before pm");
    assertEquals(c.compare("Feb 13, 2019, 10:22 am", "Aug 12, 2019, 10:22 pm"), -1,
                 " Aug before Feb because it is a string comparison not a date comparison");
  }


}
