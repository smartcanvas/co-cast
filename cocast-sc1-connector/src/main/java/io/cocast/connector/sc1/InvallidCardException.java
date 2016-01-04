package io.cocast.connector.sc1;

import io.cocast.connector.sc1.model.Card;

/**
 * Exception raised when the card is not suitable for Co-cast
 */
public class InvallidCardException extends Exception {

    /**
     * Constructor
     */
    public InvallidCardException(Card card, String reason) {
        super("Card with mnemonic " + card.getMnemonic() + " is not suitable for Co-Cast. Reason = " + reason);
    }

    /**
     * Constructor
     */
    public InvallidCardException(String reason) {
        super("Card <unknown> is not suitable for Co-Cast. Reason = " + reason);
    }
}
