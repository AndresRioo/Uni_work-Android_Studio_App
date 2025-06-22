package edu.ub.pis2324.projecte.presentation.utils.error_handling;

import java.util.HashMap;
import java.util.Map;

public class XopingThrowableMapper {
  private final Map<XopingError, XopingError> xErrorMap;

  public XopingThrowableMapper() {
    xErrorMap = new HashMap<>();
  }

  public void add(XopingError xError, XopingError xMappedError) {
    xErrorMap.put(xError, xMappedError);
  }

  /**
   * Applies the mapping if Throwable is a XopingThrowable, otherwise returns the Throwable as is
   * @param throwable the Throwable to map
   * @return the mapped Throwable
   */
  public Throwable map(Throwable throwable) {
    return (throwable instanceof XopingThrowable)
        ? map((XopingThrowable) throwable)
        : throwable;
  }

  /**
   * Maps the XopingThrowable to a XopingThrowable with its XopingError mapped to another XopingError
   * @param xThrowable the XopingThrowable to map
   * @return the mapped XopingThrowable
   */
  public XopingThrowable map(XopingThrowable xThrowable) {
    XopingError error = xThrowable.getError();
    return (xErrorMap.containsKey(error))
        ? new XopingThrowable(xErrorMap.get(error))
        : xThrowable;
  }
}
