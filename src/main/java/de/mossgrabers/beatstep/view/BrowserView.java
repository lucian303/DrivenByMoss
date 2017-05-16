// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.beatstep.view;

import de.mossgrabers.beatstep.BeatstepConfiguration;
import de.mossgrabers.beatstep.controller.BeatstepColors;
import de.mossgrabers.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.Model;
import de.mossgrabers.framework.controller.grid.PadGrid;
import de.mossgrabers.framework.daw.BrowserProxy;
import de.mossgrabers.framework.view.AbstractView;


/**
 * The Browser view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BrowserView extends AbstractView<BeatstepControlSurface, BeatstepConfiguration> implements BeatstepView
{
    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public BrowserView (final BeatstepControlSurface surface, final Model model)
    {
        super ("Browser", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnob (final int index, final int value)
    {
        final BrowserProxy browser = this.model.getBrowser ();
        if (!browser.isActive ())
            return;

        int column;
        switch (index)
        {
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                column = index - 8;
                if (value > 64)
                {
                    for (int i = 0; i < value - 64; i++)
                        browser.selectNextFilterItem (column);
                }
                else
                {
                    for (int i = 0; i < 64 - value; i++)
                        browser.selectPreviousFilterItem (column);
                }
                break;

            case 15:
                if (value > 64)
                {
                    for (int i = 0; i < value - 64; i++)
                        browser.selectNextResult ();
                }
                else
                {
                    for (int i = 0; i < 64 - value; i++)
                        browser.selectPreviousResult ();
                }
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        switch (note - 36)
        {
            // Cancel
            case 0:
                if (velocity == 0)
                    return;
                this.model.getBrowser ().stopBrowsing (false);
                this.surface.getViewManager ().restoreView ();
                break;

            // OK
            case 7:
                if (velocity == 0)
                    return;
                this.model.getBrowser ().stopBrowsing (true);
                this.surface.getViewManager ().restoreView ();
                break;

            // Notes for preview
            case 2:
                this.surface.sendMidiEvent (0x90, 12, velocity);
                break;
            case 3:
                this.surface.sendMidiEvent (0x90, 24, velocity);
                break;
            case 4:
                this.surface.sendMidiEvent (0x90, 36, velocity);
                break;
            case 5:
                this.surface.sendMidiEvent (0x90, 48, velocity);
                break;
            case 10:
                this.surface.sendMidiEvent (0x90, 60, velocity);
                break;
            case 11:
                this.surface.sendMidiEvent (0x90, 72, velocity);
                break;
            case 12:
                this.surface.sendMidiEvent (0x90, 84, velocity);
                break;
            case 13:
                this.surface.sendMidiEvent (0x90, 96, velocity);
                break;

            // Not used
            default:
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final PadGrid padGrid = this.surface.getPadGrid ();
        padGrid.light (36, BeatstepColors.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (37, BeatstepColors.BEATSTEP_BUTTON_STATE_OFF);
        for (int i = 2; i < 6; i++)
            padGrid.light (36 + i, BeatstepColors.BEATSTEP_BUTTON_STATE_PINK);
        padGrid.light (42, BeatstepColors.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (43, BeatstepColors.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (44, BeatstepColors.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (45, BeatstepColors.BEATSTEP_BUTTON_STATE_OFF);
        for (int i = 10; i < 14; i++)
            padGrid.light (36 + i, BeatstepColors.BEATSTEP_BUTTON_STATE_PINK);
        padGrid.light (50, BeatstepColors.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (51, BeatstepColors.BEATSTEP_BUTTON_STATE_OFF);
    }
}