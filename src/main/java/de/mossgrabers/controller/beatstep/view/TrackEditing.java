// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.beatstep.view;

import de.mossgrabers.controller.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.view.Views;

import java.util.Optional;


/**
 * Plugable extension to edit track parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackEditing
{
    private BeatstepControlSurface surface;
    private IModel                 model;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public TrackEditing (final BeatstepControlSurface surface, final IModel model)
    {
        this.surface = surface;
        this.model = model;
    }


    /**
     * A knob is moved for changing a track parameter.
     *
     * @param index The index of the knob
     * @param value The knobs value
     * @param isTurnedRight The knob is turned to the right (positive value change)
     */
    public void onTrackKnob (final int index, final int value, final boolean isTurnedRight)
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final Optional<ITrack> selectedTrack = tb.getSelectedItem ();
        if (selectedTrack.isEmpty ())
            return;

        switch (index)
        {
            case 0:
                selectedTrack.get ().changeVolume (value);
                break;
            case 1:
                selectedTrack.get ().changePan (value);
                break;

            case 2:
                selectedTrack.get ().setMute (isTurnedRight);
                break;

            case 3:
                selectedTrack.get ().setSolo (isTurnedRight);
                break;

            case 4:
                selectedTrack.get ().setRecArm (isTurnedRight);
                break;

            case 5:
                this.model.getTransport ().changeTempo (isTurnedRight, this.surface.isShiftPressed ());
                break;

            case 6:
                this.model.getTransport ().changePosition (isTurnedRight, this.surface.isShiftPressed ());
                break;

            case 7:
                this.surface.getViewManager ().setActive (Views.DEVICE);
                final ICursorDevice cd = this.model.getCursorDevice ();
                if (this.model.hasSelectedDevice ())
                    this.model.getBrowser ().replace (cd);
                else
                    this.model.getBrowser ().insertAfterCursorDevice ();
                break;

            // Send 1 - 4
            case 8:
            case 9:
            case 10:
            case 11:
                if (!this.model.isEffectTrackBankActive ())
                    selectedTrack.get ().getSendBank ().getItem (index - 8).changeValue (value);
                break;

            default:
                // Not used
                break;
        }
    }
}