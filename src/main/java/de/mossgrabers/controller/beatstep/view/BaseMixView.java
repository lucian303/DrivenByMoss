// Written by Lucian Hontau - lucianux.com
// (c) 2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.beatstep.view;

import de.mossgrabers.controller.beatstep.BeatstepConfiguration;
import de.mossgrabers.controller.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractView;


/**
 * The base class for all Mix views.
 *
 * @author Lucian Hontau
 */
public abstract class BaseMixView extends AbstractView<BeatstepControlSurface, BeatstepConfiguration> implements BeatstepView
{
    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public BaseMixView(final String name, final BeatstepControlSurface surface, final IModel model)
    {
        super (name, surface, model);
    }

    /**
     * Get the track to change based on the knob index and the currently selected bank
     *
     * TODO: Save last position and restore after callin scrollTo()
     *
     * @param index
     * @return
     */
    protected ITrack getTrackFromBank (final int index)
    {
        final int adjustedIndex;
        final ITrackBank tb = this.model.getTrackBank ();
        final int track = this.model.getCurrentTrackBank ().getScrollPosition ();
        final int bankOf16 = (int) Math.floor((track + 1) / 16) * 16;

        if (index >= 0 && index <= 7) {
            tb.scrollTo(0 + bankOf16, false);
            adjustedIndex = index;
        } else {
            tb.scrollTo(8 + bankOf16, false);
            adjustedIndex = index - 8;
        }

        final ITrack selectedTrack = tb.getItem(adjustedIndex);

        return selectedTrack;
    }

    /**
     * Given a pad node, get the track number it corresponds to (0 - 15)
     *
     * @param note
     */
    protected int getTrackFromPad (final int note)
    {
        // Upper row: 44 - 51
        // Lower row: 36 - 43
        int track = note - 44;
        if (track < 0) {
            track += 16;
        }

        return track;
    }
}