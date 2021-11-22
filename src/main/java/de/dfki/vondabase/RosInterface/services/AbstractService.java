package de.dfki.vondabase.RosInterface.services;

import java.util.concurrent.Callable;

public abstract class AbstractService implements Callable<GCS> {

  protected GCS result;
  protected boolean resolved = false;

  /**
   * This method is called by the corresponding rudi rule when the situation is resolved
   */
  public void situationResolved(int eyes, int awareness, int motorics) {
    this.resolved = true;
    this.result = new GCS(eyes, awareness, motorics);
  }

  public GCS getResult(){
    return this.result;
  }

  public boolean isResolved(){
    return this.resolved;
  }

  /**
   * This method is called as part of the run() Method of the serviceCall.
   * It updates the information state (IS) with the initial changes introduced by the call.
   */
  public abstract void updateIS() throws  ServiceException;


  @Override
  public GCS call() throws ServiceException{
    try{
    updateIS();
    while (!isResolved()){
      try {
        Thread.sleep(1000);
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }
    return getResult();
    } catch (IllegalStateException e){
      return null;
    }
  }

}
