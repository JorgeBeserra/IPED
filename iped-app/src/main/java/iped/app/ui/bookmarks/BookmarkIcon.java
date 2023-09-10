package iped.app.ui.bookmarks;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import iped.app.ui.IconManager;
import iped.data.IMultiBookmarks;

public class BookmarkIcon implements Icon {
    public static final String columnName = "$BookmarkIcon";

    private static final Map<Color, Icon> iconPerColor = new HashMap<Color, Icon>();
    private static final Map<String, Icon> iconPerBookmarkStr = new HashMap<String, Icon>();
    private static final Stroke strokeBorder = new BasicStroke(1f);
    private static final RenderingHints renderingHints;

    static {
        Map<Key, Object> hints = new HashMap<Key, Object>();
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        hints.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        renderingHints = new RenderingHints(hints);
    }

    private final Color color;
    private final Color[] colors;

    public static Icon getIcon(IMultiBookmarks bookmarks, String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }

        synchronized (iconPerBookmarkStr) {
            Icon icon = iconPerBookmarkStr.get(str);
            if (icon == null) {
                if (str.indexOf(" | ") < 0) {
                    return getIcon(bookmarks.getBookmarkColor(str));
                }

                String[] bookmarkNames = str.split(" \\| ");
                Color[] colors = new Color[bookmarkNames.length];
                for (int i = 0; i < bookmarkNames.length; i++) {
                    colors[i] = bookmarks.getBookmarkColor(bookmarkNames[i]);
                }

                icon = new BookmarkIcon(colors);
                iconPerBookmarkStr.put(str, icon);
            }
            return icon;
        }
    }

    public static synchronized Icon getIcon(Color color) {
        Icon icon = iconPerColor.get(color);
        if (icon == null) {
            icon = new BookmarkIcon(color);
            iconPerColor.put(color, icon);
        }
        return icon;
    }

    private BookmarkIcon(Color color) {
        this.color = color;
        this.colors = null;
    }

    private BookmarkIcon(Color[] colors) {
        this.color = null;
        this.colors = colors;
    }

    @Override
    public void paintIcon(Component comp, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;
        RenderingHints saveHints = g2.getRenderingHints();
        g2.setRenderingHints(renderingHints);

        int size = getIconWidth();
        int arc = size / 3 + 2;

        if (colors == null) {
            g2.setColor(color == null ? BookmarkStandardColors.defaultColor : color);
            g2.fillRoundRect(x + 1, y + 1, size - 2, size - 2, arc, arc);
        } else {
            double w = (size - 2) / (double) colors.length;
            double d = x + 1;
            Shape saveClip = g2.getClip();
            for (Color c : colors) {
                g2.clip(new Rectangle2D.Double(d, y, w, size));
                g2.setColor(c == null ? BookmarkStandardColors.defaultColor : c);
                g2.fillRoundRect(x + 1, y + 1, size - 2, size - 2, arc, arc);
                g2.setClip(saveClip);
                d += w;
            }
        }

        Color colorBorder = comp.getForeground();
        if (colorBorder == null) {
            colorBorder = Color.gray;
        }
        colorBorder = new Color(colorBorder.getRed(), colorBorder.getGreen(), colorBorder.getBlue(), 64);

        g2.setStroke(strokeBorder);
        g2.setColor(colorBorder);
        g2.drawRoundRect(x + 1, y + 1, size - 2, size - 2, arc, arc);

        g2.setRenderingHints(saveHints);
    }

    @Override
    public int getIconWidth() {
        return IconManager.getIconSize() - 2;
    }

    @Override
    public int getIconHeight() {
        return IconManager.getIconSize() - 2;
    }
}
