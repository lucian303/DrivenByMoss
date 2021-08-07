// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2021
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.controller.arturia.beatstep.view;

import de.mossgrabers.controller.arturia.beatstep.BeatstepConfiguration;
import de.mossgrabers.controller.arturia.beatstep.controller.BeatstepColorManager;
import de.mossgrabers.controller.arturia.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.framework.command.trigger.transport.PlayCommand;
import de.mossgrabers.framework.controller.grid.IPadGrid;
import de.mossgrabers.framework.daw.IApplication;
import de.mossgrabers.framework.daw.IModel;
import de.mossgrabers.framework.daw.ITransport;
import de.mossgrabers.framework.daw.data.ICursorDevice;
import de.mossgrabers.framework.featuregroup.AbstractView;
import de.mossgrabers.framework.featuregroup.IView;
import de.mossgrabers.framework.featuregroup.ViewManager;
import de.mossgrabers.framework.utils.ButtonEvent;
import de.mossgrabers.framework.view.Views;


/**
 * The Shift view.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class ShiftView extends AbstractView<BeatstepControlSurface, BeatstepConfiguration> implements BeatstepView
{
    private static final Views []                                      VIEWS =
    {
        Views.TRACK,
        Views.DEVICE,
        Views.PLAY,
        Views.DRUM,
        Views.SEQUENCER,
        Views.SESSION,
        Views.MIX,
        Views.TRACK_PAN
    };

    private PlayCommand<BeatstepControlSurface, BeatstepConfiguration> playCommand;


    /**
     * Constructor.
     *
     * @param surface The controller
     * @param model The model
     */
    public ShiftView (final BeatstepControlSurface surface, final IModel model)
    {
        super ("Shift", surface, model);
        this.playCommand = new PlayCommand<> (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void drawGrid ()
    {
        final ITransport t = this.model.getTransport ();
        final IPadGrid padGrid = this.surface.getPadGrid ();
        padGrid.light (36, t.isPlaying () ? BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK : BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (37, t.isRecording () ? BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK : BeatstepColorManager.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (38, t.isLoop () ? BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK : BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (39, t.isMetronomeOn () ? BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK : BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (40, BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (41, BeatstepColorManager.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (42, BeatstepColorManager.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (43, BeatstepColorManager.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (44, BeatstepColorManager.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (45, BeatstepColorManager.BEATSTEP_BUTTON_STATE_RED);
        padGrid.light (46, BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK);
        padGrid.light (47, BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK);
        padGrid.light (48, BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK);
        padGrid.light (49, BeatstepColorManager.BEATSTEP_BUTTON_STATE_BLUE);
        padGrid.light (50, BeatstepColorManager.BEATSTEP_BUTTON_STATE_OFF);
        padGrid.light (51, BeatstepColorManager.BEATSTEP_BUTTON_STATE_PINK);
    }


    /** {@inheritDoc} */
    @Override
    public void onGridNote (final int note, final int velocity)
    {
        if (velocity == 0)
            return;

        int viewIndex;
        IView view;
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        switch (note - 36)
        {
            // Play
            case 0:
                this.surface.getDisplay ().notify ("Play");
                this.playCommand.executeNormal (ButtonEvent.UP);
                break;

            // Record
            case 1:
                this.surface.getDisplay ().notify ("Record");
                this.model.getTransport ().record ();
                break;

            // Repeat
            case 2:
                this.surface.getDisplay ().notify ("Redo");
                this.model.getApplication ().redo ();
                break;

            // Undo
            case 3:
                this.surface.getDisplay ().notify ("Undo");
                this.model.getApplication ().undo ();
                break;

            // Toggle window of VSTs
            case 4:
                this.surface.getDisplay ().notify ("Device Window");
                cursorDevice.toggleWindowOpen ();
                break;

            // Insert device before current
            case 5:
                this.surface.getDisplay ().notify ("Add before");
                this.model.getBrowser ().insertBeforeCursorDevice ();
                this.surface.getViewManager ().setActive (Views.DEVICE);
                break;

            // Insert device after current
            case 6:
                this.surface.getDisplay ().notify ("Add after");
                this.model.getBrowser ().insertAfterCursorDevice ();
                this.surface.getViewManager ().setActive (Views.DEVICE);
                break;

            // Switch layouts
            case 7:
                this.surface.getDisplay ().notify ("Toggle Layout");
                IApplication app = this.model.getApplication ();
                if (!app.isMixerLayout ())
                    app.setPanelLayout ("MIX");
                else
                    app.setPanelLayout ("ARRANGE");
                break;

            default:
                viewIndex = note - 44;
                if (viewIndex < 0 || viewIndex >= 8)
                    return;

                final ViewManager viewManager = this.surface.getViewManager ();
                final Views viewId = VIEWS[viewIndex];
                viewManager.setPreviousID (viewId);
                view = viewManager.get (viewId);
                this.surface.getDisplay ().notify (view.getName ());
                break;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void onKnob (final int index, final int value, final boolean isTurnedRight)
    {
        switch (index) {
            // Hide/show VST window (show window, then save to make sure Maschine settings are saved)
            case 6:
                this.surface.getDisplay ().notify ("Device Window");
                final ICursorDevice cursorDevice = this.model.getCursorDevice ();
                cursorDevice.toggleWindowOpen ();
                break;

            // Save document
            case 7:
                this.surface.getDisplay ().notify ("Save");
                this.model.getProject ().save ();
                break;
        }
    }
}