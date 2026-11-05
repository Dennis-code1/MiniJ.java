import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MiniJ extends JPanel {
    // Each point: {x, y, penDown}. penDown=1 means the segment ARRIVING at this point is drawn.
    static final List<double[]> P = new ArrayList<>();
    static {
        P.add(new double[]{190, 100, 1});   // start of top bar (left)
        P.add(new double[]{380, 100, 1});   // end of top bar (right)
        P.add(new double[]{285, 100, 0});   // move (no drawing) to the middle of the bar
        P.add(new double[]{285, 400, 1});   // stem going down
        // Hook: half circle, center (215,400), radius 70, from the right side, through the bottom, to the left
        for (int deg = 10; deg <= 180; deg += 10) {
            double t = Math.toRadians(deg);
            P.add(new double[]{215 + 70 * Math.cos(t), 400 + 70 * Math.sin(t), 1});
        }
    }

    static final double CX = 262, CY = 285;     // center of the J (rotation pivot)
    static final double DEG_PER_FRAME = 3;      // rotation speed (clockwise, constant)
    static final int SPIN_ONLY_FRAMES = 120;    // 120 * 3 = exactly 360 degrees before moving
    static final double SPEED = 4;              // pixels per frame while tracing

    int frame = 0;

    MiniJ() {
        setBackground(Color.WHITE);
        new Timer(16, e -> { frame++; repaint(); }).start();
    }

    // Draws the J scaled around its center at the current origin
    void drawSmall(Graphics2D g, double scale) {
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 1; i < P.size(); i++) {
            if (P.get(i)[2] == 0) continue;
            double[] a = P.get(i - 1), b = P.get(i);
            g.drawLine((int) ((a[0] - CX) * scale), (int) ((a[1] - CY) * scale),
                       (int) ((b[0] - CX) * scale), (int) ((b[1] - CY) * scale));
        }
    }

    @Override protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double angle = Math.toRadians(frame * DEG_PER_FRAME);  // always increasing = constant clockwise spin
        double x = P.get(0)[0], y = P.get(0)[1];
        double scale = 0.3;

        if (frame <= SPIN_ONLY_FRAMES) {
            // PHASE 1: spin in place at the center
            x = CX; y = CY;
        } else {
            // PHASE 2: trace the big J (trail) while the small J keeps spinning
            double dist = (frame - SPIN_ONLY_FRAMES) * SPEED;
            g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(Color.RED);
            for (int i = 1; i < P.size(); i++) {
                double[] a = P.get(i - 1), b = P.get(i);
                double len = Math.hypot(b[0] - a[0], b[1] - a[1]);
                double used = Math.min(dist, len);
                x = a[0] + (b[0] - a[0]) * used / len;
                y = a[1] + (b[1] - a[1]) * used / len;
                if (b[2] == 1)
                    g.drawLine((int) a[0], (int) a[1], (int) x, (int) y);
                dist -= used;
                if (dist <= 0) break;
            }
            scale = 0.15;
        }

        // The small spinning J
        Graphics2D s = (Graphics2D) g.create();
        s.translate(x, y);
        s.rotate(angle);          // positive angle = clockwise on screen
        s.setColor(Color.BLUE);
        drawSmall(s, scale);
        s.dispose();
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Mini Project 1 - Letter J");
        f.add(new MiniJ());
        f.setSize(520, 650);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}
