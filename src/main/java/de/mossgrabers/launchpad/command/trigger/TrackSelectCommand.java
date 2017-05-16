// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.launchpad.command.trigger;

import de.mossgrabers.framework.ButtonEvent;
import de.mossgrabers.framework.Model;
import de.mossgrabers.launchpad.controller.LaunchpadControlSurface;
import de.mossgrabers.launchpad.mode.Modes;


/**
 * Track select command.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 */
public class TrackSelectCommand extends AbstractTrackCommand
{
    /**
     * Constructor.
     *
     * @param model The model
     * @param surface The surface
     */
    public TrackSelectCommand (final Model model, final LaunchpadControlSurface surface)
    {
        super (model, surface);
    }


    /** {@inheritDoc} */
    @Override
    public void execute (final ButtonEvent event)
    {
        this.onModeButton (event, Modes.MODE_TRACK_SELECT, "Track Select");
    }
}
