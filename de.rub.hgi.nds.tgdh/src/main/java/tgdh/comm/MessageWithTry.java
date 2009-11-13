/*
 * Created on 2004-9-20
 *
 */
package tgdh.comm;

/**
 * This class implements a mechanism which controls the try times of processing of 
 * the {@link tgdh.comm.TgdhMessage}. The TgdhMessage will be ignored after max. 
 * tries. (This function is in this release desabled).
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class MessageWithTry {
    private TgdhMessage  message;
    private int 	     times2try = 0; 

    /**
      * Constructs a new MessageWithTry with detail TgdhMessage and the max. times
      * to try to process the message
      * @param message    the TgdhMessage
      * @param times2try  max. times to try to process the message
     */
    public MessageWithTry(TgdhMessage message, int times2try) {
        super();
        this.message   = message;
        this.times2try = times2try;
    }   
    
    
    /**
     * Decrements the times to try, then returns the message. 
     * In this releae it ONLY returns the message, but doesn't decrement the times
     * to try
     * @return the detail message.
     */
    public TgdhMessage getMessage() {
        times2try --;
        return message;
    }
    
    /**
     * @return Returns the remained times to try.
     */
    public int getTimes2try() {
        return times2try;
    }
}
