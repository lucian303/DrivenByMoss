// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.beatstep.view;

import de.mossgrabers.controller.beatstep.BeatstepConfiguration;
import de.mossgrabers.controller.beatstep.controller.BeatstepColorManager;
import de.mossgrabers.controller.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IBrowser;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.featuregroup.AbstractView;


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
    public BrowserView (final BeatstepControlSurface surface, final IModel model)
    {
        super ("Browser", surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnob (final int index, final int value, final boolean isTurnedRight)
    {
        final IBrowser browser = this.model.getBrowser ();
        if (!browser.isActive ())
            return;

        final int steps = Math.abs (this.model.getValueChanger ().calcSteppedKnobChange (value));

        // The "All Devices" column in BW is #7 but shows up as the 4th onscreen
        final int[] cols = {0, 1, 7, 3, 4, 5, 6};

        int column;
        switch (index)
        {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                column = cols[index];
                if (isTurnedRight)
                {
                    for (int i = 0; i < steps; i++)
                        browser.selectNextFilterItem (column);
                }
                else
                {
                    for (int i = 0; i < steps; i++)
                        browser.selectPreviousFilterItem (column);
                }
                break;

            case 7:
                if (isTurnedRight)
                {
                    for (int i = 0; i < steps; i++)
                        browser.selectNextResult ();
                }
                else
                {
                    for (int i = 0; i < steps; i++)
                        browser.selectPreviousResult ();
                }
                break;

            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                final ICursorDevice cd = this.model.getCursorDevice ();
                cd.getParameterBank ().getItem (index - 8).changeValue (value);
                break;

            default:
                // Not used
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final IBrowser browser = this.model.getBrowser ();
        if (!browser.isActive ())
            return;

        switch (note - 36)
        {
            // Cancel
            case 0:
                browser.stopBrowsing (false);
                this.surface.getViewManager ().restore ();
                this.surface.getDisplay ().notify ("Cancel");
                break;

            // OK
            case 7:
                browser.stopBrowsing (true);
                this.surface.getViewManager ().restore ();
                this.surface.getDisplay ().notify ("OK!");
                break;

            // Tabs
            case 1:
                this.surface.getDisplay ().notify ("Previous Tab");
                browser.previousContentType();
                break;
            case 2:
                this.surface.getDisplay ().notify ("Next Tab");
                browser.nextContentType();
                break;

            // Next / Previous Patch
            case 5:
                this.surface.getDisplay ().notify ("Previous");
                browser.selectPreviousResult();
                break;
            case 6:
                this.surface.getDisplay ().notify ("Next");
                browser.selectNextResult();
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
        final IPadGrid padGrid = this.surface.getPadGrid ();
        padGrid.light (36, BeatstepColorManager.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (37, BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        for (int i = 2; i < 6; i++)
            padGrid.light (36 + i, BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK);
        padGrid.light (42, BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (43, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (44, BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (45, BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        for (int i = 10; i < 14; i++)
            padGrid.light (36 + i, BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK);
        padGrid.light (50, BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (51, BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
    }
}