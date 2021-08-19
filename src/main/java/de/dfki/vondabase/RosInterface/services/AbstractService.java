package de.dfki.vondabase.RosInterface.services;

import java.util.concurrent.Callable;

public abstract class AbstractService implements Callable<Integer> {

  protected int result;
  protected boolean resolved = false;

  /**
   * This method is called by the corresponding rudi rule when the situation is resolved
   * @param resolved this boolean variable indicates whether the situation was resolved (true positive, false negative)
   */
  public void situationResolved(int resolved) {
    this.resolved = true;
    this.result = resolved;
  }

  public Integer getResult(){
    return this.result;
  }

  public boolean isResolved(){
    return this.resolved;
  }

  /**
   * This method is called as part of the run() Method of the serviceCall.
   * It updates the information state (IS) with the initial changes introduced by the call.
   */
  public abstract void updateIS();


  @Override
  public Integer call(){
    updateIS();
    while (!isResolved()){
      try {
        Thread.sleep(1000);
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }
    return getResult();
  }

}
