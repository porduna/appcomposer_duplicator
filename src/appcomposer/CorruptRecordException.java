package appcomposer;

/**
 * Exception which happens when there is something wrong in the format
 * of the stored data
 * 
 * @author Pablo Orduña <pablo.orduna@deusto.es>
 */
public class CorruptRecordException extends AppComposerException {

	private static final long serialVersionUID = 650436636441366710L;

	public CorruptRecordException() {
	}

	public CorruptRecordException(String message) {
		super(message);
	}

	public CorruptRecordException(Throwable cause) {
		super(cause);
	}

	public CorruptRecordException(String message, Throwable cause) {
		super(message, cause);
	}
}
