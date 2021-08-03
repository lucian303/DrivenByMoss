// Written by Lucian Hontau - lucianux.com
// (c) 2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.arturia.beatstep.view;

import de.mossgrabers.controller.arturia.beatstep.BeatstepConfiguration;
import de.mossgrabers.controller.arturia.beatstep.controller.BeatstepControlSurface;
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
     * TODO: Save last position and restore after calling scrollTo()
     *
     * @param index Knob index
     * @return Track or null
     */
    protected ITrack getTrackFromBank (final int index)
    {
        try {
            final int adjustedIndex;
            final int offset;

            final ITrackBank tb = this.model.getTrackBank ();
            final int track = this.model.getCurrentTrackBank ().getScrollPosition ();
            final int bankOf16 = (int) Math.floor((float) (track + 1) / 16) * 16;

            if (index >= 0 && index <= 7) {
                offset = 0;
                adjustedIndex = index;
            } else {
                offset = 8;
                adjustedIndex = index - 8;
            }

            final int position = offset + bankOf16;
            if (position < 0 || position > tb.getItemCount()) {
                return null;
            }

            tb.scrollTo(position, false);

            return tb.getItem(adjustedIndex);
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Given a pad node, get the track number it corresponds to (0 - 15)
     *
     * @param note Pad note played
     */
    protected int getTrackFromPadNote(final int note)
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