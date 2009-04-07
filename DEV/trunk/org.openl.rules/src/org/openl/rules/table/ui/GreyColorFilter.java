/**
 * Created Mar 5, 2007
 */
package org.openl.rules.table.ui;

/**
 * @author snshor
 *
 */
public class GreyColorFilter implements IColorFilter {
    double brightness;

    public GreyColorFilter(double brightness) {
        this.brightness = brightness;
    }

    public short[] filterColor(short[] color) {

        if (color == null) {
            color = BLACK;
        }

        int avg = (color[0] + color[1] + color[2]) / 3;

        avg = (int) (avg * brightness);

        return new short[] { (short) avg, (short) avg, (short) avg };
    }

}
