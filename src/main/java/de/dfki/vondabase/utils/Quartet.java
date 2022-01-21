package de.dfki.vondabase.utils;

public class Quartet<FIRSTTYPE, SECONDTYPE, THIRDTYPE, FOURTHTYPE> {
  public FIRSTTYPE first;
  public SECONDTYPE second;
  public THIRDTYPE third;
  public FOURTHTYPE fourth;

  public Quartet(FIRSTTYPE theFirst, SECONDTYPE theSecond, THIRDTYPE theThird, FOURTHTYPE theFourth) {
    this.first = theFirst;
    this.second = theSecond;
    this.third = theThird;
    this.fourth = theFourth;
  }

  public String toString() {
    return "<" + this.first + "|" + this.second + "|" + this.third +">";
  }
}
