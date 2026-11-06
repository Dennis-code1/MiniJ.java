import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MiniJ extends JPanel {
        // ===================== CHOOSE THE STARTING POINT =====================
    static final boolean START_FROM_BOTTOM = false;   // true = begin at the hook, false = begin at the top bar

    // ===================== PATH A: FROM THE TOP (original, index 0 to 21) =====================
    static final double[][] FROM_TOP = {
        {190.0, 100.0, 1},   //  0  START: left end of top bar
        {380.0, 100.0, 1},   //  1  right end of top bar // only Horizontal line, no vertical line
        {285.0, 100.0, 0},   //  2  pen-up jump to middle of bar// vertical line, no horizontal line
        {285.0, 400.0, 1},   //  3  bottom of stem
        {283.9, 412.2, 1},   //  4  hook 10 deg
        {280.8, 423.9, 1},   //  5  20 deg
        {275.6, 435.0, 1},   //  6  30 deg
        {268.6, 445.0, 1},   //  7  40 deg
        {260.0, 453.6, 1},   //  8  50 deg
        {250.0, 460.6, 1},   //  9  60 deg
        {238.9, 465.8, 1},   // 10  70 deg
        {227.2, 468.9, 1},   // 11  80 deg
        {215.0, 470.0, 1},   // 12  90 deg (lowest point)
        {202.8, 468.9, 1},   // 13  100 deg
        {191.1, 465.8, 1},   // 14  110 deg
        {180.0, 460.6, 1},   // 15  120 deg
        {170.0, 453.6, 1},   // 16  130 deg
        {161.4, 445.0, 1},   // 17  140 deg
        {154.4, 435.0, 1},   // 18  150 deg
        {149.2, 423.9, 1},   // 19  160 deg
        {146.1, 412.2, 1},   // 20  170 deg
        {145.0, 400.0, 1}    // 21  END: tip of hook
    };

    // ===================== PATH B: FROM THE BOTTOM (reversed, index 0 to 21) =====================
    static final double[][] FROM_BOTTOM = {
        {145.0, 400.0, 1},   //  0  START: tip of the hook
        {146.1, 412.2, 1},   //  1  170 deg
        {149.2, 423.9, 1},   //  2  160 deg
        {154.4, 435.0, 1},   //  3  150 deg
        {161.4, 445.0, 1},   //  4  140 deg
        {170.0, 453.6, 1},   //  5  130 deg
        {180.0, 460.6, 1},   //  6  120 deg
        {191.1, 465.8, 1},   //  7  110 deg
        {202.8, 468.9, 1},   //  8  100 deg
        {215.0, 470.0, 1},   //  9  90 deg (lowest point)
        {227.2, 468.9, 1},   // 10  80 deg
        {238.9, 465.8, 1},   // 11  70 deg
        {250.0, 460.6, 1},   // 12  60 deg
        {260.0, 453.6, 1},   // 13  50 deg
        {268.6, 445.0, 1},   // 14  40 deg
        {275.6, 435.0, 1},   // 15  30 deg
        {280.8, 423.9, 1},   // 16  20 deg
        {283.9, 412.2, 1},   // 17  10 deg
        {285.0, 400.0, 1},   // 18  bottom of stem
        {285.0, 100.0, 1},   // 19  top of stem (draws the stem upward)
        {380.0, 100.0, 0},   // 20  pen-up jump to the right end of the bar
        {190.0, 100.0, 1}    // 21  END: draws the bar right to left

    };

    // The path the program actually uses
    static final double[][] P = START_FROM_BOTTOM ? FROM_BOTTOM : FROM_TOP;
    // =======================================================================
    // =======================================================================

    static final double CX = 262, CY = 285;     // center of the J (rotation pivot)
    static final double DEG_PER_FRAME = 3;      // spin speed
    static final double SPEED = 4;              // pixels per frame along the path

    int moveDir = +1;   // +1 forward, -1 backward   (SPACE toggles)
    int spinDir = +1;   // +1 clockwise, -1 counter-clockwise (R toggles)
    boolean showPoints = false;                 // P toggles the numbered dots

    double angleDeg = 0;
    double phase1Turned = 0;
    boolean tracing = false;
    double dist = 0;
    final double total;

    MiniJ() {
        setBackground(Color.CYAN);// background color of the window changed to cyan
        double t = 0;
        for (int i = 1; i < P.length; i++)
            t += Math.hypot(P[i][0] - P[i - 1][0], P[i][1] - P[i - 1][1]);
        total = t;

        bind("SPACE", () -> moveDir = -moveDir);
        bind("R", () -> spinDir = -spinDir);
        bind("P", () -> showPoints = !showPoints);

        new Timer(16, e -> { update(); repaint(); }).start();
    }

    void bind(String key, Runnable r) {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key);
        getActionMap().put(key, new AbstractAction() {
            public void actionPerformed(ActionEvent e) { r.run(); }
        });
    }

    void update() {
        angleDeg += DEG_PER_FRAME * spinDir; //always spin, even during tracing
        if (!tracing) {
            phase1Turned += DEG_PER_FRAME;
            if (phase1Turned >= 360) tracing = true; // after one full turn, start tracing
        } else {
            dist += SPEED * moveDir;
            dist = Math.max(0, Math.min(total, dist));
        }
    }

    void drawSmall(Graphics2D g, double scale) {
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 1; i < P.length; i++) {
            if (P[i][2] == 0) continue;
            g.drawLine((int) ((P[i-1][0] - CX) * scale), (int) ((P[i-1][1] - CY) * scale),
                       (int) ((P[i][0]   - CX) * scale), (int) ((P[i][1]   - CY) * scale));
        }
    }

    @Override protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double x = CX, y = CY, scale = 0.3;

        if (tracing) {
            scale = 0.15;
            double d = dist;
            x = P[0][0]; y = P[0][1];
            g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(Color.BLUE); //Colour of Big J when tracing
            for (int i = 1; i < P.length && d > 0; i++) {
                double len = Math.hypot(P[i][0] - P[i-1][0], P[i][1] - P[i-1][1]);
                double used = Math.min(d, len);
                x = P[i-1][0] + (P[i][0] - P[i-1][0]) * used / len;
                y = P[i-1][1] + (P[i][1] - P[i-1][1]) * used / len;
                if (P[i][2] == 1) g.drawLine((int) P[i-1][0], (int) P[i-1][1], (int) x, (int) y);
                d -= used;
            }
        }

        // Optional: show the path points as numbered dots
        if (showPoints) {
            g.setFont(new Font("SansSerif", Font.BOLD, 11));
            for (int i = 0; i < P.length; i++) {
                g.setColor(P[i][2] == 1 ? Color.GREEN.darker() : Color.ORANGE);
                g.fillOval((int) P[i][0] - 3, (int) P[i][1] - 3, 7, 7);
                g.setColor(Color.BLACK);
                g.drawString(String.valueOf(i), (int) P[i][0] + 6, (int) P[i][1] - 4);
            }
        }

        // Small spinning J
        Graphics2D s = (Graphics2D) g.create();
        s.translate(x, y);
        s.rotate(Math.toRadians(angleDeg));
        s.setColor(Color.RED); // Change the color of the small J to red
        drawSmall(s, scale);
        s.dispose();

        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.drawString("SPACE: reverse movement  |  R: reverse spin  |  P: show points", 15, 25);
        g.drawString("Movement: " + (moveDir > 0 ? "forward" : "backward")
                + "   Spin: " + (spinDir > 0 ? "clockwise" : "counter-clockwise"), 15, 45);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Mini Project 1 - Letter J");
        f.add(new MiniJ());
        f.setSize(520, 650);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}