package edu.ub.pis2324.projecte.presentation.utils.error_handling;

/**
 * Extension of Throwable to represent the errors thrown by our application.
 */
public class XopingThrowable extends Throwable {
    private XopingError xError;

    /**
     * Constructor.
     * @param xError the type of error.
     */
    public XopingThrowable(XopingError xError) {
        this.xError = xError;
    }

    /**
     * Gets the type of error.
     * @return the type of error.
     */
    public XopingError getError() {
        return xError;
    }
}
