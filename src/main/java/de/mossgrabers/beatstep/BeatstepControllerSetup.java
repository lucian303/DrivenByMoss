// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.beatstep;

import de.mossgrabers.beatstep.command.continuous.BeatstepPlayPositionCommand;
import de.mossgrabers.beatstep.command.continuous.KnobRowViewCommand;
import de.mossgrabers.beatstep.command.trigger.StepCommand;
import de.mossgrabers.beatstep.controller.BeatstepColors;
import de.mossgrabers.beatstep.controller.BeatstepControlSurface;
import de.mossgrabers.beatstep.controller.BeatstepValueChanger;
import de.mossgrabers.beatstep.view.BrowserView;
import de.mossgrabers.beatstep.view.DeviceView;
import de.mossgrabers.beatstep.view.DrumView;
import de.mossgrabers.beatstep.view.PlayView;
import de.mossgrabers.beatstep.view.SequencerView;
import de.mossgrabers.beatstep.view.SessionView;
import de.mossgrabers.beatstep.view.ShiftView;
import de.mossgrabers.beatstep.view.TrackView;
import de.mossgrabers.beatstep.view.Views;
import de.mossgrabers.framework.command.Commands;
import de.mossgrabers.framework.command.aftertouch.AftertouchAbstractPlayViewCommand;
import de.mossgrabers.framework.controller.AbstractControllerSetup;
import de.mossgrabers.framework.controller.ISetupFactory;
import de.mossgrabers.framework.controller.color.ColorManager;
import de.mossgrabers.framework.controller.display.DummyDisplay;
import de.mossgrabers.framework.daw.IChannelBank;
import de.mossgrabers.framework.daw.ICursorDevice;
import de.mossgrabers.framework.daw.IHost;
import de.mossgrabers.framework.daw.ITrackBank;
import de.mossgrabers.framework.daw.data.IMasterTrack;
import de.mossgrabers.framework.daw.data.ITrack;
import de.mossgrabers.framework.daw.midi.IMidiAccess;
import de.mossgrabers.framework.daw.midi.IMidiInput;
import de.mossgrabers.framework.daw.midi.IMidiOutput;
import de.mossgrabers.framework.scale.Scales;
import de.mossgrabers.framework.view.ViewManager;

import com.bitwig.extension.controller.api.Preferences;


