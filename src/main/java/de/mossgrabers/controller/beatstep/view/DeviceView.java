// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.beatstep.view;

import de.mossgrabers.controller.beatstep.BeatstepConfiguration;
import de.mossgrabers.controller.beatstep.controller.BeatstepColorManager;
import de.mossgrabers.controller.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.daw.data.ILayer;
import de.mossgrabers.framework.daw.data.bank.IChannelBank;
import de.mossgrabers.framework.daw.data.bank.IParameterPageBank;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.view.Views;

import java.util.Optional;


/**
 * The Device view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class DeviceView extends AbstractView<BeatstepControlSurface, BeatstepConfiguration> implements BeatstepView
{
    private TrackEditing extensions;
    private boolean      isLayer;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public DeviceView (final BeatstepControlSurface surface, final IModel model)
    {
        super ("Device", surface, model);
        this.extensions = new TrackEditing (surface, model);
        this.isLayer = false;
    }


    /** {@inheritDoc} */
    @Override
    public void onKnob (final int index, final int value, final boolean isTurnedRight)
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        if (index < 8 || index == 16)
        {
            this.extensions.onTrackKnob (index, value, isTurnedRight);
            return;
        }

        cd.getParameterBank ().getItem (index - 8).changeValue (value);
        this.surface.getDisplay ().notify ("Param: " + cd.getParameterBank ().getItem (index - 8).getName () + " (" + (isTurnedRight ? "up" : "down") + ")");
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final ICursorDevice cd = this.model.getCursorDevice ();
        final IChannelBank<ILayer> bank = cd.getLayerBank ();
        final Optional<ILayer> sel = bank.getSelectedItem ();

        switch (note - 36)
        {
            // Toggle device on/off
            case 0:
                cd.toggleEnabledState ();
                break;

            // Device Left
            case 1:
                if (this.isLayer)
                {
                    int index = 0;
                    if (sel.isPresent ())
                    {
                        final int idx = sel.get ().getIndex ();
                        index = idx - 1;
                    }
                    if (index >= 0)
                        bank.getItem (index).select ();
                }
                else
                    cd.selectPrevious ();

                this.surface.scheduleTask (() -> this.surface.getDisplay ().notify ("Previous Device: " + this.model.getCursorDevice ().getName ()), 150);

                break;

            // Device Right
            case 2:
                if (this.isLayer)
                {
                    final int index = sel.isEmpty () ? 0 : sel.get ().getIndex () + 1;
                    bank.getItem (index > 7 ? 7 : index).select ();
                }
                else
                    cd.selectNext ();

                this.surface.scheduleTask (() -> this.surface.getDisplay ().notify ("Next Device: " + this.model.getCursorDevice ().getName ()), 150);

                break;

            // Enter layer
            case 3:
                // If no layer, show/hide the remote control param page
                if (!cd.hasLayers ()) {
                    this.surface.getDisplay ().notify ("Show Params");
                    cd.toggleParameterPageSectionVisible ();

                    return;
                }

                if (this.isLayer)
                {
                    this.surface.getDisplay ().notify ("Into Layer");
                    if (sel.isPresent ())
                        sel.get ().enter ();
                }
                else if (sel.isEmpty ())
                    bank.getItem (0).select ();

                this.isLayer = !this.isLayer;
                this.surface.scheduleTask (() -> this.surface.getDisplay ().notify ("Device: " + this.model.getCursorDevice ().getName ()), 150);

                break;

            // Exit layer
            case 4:
                // Toggle external VST window if not in layer
                if (!this.isLayer && !cd.isNested ()) {
                    this.surface.getDisplay ().notify ("Device Details");
                    cd.toggleWindowOpen ();

                    return;
                }

                if (this.isLayer) {
                    this.surface.getDisplay ().notify ("Out of Layer");
                    this.isLayer = false;
                } else {
                    if (cd.isNested ())
                    {
                        cd.selectParent ();
                        cd.selectChannel ();
                        this.isLayer = true;
                    }
                }

                this.surface.scheduleTask (() -> this.surface.getDisplay ().notify ("Device: " + this.model.getCursorDevice ().getName ()), 150);

                break;

            // Open browser / create device
            case 5:
                if (this.model.hasSelectedDevice ()) {
                    this.surface.getDisplay ().notify ("Replace Device");
                    this.model.getBrowser ().replace (cd);
                }
                else {
                    this.surface.getDisplay ().notify ("Add after");
                    this.model.getBrowser ().insertAfterCursorDevice ();
                }
                break;

            // Param bank down
            case 6:
                this.surface.getDisplay ().notify ("Previous Param Bank");
                cd.getParameterBank ().scrollBackwards ();
                break;

            // Param bank page up
            case 7:
                this.surface.getDisplay ().notify ("Next Param Bank");
                cd.getParameterBank ().scrollForwards ();
                break;

            default:
                final int paramBank = note - 36 - 8;
                this.surface.getDisplay ().notify ("Param Bank: " + (paramBank + 1));
                cd.getParameterPageBank ().selectPage (paramBank);
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final ICursorDevice cd = this.model.getCursorDevice ();
        final IParameterPageBank parameterPageBank = cd.getParameterPageBank ();
        final int selectedItemIndex = parameterPageBank.getSelectedItemIndex ();
        final IPadGrid padGrid = this.surface.getPadGrid ();
        for (int i = 0; i < parameterPageBank.getPageSize (); i++)
            padGrid.light (44 + i, i == selectedItemIndex ? BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE : BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (36, cd.isEnabled () ? BeatstepColorManager.BEATSTEP_BUTTON_STATE_RED : BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (37, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (38, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (39, BeatstepColorManager.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (40, BeatstepColorManager.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (41, BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK);
        padGrid.light (42, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (43, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
    }
}