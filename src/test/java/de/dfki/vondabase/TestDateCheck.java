package de.dfki.vondabase;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;

public class TestDateCheck {

  @Test
  public void test() {
    ULocale locale = new ULocale("de_DE");
    Calendar calendar = Calendar.getInstance(locale);

    // name of month
    SimpleDateFormat df2 = new SimpleDateFormat (SimpleDateFormat.MONTH, locale);
    String monthname = df2.format(calendar.getTime());
    //System.out.println(monthname);
    assertNotNull(monthname);

    // name of weekday
    SimpleDateFormat df3 = new SimpleDateFormat (SimpleDateFormat.WEEKDAY, locale);
    String dayname = df3.format(calendar.getTime());
    //System.out.println(dayname);
    assertNotNull(dayname);
  }

}
