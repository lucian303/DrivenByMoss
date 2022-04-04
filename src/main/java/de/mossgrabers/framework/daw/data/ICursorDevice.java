// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2022
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.framework.daw.data;

import de.mossgrabers.framework.daw.data.bank.IDeviceBank;


/**
 * Interface to the Cursor device.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public interface ICursorDevice extends ISpecificDevice, ICursor
{
    /**
     * Select the parent of the device.
     */
    void selectParent ();


    /**
     * Select the channel which hosts the device.
     */
    void selectChannel ();


    /**
     * Get the device sibling bank.
     *
     * @return The bank
     */
    IDeviceBank getDeviceBank ();


    /**
     * Moves the device to the left in the device chain (swaps it position with the previous
     * device). If there is no device before this, nothing happens.
     */
    void swapWithPrevious ();


    /**
     * Moves the device to the right in the device chain (swaps it position with the following
     * device). If there is no device after this, nothing happens.
     */
    void swapWithNext ();


    /**
     * Get the names of slot chains of a device.
     *
     * @return The names or an empty array
     */
    String [] getSlotChains ();


    /**
     * Select the first device of a slot chain.
     *
     * @param slotChainName One of the slot chain names retrieved with {@link #getSlotChains()}.
     */
    void selectSlotChain (String slotChainName);
}