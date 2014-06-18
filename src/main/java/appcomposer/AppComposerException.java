package appcomposer;

/**
 * Generic abstraction exception for those errors hapenning in the AppComposer
 * 
 * @author Pablo Orduña <pablo.orduna@deusto.es>
 */
public class AppComposerException extends Exception {

	private static final long serialVersionUID = 1593064530097100712L;

	public AppComposerException() {
	}

	public AppComposerException(String message) {
		super(message);
	}

	public AppComposerException(Throwable cause) {
		super(cause);
	}

	public AppComposerException(String message, Throwable cause) {
		super(message, cause);
	}
}
