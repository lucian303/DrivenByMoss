// Written by Lucian Hontau - lucianux.com
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.beatstep.view;

import de.mossgrabers.controller.beatstep.BeatstepConfiguration;
import de.mossgrabers.controller.beatstep.controller.BeatstepColorManager;
import de.mossgrabers.controller.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.constants.DeviceID;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.view.Views;

import java.util.Optional;


/**
 * The track view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class MixView extends AbstractView<BeatstepControlSurface, BeatstepConfiguration> implements BeatstepView
{
    private TrackEditing extensions;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public MixView(final BeatstepControlSurface surface, final IModel model)
    {
        super ("Mix", surface, model);
        this.extensions = new TrackEditing (surface, model);
    }


    /** {@inheritDoc} */
    @Override
    public void onKnob (final int index, final int value, final boolean isTurnedRight)
    {
        ITrack selectedTrack = this.getTrackFromBank (index);
        if (selectedTrack != null)
            selectedTrack.changeVolume(value);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        final int track = this.getTrackFromPad (note);
        ITrack selectedTrack = this.getTrackFromBank (track);
        if (selectedTrack != null)
            selectedTrack.toggleMute();
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final ITrackBank tb = this.model.getCurrentTrackBank ();
        final IPadGrid padGrid = this.surface.getPadGrid ();
        padGrid.light (36, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (37, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (38, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (39, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (40, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (41, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (42, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (43, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (44, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
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