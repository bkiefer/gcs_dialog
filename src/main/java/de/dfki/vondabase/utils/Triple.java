package de.dfki.vondabase.utils;

public class Triple<FIRSTTYPE, SECONDTYPE, THIRDTYPE> {
  public FIRSTTYPE first;
  public SECONDTYPE second;
  public THIRDTYPE third;

  public Triple(FIRSTTYPE theFirst, SECONDTYPE theSecond, THIRDTYPE theThird) {
    this.first = theFirst;
    this.second = theSecond;
    this.third = theThird;
  }

  public String toString() {
    return "<" + this.first + "|" + this.second + "|" + this.third +">";
  }
}
