// Written by Lucian Hontau - lucianux.com
// (c) 2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.arturia.beatstep.view;

import de.mossgrabers.controller.arturia.beatstep.controller.BeatstepColorManager;
import de.mossgrabers.controller.arturia.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;


/**
 * The Mix view.
 *
 * @author Lucian Hontau
 */
public class PanView extends BaseMixView implements BeatstepView {
    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model   The model
     */
    public PanView (final BeatstepControlSurface surface, final IModel model) {
        super ("Pan", surface, model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onKnob (final int index, final int value, final boolean isTurnedRight) {
        final ITrack selectedTrack = this.getTrackFromBank (index);
        if (selectedTrack != null) {
            int pan = selectedTrack.getPan ();
            if (isTurnedRight) {
                pan += 1;
            } else {
                pan -= 1;
            }

            this.surface.getDisplay ().notify ("Pan " + (selectedTrack.getPosition () + 1) + (isTurnedRight ? " right" : " left"));
            selectedTrack.setPan (pan);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onGridNote (final int note, final int velocity) {
        if (velocity == 0)
            return;

        final int track = this.getTrackFromPadNote (note);
        final ITrack selectedTrack = this.getTrackFromBank (track);
        if (selectedTrack != null) {
            this.surface.getDisplay ().notify ("Solo " + (selectedTrack.getPosition () + 1));
            selectedTrack.toggleSolo ();
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void drawGrid () {
        final IPadGrid padGrid = this.surface.getPadGrid ();

        for (int i = 36; i <= 51; i++) {
            // Disable as trying to show the correct light doesn't work
            padGrid.light (i, BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        }
    }
}