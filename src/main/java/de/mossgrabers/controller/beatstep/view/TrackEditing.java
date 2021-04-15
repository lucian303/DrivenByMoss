// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.beatstep.view;

import de.mossgrabers.controller.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.view.Views;

import java.util.Optional;


/**
 * Plugable extension to edit track parameters.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackEditing extends AbstractView
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
        super("Track Editing", surface, model);
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
                this.surface.getDisplay ().notify ("Volume " + (isTurnedRight ? "up" : "down"));
                selectedTrack.get ().changeVolume (value);
                break;
            case 1:
                this.surface.getDisplay ().notify ("Pan " + (isTurnedRight ? "up" : "down"));
                selectedTrack.get ().changePan (value);
                break;

            case 2:
                this.surface.getDisplay ().notify ((isTurnedRight ? "Mute" : "Unmute"));
                selectedTrack.get ().setMute (isTurnedRight);
                break;

            case 3:
                this.surface.getDisplay ().notify ((isTurnedRight ? "Solo" : "Solo off"));
                selectedTrack.get ().setSolo (isTurnedRight);
                break;

            case 4:
                this.surface.getDisplay ().notify ((isTurnedRight ? "Armed" : "Unarmed"));
                selectedTrack.get ().setRecArm (isTurnedRight);
                break;

            case 5:
                this.surface.getDisplay ().notify ("Tempo " + (isTurnedRight ? "up" : "down"));
                this.model.getTransport ().changeTempo (isTurnedRight, this.surface.isShiftPressed ());
                break;

            case 6:
                this.surface.getDisplay ().notify ("Move " + (isTurnedRight ? "right" : "left"));
                this.model.getTransport ().changePosition (isTurnedRight, this.surface.isShiftPressed ());
                break;

            case 7:
                this.surface.getViewManager ().setActive (Views.DEVICE);
                final ICursorDevice cd = this.model.getCursorDevice ();
                if (this.model.hasSelectedDevice ()) {
                    this.surface.getDisplay ().notify ("Replace device");
                    this.model.getBrowser ().replace (cd);
                }
                else {
                    this.surface.getDisplay ().notify ("Add device after");
                    this.model.getBrowser ().insertAfterCursorDevice ();
                }
                break;

            // Send 1 - 4
            case 8:
            case 9:
            case 10:
            case 11:
                if (!this.model.isEffectTrackBankActive ()) {
                    this.surface.getDisplay ().notify ("Send: " + (index - 8) + (isTurnedRight ? " up" : " down"));
                    selectedTrack.get ().getSendBank ().getItem (index - 8).changeValue (value);
                }
                break;

            // Big knob
            case 16:
                if (isTurnedRight) {
                    int knobIndex2 = selectedTrack.get ().getIndex () + 1;
                    if (knobIndex2 >= 8 || this.surface.isShiftPressed ()) {
                        tb.selectNextPage ();
                        this.surface.scheduleTask (() -> this.surface.getDisplay ().notify ("Next Bank: " + this.getBank ()), 150);
                    } else
                        this.selectTrack (knobIndex2);
                } else {
                    int knobIndex = selectedTrack.get ().getIndex () - 1;
                    if (knobIndex < 0 || this.surface.isShiftPressed ()) {
                        tb.selectPreviousPage ();
                        this.surface.scheduleTask (() -> this.surface.getDisplay ().notify ("Previous Bank: " + this.getBank ()), 150);
                    }
                    else
                        this.selectTrack (knobIndex);
                }

                break;

            default:
                // Not used
                break;
        }
    }

    @Override
    public void drawGrid () {
    }

    @Override
    public void onGridNote (int note, int velocity) {
    }
}