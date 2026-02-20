package org.leapyear;
import org.year.LeapYearCalculator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeapYearCalculatorTest {

  @Test
  void testLeapYear() {
    assertTrue(LeapYearCalculator.isLeapYear(1600));  // 1600 is a leap year
    assertTrue(LeapYearCalculator.isLeapYear(2000));  // 2000 is a leap year
  }

  @Test
  void testNonLeapYear() {
    assertFalse(LeapYearCalculator.isLeapYear(1700)); // 1700 is not a leap year
    assertFalse(LeapYearCalculator.isLeapYear(2017)); // 2017 is not a leap year
  }

  @Test
  void testOutOfRangeYear() {
    assertFalse(LeapYearCalculator.isLeapYear(-1600));// -1600 is out of range
  }
}
