package de.dfki.vondabase.utils;

public class Quartet<FIRSTTYPE, SECONDTYPE, THIRDTYPE, FOURTHTYPE> {
  private FIRSTTYPE first;
  private SECONDTYPE second;
  private THIRDTYPE third;
  private FOURTHTYPE fourth;

  public Quartet(FIRSTTYPE theFirst, SECONDTYPE theSecond, THIRDTYPE theThird, FOURTHTYPE theFourth) {
    this.first = theFirst;
    this.second = theSecond;
    this.third = theThird;
    this.fourth = theFourth;
  }

  public FIRSTTYPE getFirst(){
    return this.first;
  }

  public SECONDTYPE getSecond(){
    return this.second;
  }

  public THIRDTYPE getThird(){
    return this.third;
  }

  public FOURTHTYPE getFourth(){
    return this.fourth;
  }

  public String toString() {
    return "<" + this.first + "|" + this.second + "|" + this.third + "|" + this.fourth + ">";
  }
}
