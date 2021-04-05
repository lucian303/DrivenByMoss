// Written by Lucian Hontau - lucianux.com
// (c) 2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.beatstep.view;

import de.mossgrabers.controller.beatstep.controller.BeatstepColorManager;
import de.mossgrabers.controller.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.data.bank.ITrackBank;


/**
 * The Mix view.
 *
 * @author Lucian Hontau
 */
public class MixView extends BaseMixView implements BeatstepView {
    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model   The model
     */
    public MixView(final BeatstepControlSurface surface, final IModel model) {
        super("Mix", surface, model);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onKnob(final int index, final int value, final boolean isTurnedRight) {
        ITrack selectedTrack = this.getTrackFromBank(index);
        if (selectedTrack != null)
            selectedTrack.changeVolume(value);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onGridNote(final int note, final int velocity) {
        if (velocity == 0)
            return;

        final int track = this.getTrackFromPad(note);
        ITrack selectedTrack = this.getTrackFromBank(track);
        if (selectedTrack != null)
            selectedTrack.toggleMute();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void drawGrid() {
        final ITrackBank tb = this.model.getCurrentTrackBank();
        final IPadGrid padGrid = this.surface.getPadGrid();
        padGrid.light(36, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light(37, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light(38, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light(39, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light(40, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light(41, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light(42, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light(43, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light(44, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
    }
}