/**
 * Bitwig Studio extension to support the Arturia Beatstep and Beatstep Pro controllers.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class BeatstepControllerSetup extends AbstractControllerSetup<BeatstepControlSurface, BeatstepConfiguration>
{
    private static final int [] DRUM_MATRIX =
    {
        0,
        1,
        2,
        3,
        4,
        5,
        6,
        7,
        8,
        9,
        10,
        11,
        12,
        13,
        14,
        15,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1
    };

    private final boolean       isPro;


    /**
     * Constructor.
     *
     * @param host The DAW host
     * @param factory The factory
     * @param preferences The preferences
     * @param isPro True if Beatstep Pro
     */
    protected BeatstepControllerSetup (final IHost host, final ISetupFactory factory, final Preferences preferences, final boolean isPro)
    {
        super (factory, host, preferences);
        this.isPro = isPro;
        this.colorManager = new ColorManager ();
        BeatstepColors.addColors (this.colorManager);
        this.valueChanger = new BeatstepValueChanger (128, 1, 0.5);
        this.configuration = new BeatstepConfiguration (this.valueChanger, isPro);
    }


    /** {@inheritDoc} */
    @Override
    public void flush ()
    {
        this.flushSurfaces ();
    }


    /** {@inheritDoc} */
    @Override
    protected void createScales ()
    {
        this.scales = new Scales (this.valueChanger, 36, 52, 8, 2);
        this.scales.setDrumMatrix (DRUM_MATRIX);
        this.scales.setDrumNoteEnd (52);
    }


    /** {@inheritDoc} */
    @Override
    protected void createModel ()
    {
        this.model = this.factory.createModel (this.colorManager, this.valueChanger, this.scales, 8, 8, 8, 16, 16, true, -1, -1, -1, -1);
        this.model.getTrackBank ().addTrackSelectionObserver (this::handleTrackChange);
    }


    /** {@inheritDoc} */
    @Override
    protected void createSurface ()
    {
        final IMidiAccess midiAccess = this.factory.createMidiAccess ();
        final IMidiOutput output = midiAccess.createOutput ();
        final IMidiInput input = midiAccess.createInput ("Control", "82????", "92????", "A2????", "B2????");

        // Sequencer 1 is on channel 1
        input.createNoteInput ("Seq. 1", "90????", "80????");

        // Setup the 2 note sequencers and 1 drum sequencer
        if (this.isPro)
        {
            // Sequencer 2 is on channel 2
            input.createNoteInput ("Seq. 2", "91????", "81????");
            // Drum Sequencer is on channel 10
            input.createNoteInput ("Drums", "99????", "89????");
        }

        final BeatstepControlSurface surface = new BeatstepControlSurface (this.model.getHost (), this.colorManager, this.configuration, output, input, this.isPro);
        this.surfaces.add (surface);
        surface.setDisplay (new DummyDisplay (this.host));
    }


    /** {@inheritDoc} */
    @Override
    protected void createObservers ()
    {
        this.getSurface ().getViewManager ().addViewChangeListener ( (previousViewId, activeViewId) -> this.updateIndication ());
        this.createScaleObservers (this.configuration);
    }


    /** {@inheritDoc} */
    @Override
    protected void createViews ()
    {
        final BeatstepControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        viewManager.registerView (Views.VIEW_TRACK, new TrackView (surface, this.model));
        viewManager.registerView (Views.VIEW_DEVICE, new DeviceView (surface, this.model));
        viewManager.registerView (Views.VIEW_PLAY, new PlayView (surface, this.model));
        viewManager.registerView (Views.VIEW_DRUM, new DrumView (surface, this.model));
        viewManager.registerView (Views.VIEW_SEQUENCER, new SequencerView (surface, this.model));
        viewManager.registerView (Views.VIEW_SESSION, new SessionView (surface, this.model));
        viewManager.registerView (Views.VIEW_BROWSER, new BrowserView (surface, this.model));
        viewManager.registerView (Views.VIEW_SHIFT, new ShiftView (surface, this.model));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerTriggerCommands ()
    {
        if (!this.isPro)
            return;

        final BeatstepControlSurface surface = this.getSurface ();
        for (int i = 0; i < 16; i++)
            this.addTriggerCommand (Integer.valueOf (Commands.COMMAND_ROW1_1.intValue () + i), BeatstepControlSurface.BEATSTEP_PRO_STEP1 + i, new StepCommand (i, this.model, surface));
    }


    /** {@inheritDoc} */
    @Override
    protected void registerContinuousCommands ()
    {
        final BeatstepControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        for (int i = 0; i < 8; i++)
        {
            this.addContinuousCommand (Integer.valueOf (Commands.CONT_COMMAND_KNOB1.intValue () + i), BeatstepControlSurface.BEATSTEP_KNOB_1 + i, new KnobRowViewCommand (i, this.model, surface));
            this.addContinuousCommand (Integer.valueOf (Commands.CONT_COMMAND_DEVICE_KNOB1.intValue () + i), BeatstepControlSurface.BEATSTEP_KNOB_9 + i, new KnobRowViewCommand (i + 8, this.model, surface));
        }
        this.addContinuousCommand (Commands.CONT_COMMAND_MASTER_KNOB, BeatstepControlSurface.BEATSTEP_KNOB_MAIN, new BeatstepPlayPositionCommand (this.model, surface));
        final PlayView playView = (PlayView) viewManager.getView (Views.VIEW_PLAY);
        playView.registerAftertouchCommand (new AftertouchAbstractPlayViewCommand<> (playView, this.model, surface));
    }


    /** {@inheritDoc} */
    @Override
    protected void startup ()
    {
        // Enable Shift button to send Midi Note 07
        final BeatstepControlSurface surface = this.getSurface ();
        surface.getOutput ().sendSysex ("F0 00 20 6B 7F 42 02 00 01 5E 09 F7");
        surface.scheduleTask ( () -> surface.getViewManager ().setActiveView (Views.VIEW_TRACK), 100);
    }


    private void updateIndication ()
    {
        final BeatstepControlSurface surface = this.getSurface ();
        final ViewManager viewManager = surface.getViewManager ();
        final boolean isTrack = viewManager.isActiveView (Views.VIEW_TRACK);
        final boolean isDevice = viewManager.isActiveView (Views.VIEW_DEVICE);
        final boolean isSession = viewManager.isActiveView (Views.VIEW_SESSION);

        final IMasterTrack mt = this.model.getMasterTrack ();
        mt.setVolumeIndication (!isDevice);

        final ITrackBank tb = this.model.getTrackBank ();
        final ITrack selectedTrack = tb.getSelectedTrack ();
        final IChannelBank tbe = this.model.getEffectTrackBank ();
        final ITrack selectedFXTrack = tbe.getSelectedTrack ();
        final ICursorDevice cursorDevice = this.model.getCursorDevice ();
        final boolean isEffect = this.model.isEffectTrackBankActive ();

        tb.setIndication (!isEffect && isSession);
        tbe.setIndication (isEffect && isSession);

        for (int i = 0; i < 8; i++)
        {
            final boolean hasTrackSel = selectedTrack != null && selectedTrack.getIndex () == i;
            tb.setVolumeIndication (i, !isEffect && hasTrackSel && !isDevice);
            tb.setPanIndication (i, !isEffect && hasTrackSel && !isDevice);
            for (int j = 0; j < 6; j++)
                tb.setSendIndication (i, j, !isEffect && hasTrackSel && isTrack);

            final boolean hasFXTrackSel = selectedFXTrack != null && selectedFXTrack.getIndex () == i;
            tbe.setVolumeIndication (i, isEffect && hasFXTrackSel && isTrack);
            tbe.setPanIndication (i, isEffect && hasFXTrackSel && isTrack);

            cursorDevice.indicateParameter (i, isDevice);
        }
    }


    /**
     * Handle a track selection change.
     *
     * @param index The index of the track
     * @param isSelected Has the track been selected?
     */
    private void handleTrackChange (final int index, final boolean isSelected)
    {
        if (!isSelected)
            return;

        final ViewManager viewManager = this.getSurface ().getViewManager ();
        if (viewManager.isActiveView (Views.VIEW_PLAY))
            viewManager.getActiveView ().updateNoteMapping ();

        // Reset drum octave because the drum pad bank is also reset
        this.scales.setDrumOctave (0);
        if (viewManager.isActiveView (Views.VIEW_DRUM))
            viewManager.getView (Views.VIEW_DRUM).updateNoteMapping ();
    }
}