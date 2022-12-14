package com.dippin.bsp.render;

import com.dippin.bsp.BSPTree;
import com.dippin.bsp.Polygon;
import com.dippin.bsp.Vector2;
import com.dippin.bsp.World;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.Iterator;

/**
 * Defines a window to view a <code>World</code>.
 */
public class ViewWindow {

    private final World world;
    private final InternalWindow window;

    /**
     * Create a new window from a <code>World</code>.
     *
     * @param world The <code>World</code> to create this window from.
     */
    public ViewWindow(World world) {
        this.world = world;
        this.window = new InternalWindow();
    }

    /**
     * Shows this window.
     */
    public void show() {
        this.window.setVisible(true);
    }

    class InternalWindow extends Frame implements WindowListener {

        InternalWindow() {
            add(new RenderComponent());
            addWindowListener(this);
            setTitle("BSP");
            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        }

        @Override
        public void windowOpened(WindowEvent e) { }

        @Override
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }

        @Override
        public void windowClosed(WindowEvent e) { }

        @Override
        public void windowIconified(WindowEvent e) { }

        @Override
        public void windowDeiconified(WindowEvent e) { }

        @Override
        public void windowActivated(WindowEvent e) { }

        @Override
        public void windowDeactivated(WindowEvent e) { }

        class RenderComponent extends Component implements MouseListener {

            BufferedImage backBuffer;
            int width, height;
            Graphics graphics;
            Vector2 min, max;
            int mouseX, mouseY;
            Vector2 scale;

            RenderComponent() {
                this.min = ViewWindow.this.world.getMin();
                this.max = ViewWindow.this.world.getMax();
                setPreferredSize(new Dimension(500, 500));
                addMouseListener(this);
            }

            void setupBuffers() {
                if (this.graphics != null)
                    this.graphics.dispose();

                if (this.backBuffer != null)
                    this.backBuffer.flush();

                Dimension dim = getSize();
                this.width = dim.width;
                this.height = dim.height;
                this.backBuffer = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
                this.graphics = backBuffer.createGraphics();

                this.scale = new Vector2(this.width / (this.max.x - this.min.y),
                        this.height / (this.max.y - this.min.y));
            }

            @Override
            public void paint(Graphics g) {
                if (this.backBuffer != null && this.graphics != null) {
                    this.graphics.setColor(Color.WHITE);
                    this.graphics.fillRect(0, 0, this.width, this.height);
                    drawWorld();
                    g.drawImage(this.backBuffer, 0, 0, this);
                }
            }

            @Override
            public void invalidate() {
                super.invalidate();

                this.width = -1;
                this.height = -1;

                if (this.graphics != null)
                    this.graphics.dispose();
                this.graphics = null;

                if (this.backBuffer != null)
                    this.backBuffer.flush();
                this.backBuffer = null;
            }

            @Override
            public void revalidate() {
                super.revalidate();
            }

            @Override
            public void validate() {
                super.validate();
                setupBuffers();
            }

            void drawWorld() {
                World world = ViewWindow.this.world;
                Vector2 worldMouse = toWorld(new Point(this.mouseX, this.mouseY));

                BSPTree tree = world.getBspTree();
                int idx = 0;
                Iterator<Polygon> iterator = tree.iterator(worldMouse);
                while (iterator.hasNext())
                    drawPoly(idx++, iterator.next());

                this.graphics.setColor(Color.BLUE);
                this.graphics.fillRect(mouseX - 2, mouseY - 2, 5, 5);

                this.graphics.setColor(Color.BLACK);
                this.graphics.drawString(String.format("World Polys: %d Partitioned Polys: %d Mouse World: (%f, %f)",
                        world.getPolys().size(), tree.getPolygonCount(), worldMouse.x, worldMouse.y), 0, 10);
            }

            void drawPoly(int index, Polygon poly) {
                Point start = toPoint(poly.getStart());
                Point end = toPoint(poly.getEnd());

                this.graphics.setColor(Color.RED);
                this.graphics.drawLine(start.x, start.y, end.x, end.y);

                Vector2 mid = poly.getStart().add(poly.getEnd()).div(2);

                Point normStart = toPoint(mid);
                Point normEnd = toPoint(mid.add(poly.getNormal()));

                Vector2 normal = new Vector2(normEnd.x - normStart.x, normEnd.y - normStart.y);
                normal = normal.normalize().mul(50);

                this.graphics.setColor(Color.GREEN);
                this.graphics.drawLine(normStart.x, normStart.y, (int)(normStart.x + normal.x), (int)(normStart.y + normal.y));

                this.graphics.setColor(Color.BLACK);
                this.graphics.drawString(Integer.toString(index), normStart.x, normStart.y);

                this.graphics.setColor(Color.BLACK);
                this.graphics.fillRect(start.x - 2, start.y - 2, 5, 5);
                this.graphics.fillRect(end.x - 2, end.y - 2, 5, 5);
            }

            Point toPoint(Vector2 point) {
                Vector2 scaled = point.sub(this.min).mul(this.scale);
                return new Point((int)scaled.x, this.height - (int)scaled.y);
            }

            Vector2 toWorld(Point point) {
               return new Vector2(point.x, this.height - point.y).div(this.scale).add(this.min);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                this.mouseX = e.getX();
                this.mouseY = e.getY();
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) { }

            @Override
            public void mouseReleased(MouseEvent e) { }

            @Override
            public void mouseEntered(MouseEvent e) { }

            @Override
            public void mouseExited(MouseEvent e) { }
        }
    }
}
