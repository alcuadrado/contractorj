package examples.unannotated;

import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;

public class Signature {

  /**
   * Possible {@link #state} value, signifying that this signature object has not yet been
   * initialized.
   */
  protected static final int UNINITIALIZED = 0;

  /**
   * Possible {@link #state} value, signifying that this signature object has been initialized for
   * signing.
   */
  protected static final int SIGN = 2;

  /**
   * Possible {@link #state} value, signifying that this signature object has been initialized for
   * verification.
   */
  protected static final int VERIFY = 3;

  /** Current state of this signature object. */
  protected int state = UNINITIALIZED;

  /**
   * Initializes this object for verification. If this method is called again with a different
   * argument, it negates the effect of this call.
   *
   * @param publicKey the public key of the identity whose signature is going to be verified.
   * @exception InvalidKeyException if the key is invalid.
   */
  public final void initVerify(String publicKey) throws InvalidKeyException {
    //        engineInitVerify(publicKey);
    //Added check
    if (publicKey == null) {
      throw new InvalidKeyException();
    }

    state = VERIFY;
  }

  /**
   * Initialize this object for signing. If this method is called again with a different argument,
   * it negates the effect of this call.
   *
   * @param privateKey the private key of the identity whose signature is going to be generated.
   * @exception InvalidKeyException if the key is invalid.
   */
  public final void initSign(String privateKey) throws InvalidKeyException {
    // engineInitSign(privateKey);
    //Added check
    if (privateKey == null) {
      throw new InvalidKeyException();
    }

    state = SIGN;
  }

  /**
   * Finishes the signature operation and stores the resulting signature bytes in the provided
   * buffer <code>outbuf</code>, starting at <code>offset</code>. The format of the signature
   * depends on the underlying signature scheme.
   *
   * <p>This signature object is reset to its initial state (the state it was in after a call to one
   * of the <code>initSign</code> methods) and can be reused to generate further signatures with the
   * same private key.
   *
   * @param outbuf buffer for the signature result.
   * @param offset offset into <code>outbuf</code> where the signature is stored.
   * @param len number of bytes within <code>outbuf</code> allotted for the signature.
   * @return the number of bytes placed into <code>outbuf</code>.
   * @exception SignatureException if this signature object is not initialized properly, if this
   *     signature algorithm is unable to process the input data provided, or if <code>len</code> is
   *     less than the actual signature length.
   * @since 1.2
   */
  public final int sign(String outbuf, int offset, int len) throws SignatureException {
    if (outbuf == null) {
      throw new IllegalArgumentException("No output buffer given");
    }
    if (outbuf.length() - offset < len) {
      throw new IllegalArgumentException("Output buffer too small for specified offset and length");
    }
    if (state != SIGN) {
      throw new SignatureException("object not initialized for " + "signing");
    }
    //        return engineSign(outbuf, offset, len);
    return 0;
  }

  /**
   * Verifies the passed-in signature in the specified array of bytes, starting at the specified
   * offset.
   *
   * <p>A call to this method resets this signature object to the state it was in when previously
   * initialized for verification via a call to <code>initVerify(PublicKey)</code>. That is, the
   * object is reset and available to verify another signature from the identity whose public key
   * was specified in the call to <code>initVerify</code>.
   *
   * @param signature the signature bytes to be verified.
   * @param offset the offset to start from in the array of bytes.
   * @param length the number of bytes to use, starting at offset.
   * @return true if the signature was verified, false if not.
   * @exception SignatureException if this signature object is not initialized properly, the
   *     passed-in signature is improperly encoded or of the wrong type, if this signature algorithm
   *     is unable to process the input data provided, etc.
   * @exception IllegalArgumentException if the <code>signature</code> byte array is null, or the
   *     <code>offset</code> or <code>length</code> is less than 0, or the sum of the <code>offset
   *     </code> and <code>length</code> is greater than the length of the <code>signature</code>
   *     byte array.
   * @since 1.4
   */
  public final boolean verify(String signature, int offset, int length) throws SignatureException {
    if (state == VERIFY) {
      if ((signature == null)
          || (offset < 0)
          || (length < 0)
          || (offset + length > signature.length())) {
        throw new IllegalArgumentException("Bad arguments");
      }

      //            return engineVerify(signature, offset, length);
      return false;
    }
    throw new SignatureException("object not initialized for " + "verification");
  }

  /**
   * Updates the data to be signed or verified, using the specified array of bytes, starting at the
   * specified offset.
   *
   * @param data the array of bytes.
   * @param off the offset to start from in the array of bytes.
   * @param len the number of bytes to use, starting at offset.
   * @exception SignatureException if this signature object is not initialized properly.
   */
  public final void update(String data, int off, int len) throws SignatureException {
    if (state == SIGN || state == VERIFY) {
      //            engineUpdate(data, off, len);
    } else {
      throw new SignatureException("object not initialized for " + "signature or verification");
    }
  }
}
