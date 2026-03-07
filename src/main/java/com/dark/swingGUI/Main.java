package com.dark.swingGUI;

import com.dark.App;
import com.dark.entity.Course;
import com.dark.entity.Student;
import com.dark.entity.Teacher;
import com.dark.service.LoginService;
import com.dark.service.StudentService;
import com.dark.service.TeacherService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class Main extends JFrame {

    // ══════════════════════════════════════════════
    // LIVE THEME DETECTION
    // ══════════════════════════════════════════════
    private static boolean DARK = detectDarkMode();

    private static boolean detectDarkMode() {
        try {
            Process p = Runtime.getRuntime().exec(new String[] { "defaults", "read", "-g", "AppleInterfaceStyle" });
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = r.readLine();
            r.close();
            return "Dark".equalsIgnoreCase(line != null ? line.trim() : "");
        } catch (Exception e) {
            return false;
        }
    }

    // ══════════════════════════════════════════════
    // STRIPE-INSPIRED THEME (mutable for live switch)
    // ══════════════════════════════════════════════

    // Gradient mesh colors (light: vibrant Stripe / dark: deep rich)
    private static Color GR1, GR2, GR3, GR4, GR5;

    // Cards & surfaces
    private static Color CARD, CARD_BORDER, CARD_SHADOW;
    private static Color INPUT_BG, INPUT_BORDER;

    // Accents (constant)
    private static final Color INDIGO = new Color(99, 91, 255);
    private static final Color INDIGO_H = new Color(79, 70, 229);
    private static final Color EMERALD = new Color(16, 185, 129);
    private static final Color EMERALD_H = new Color(5, 150, 105);
    private static final Color CORAL = new Color(244, 63, 94);
    private static final Color CORAL_H = new Color(225, 29, 72);

    // Text
    private static Color TXT, TXT2, TXT3;

    // List
    private static Color ROW_A, ROW_B, SEL_BG;

    // Toggle button inactive
    private static Color TOG_BG, TOG_BORDER;

    static {
        applyTheme();
    }

    private static void applyTheme() {
        if (DARK) {
            GR1 = new Color(15, 10, 40);
            GR2 = new Color(45, 15, 70);
            GR3 = new Color(20, 30, 80);
            GR4 = new Color(60, 20, 50);
            GR5 = new Color(10, 25, 55);
            CARD = new Color(20, 20, 40, 210);
            CARD_BORDER = new Color(255, 255, 255, 15);
            CARD_SHADOW = new Color(0, 0, 0, 80);
            INPUT_BG = new Color(15, 15, 35);
            INPUT_BORDER = new Color(60, 60, 100);
            TXT = new Color(248, 250, 252);
            TXT2 = new Color(160, 170, 190);
            TXT3 = new Color(100, 110, 130);
            ROW_A = new Color(20, 20, 42);
            ROW_B = new Color(28, 28, 52);
            SEL_BG = new Color(99, 91, 255, 45);
            TOG_BG = new Color(30, 30, 55);
            TOG_BORDER = new Color(60, 60, 100);
        } else {
            GR1 = new Color(255, 200, 150);
            GR2 = new Color(250, 130, 180);
            GR3 = new Color(160, 130, 255);
            GR4 = new Color(100, 180, 255);
            GR5 = new Color(255, 230, 180);
            CARD = new Color(255, 255, 255, 230);
            CARD_BORDER = new Color(0, 0, 0, 8);
            CARD_SHADOW = new Color(0, 0, 0, 20);
            INPUT_BG = new Color(247, 248, 252);
            INPUT_BORDER = new Color(215, 220, 230);
            TXT = new Color(15, 23, 42);
            TXT2 = new Color(100, 116, 139);
            TXT3 = new Color(160, 170, 185);
            ROW_A = new Color(255, 255, 255, 160);
            ROW_B = new Color(247, 248, 252, 160);
            SEL_BG = new Color(99, 91, 255, 30);
            TOG_BG = new Color(245, 246, 250);
            TOG_BORDER = new Color(215, 220, 230);
        }
    }

    private static void updateUIManager() {
        Color bg = DARK ? new Color(30, 30, 55) : new Color(247, 248, 252);
        UIManager.put("OptionPane.background", bg);
        UIManager.put("Panel.background", bg);
        UIManager.put("OptionPane.messageForeground", TXT);
        UIManager.put("Label.foreground", TXT);
    }

    // Fonts
    private static final Font FH = new Font("Helvetica Neue", Font.BOLD, 32);
    private static final Font F1 = new Font("Helvetica Neue", Font.BOLD, 22);
    private static final Font F2 = new Font("Helvetica Neue", Font.BOLD, 16);
    private static final Font F3 = new Font("Helvetica Neue", Font.BOLD, 14);
    private static final Font FB = new Font("Helvetica Neue", Font.PLAIN, 14);
    private static final Font FS = new Font("Helvetica Neue", Font.PLAIN, 12);
    private static final Font FBT = new Font("Helvetica Neue", Font.BOLD, 13);

    // Services
    private final LoginService loginService = new LoginService();
    private final StudentService studentService = new StudentService();
    private final TeacherService teacherService = new TeacherService();

    private final CardLayout cl = new CardLayout();
    private final JPanel mainPanel = new JPanel(cl);
    private final StudentPanel studentPanel;
    private final TeacherPanel teacherPanel;

    public Main() {
        setTitle("UniPortal — Course Registration");
        setSize(1120, 800);
        setMinimumSize(new Dimension(900, 650));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        updateUIManager();

        mainPanel.setOpaque(false);
        LoginPanel loginPanel = new LoginPanel();
        studentPanel = new StudentPanel();
        teacherPanel = new TeacherPanel();
        mainPanel.add(loginPanel, "L");
        mainPanel.add(studentPanel, "S");
        mainPanel.add(teacherPanel, "T");
        setContentPane(new MeshBG());
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel);

        // Live theme polling every 2s
        Timer timer = new Timer(2000, e -> {
            boolean nd = detectDarkMode();
            if (nd != DARK) {
                DARK = nd;
                applyTheme();
                updateUIManager();
                updateAll(this);
                repaint();
            }
        });
        timer.start();
    }

    private void updateAll(Container c) {
        for (Component comp : c.getComponents()) {
            if (comp instanceof JLabel) {
                Color fg = ((JLabel) comp).getForeground();
                if (!Color.WHITE.equals(fg) && !INDIGO.equals(fg) && fg.getAlpha() == 255)
                    ((JLabel) comp).setForeground(TXT);
            }
            if (comp instanceof JTextField) {
                ((JTextField) comp).setBackground(INPUT_BG);
                ((JTextField) comp).setForeground(TXT);
                ((JTextField) comp).setCaretColor(TXT);
            }
            if (comp instanceof Container)
                updateAll((Container) comp);
        }
    }

    // ══════════════════════════════════════════════
    // STRIPE MESH GRADIENT BACKGROUND
    // ══════════════════════════════════════════════
    static class MeshBG extends JPanel {
        MeshBG() {
            setOpaque(true);
        }

        protected void paintComponent(Graphics g) {
            int w = getWidth(), h = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            // Base
            g2.setColor(DARK ? new Color(10, 8, 30) : new Color(255, 250, 245));
            g2.fillRect(0, 0, w, h);
            // Mesh blobs
            float[][] blobs = { { -0.1f, -0.1f, 0.7f }, { 0.6f, -0.15f, 0.6f }, { 1.0f, 0.3f, 0.5f },
                    { -0.05f, 0.7f, 0.6f }, { 0.5f, 0.9f, 0.5f } };
            Color[] cols = { GR1, GR2, GR3, GR4, GR5 };
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, DARK ? 0.6f : 0.7f));
            for (int i = 0; i < blobs.length; i++) {
                int bx = (int) (blobs[i][0] * w), by = (int) (blobs[i][1] * h),
                        bs = (int) (blobs[i][2] * Math.max(w, h));
                RadialGradientPaint rg = new RadialGradientPaint(bx + bs / 2, by + bs / 2, bs / 2,
                        new float[] { 0f, 1f },
                        new Color[] { cols[i], new Color(cols[i].getRed(), cols[i].getGreen(), cols[i].getBlue(), 0) });
                g2.setPaint(rg);
                g2.fillOval(bx, by, bs, bs);
            }
            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════
    // GLASS CARD
    // ══════════════════════════════════════════════
    static class Card extends JPanel {
        int rad;

        Card(int r) {
            rad = r;
            setOpaque(false);
        }

        Card() {
            this(20);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Shadow
            g2.setColor(CARD_SHADOW);
            g2.fill(new RoundRectangle2D.Float(3, 3, getWidth() - 3, getHeight() - 3, rad, rad));
            // Fill
            g2.setColor(CARD);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 3, rad, rad));
            // Border
            g2.setColor(CARD_BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 4, getHeight() - 4, rad, rad));
            // Top shine
            g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, DARK ? 5 : 60), 0, 25, new Color(0, 0, 0, 0)));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, 25, rad, rad));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ══════════════════════════════════════════════
    // PILL BUTTON (for gradient action buttons)
    // ══════════════════════════════════════════════
    static class Pill extends JPanel {
        Color c1, c2;
        boolean hov;
        JLabel lbl;

        Pill(String t, Color a, Color b) {
            c1 = a;
            c2 = b;
            setOpaque(false);
            setLayout(new GridBagLayout());
            lbl = new JLabel(t);
            lbl.setFont(FBT);
            lbl.setForeground(Color.WHITE);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            add(lbl);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(160, 42));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    hov = true;
                    repaint();
                }

                public void mouseExited(MouseEvent e) {
                    hov = false;
                    repaint();
                }
            });
        }

        void setCol(Color a, Color b) {
            c1 = a;
            c2 = b;
            repaint();
        }

        void addActionListener(ActionListener al) {
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    al.actionPerformed(new ActionEvent(Pill.this, ActionEvent.ACTION_PERFORMED, ""));
                }
            });
        }

        public void setForeground(Color c) {
            if (lbl != null)
                lbl.setForeground(c);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, hov ? c2 : c1, getWidth(), 0, hov ? c1 : c2));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ══════════════════════════════════════════════
    // TOGGLE BUTTON (for Student/Teacher switch)
    // ══════════════════════════════════════════════
    static class Toggle extends JPanel {
        boolean active;
        JLabel lbl;

        Toggle(String t, boolean act) {
            active = act;
            setOpaque(false);
            setLayout(new GridBagLayout());
            lbl = new JLabel(t);
            lbl.setFont(FBT);
            lbl.setForeground(act ? Color.WHITE : TXT2);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            add(lbl);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(170, 44));
        }

        void setActive(boolean a) {
            active = a;
            lbl.setForeground(a ? Color.WHITE : TXT2);
            repaint();
        }

        void addActionListener(ActionListener al) {
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    al.actionPerformed(new ActionEvent(Toggle.this, ActionEvent.ACTION_PERFORMED, ""));
                }
            });
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (active) {
                g2.setPaint(new GradientPaint(0, 0, INDIGO, getWidth(), 0, new Color(139, 92, 246)));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
            } else {
                g2.setColor(TOG_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(TOG_BORDER);
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 12, 12));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ══════════════════════════════════════════════
    // LOGIN PANEL
    // ══════════════════════════════════════════════
    class LoginPanel extends JPanel {
        JTextField userF = mkField(22);
        JPasswordField passF = mkPass(22);
        Toggle stuBtn = new Toggle("Student", true);
        Toggle tchBtn = new Toggle("Teacher", false);
        Pill loginBtn;

        LoginPanel() {
            setOpaque(false);
            setLayout(new GridBagLayout());

            Card card = new Card(24);
            card.setLayout(new GridBagLayout());
            card.setPreferredSize(new Dimension(440, 560));
            GridBagConstraints g = new GridBagConstraints();
            g.insets = new Insets(5, 30, 5, 30);
            g.fill = GridBagConstraints.HORIZONTAL;
            g.gridx = 0;
            g.gridy = 0;
            g.gridwidth = 2;

            // Icon
            JLabel ico = mkLbl("Uni", FH, INDIGO);
            ico.setHorizontalAlignment(SwingConstants.CENTER);
            card.add(ico, g);

            // Title
            g.gridy++;
            g.insets = new Insets(6, 30, 0, 30);
            JLabel ti = mkLbl("Welcome Back", FH, TXT);
            ti.setHorizontalAlignment(SwingConstants.CENTER);
            card.add(ti, g);

            g.gridy++;
            g.insets = new Insets(2, 30, 20, 30);
            JLabel su = mkLbl("Sign in to your university portal", FB, TXT2);
            su.setHorizontalAlignment(SwingConstants.CENTER);
            card.add(su, g);

            // Role toggle
            g.gridy++;
            g.insets = new Insets(4, 30, 12, 30);
            JPanel toggleRow = new JPanel(new GridLayout(1, 2, 10, 0));
            toggleRow.setOpaque(false);
            stuBtn.addActionListener(e -> {
                stuBtn.setActive(true);
                tchBtn.setActive(false);
            });
            tchBtn.addActionListener(e -> {
                tchBtn.setActive(true);
                stuBtn.setActive(false);
            });
            toggleRow.add(stuBtn);
            toggleRow.add(tchBtn);
            card.add(toggleRow, g);

            // User ID
            g.gridy++;
            g.insets = new Insets(6, 30, 2, 30);
            card.add(mkLbl("User ID", FS, TXT2), g);
            g.gridy++;
            g.insets = new Insets(0, 30, 8, 30);
            card.add(userF, g);

            // Password
            g.gridy++;
            g.insets = new Insets(6, 30, 2, 30);
            card.add(mkLbl("Password", FS, TXT2), g);
            g.gridy++;
            g.insets = new Insets(0, 30, 8, 30);
            card.add(passF, g);

            // Sign In button
            g.gridy++;
            g.insets = new Insets(18, 30, 8, 30);
            loginBtn = new Pill("Sign In", INDIGO, INDIGO_H);
            loginBtn.setPreferredSize(new Dimension(380, 48));
            loginBtn.addActionListener(e -> login());
            card.add(loginBtn, g);

            // Footer
            g.gridy++;
            g.insets = new Insets(12, 30, 10, 30);
            JLabel ft = mkLbl("University Course Registration System", FS, TXT3);
            ft.setHorizontalAlignment(SwingConstants.CENTER);
            card.add(ft, g);

            add(card);
        }

        void setLoading(boolean loading) {
            loginBtn.lbl.setText(loading ? "Signing in..." : "Sign In");
            loginBtn.setCol(loading ? TXT3 : INDIGO, loading ? TXT3 : INDIGO_H);
            userF.setEnabled(!loading);
            passF.setEnabled(!loading);
        }

        void login() {
            String id = userF.getText().trim(), pw = new String(passF.getPassword());
            if (id.isEmpty() || pw.isEmpty()) {
                err("Enter both User ID and Password.");
                return;
            }
            setLoading(true);

            boolean isStu = stuBtn.active;
            new SwingWorker<Object, Void>() {
                protected Object doInBackground() {
                    if (isStu)
                        return loginService.loginStudent(id, pw);
                    else
                        return loginService.loginTeacher(id, pw);
                }

                protected void done() {
                    setLoading(false);
                    try {
                        Object result = get();
                        if (isStu) {
                            Student s = (Student) result;
                            if (s != null) {
                                studentPanel.load(s);
                                cl.show(mainPanel, "S");
                                userF.setText("");
                                passF.setText("");
                            } else
                                err("Invalid Student Credentials");
                        } else {
                            Teacher t = (Teacher) result;
                            if (t != null) {
                                teacherPanel.load(t);
                                cl.show(mainPanel, "T");
                                userF.setText("");
                                passF.setText("");
                            } else
                                err("Invalid Teacher Credentials");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        err("Login error: " + (ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()));
                    }
                }
            }.execute();
        }
    }

    // ══════════════════════════════════════════════
    // STUDENT PANEL
    // ══════════════════════════════════════════════
    class StudentPanel extends JPanel {
        JLabel namL = new JLabel(), rolL = new JLabel();
        JLabel sAll = new JLabel("0"), sMy = new JLabel("0"), sAv = new JLabel("0");
        DefaultListModel<Course> avM = new DefaultListModel<>(), myM = new DefaultListModel<>();
        JList<Course> avL = new JList<>(avM), myL = new JList<>(myM);
        Student cur;

        StudentPanel() {
            setOpaque(false);
            setLayout(new BorderLayout());

            JPanel hdr = gradHdr();
            namL.setFont(F1);
            namL.setForeground(Color.WHITE);
            rolL.setFont(FB);
            rolL.setForeground(new Color(224, 231, 255));
            JPanel nb = new JPanel(new GridLayout(2, 1));
            nb.setOpaque(false);
            nb.add(namL);
            nb.add(rolL);
            Pill cpBtn = new Pill("Change Password", INDIGO, INDIGO_H);
            cpBtn.setPreferredSize(new Dimension(160, 36));
            cpBtn.addActionListener(e -> changePassDlg());
            Pill lo = new Pill("Logout", CORAL, CORAL_H);
            lo.setPreferredSize(new Dimension(100, 36));
            lo.addActionListener(e -> cl.show(mainPanel, "L"));
            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            btns.setOpaque(false);
            btns.add(cpBtn);
            btns.add(lo);
            hdr.add(nb, BorderLayout.CENTER);
            hdr.add(btns, BorderLayout.EAST);
            add(hdr, BorderLayout.NORTH);

            JPanel body = new JPanel(new BorderLayout(0, 16));
            body.setOpaque(false);
            body.setBorder(new EmptyBorder(16, 24, 24, 24));

            JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
            stats.setOpaque(false);
            stats.add(mkStat("[T]", "Total Courses", sAll));
            stats.add(mkStat("[R]", "Registered", sMy));
            stats.add(mkStat("[A]", "Available", sAv));
            body.add(stats, BorderLayout.NORTH);

            JPanel cols = new JPanel(new GridLayout(1, 2, 20, 0));
            cols.setOpaque(false);

            Card left = mkCard("Available Courses");
            stList(avL);
            avL.setCellRenderer(new CRen());
            Pill regB = new Pill("Register Selected", EMERALD, EMERALD_H);
            regB.addActionListener(e -> {
                Course c = avL.getSelectedValue();
                if (c == null) {
                    err("Select a course.");
                    return;
                }
                suc(studentService.registerStudentForCourse(cur.getRollNumber(), c.getCourseId()));
                refresh();
            });
            left.add(mkScroll(avL), BorderLayout.CENTER);
            left.add(regB, BorderLayout.SOUTH);

            Card right = mkCard("My Schedule");
            stList(myL);
            myL.setCellRenderer(new CRen());
            Pill drpB = new Pill("Drop Selected", CORAL, CORAL_H);
            drpB.addActionListener(e -> {
                Course c = myL.getSelectedValue();
                if (c == null) {
                    err("Select a course.");
                    return;
                }
                suc(studentService.dropCourse(cur.getRollNumber(), c.getCourseId()));
                refresh();
            });
            right.add(mkScroll(myL), BorderLayout.CENTER);
            right.add(drpB, BorderLayout.SOUTH);

            cols.add(left);
            cols.add(right);
            body.add(cols, BorderLayout.CENTER);
            add(body, BorderLayout.CENTER);

            MouseAdapter ma = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        @SuppressWarnings("unchecked")
                        JList<Course> l = (JList<Course>) e.getSource();
                        Course c = l.getSelectedValue();
                        if (c != null)
                            coursePopup(c);
                    }
                }
            };
            avL.addMouseListener(ma);
            myL.addMouseListener(ma);
        }

        void load(Student s) {
            cur = s;
            namL.setText("Welcome, " + s.getName());
            rolL.setText("Roll No: " + s.getRollNumber());
            refresh();
        }

        void refresh() {
            List<Course> all = studentService.getAllCourses();
            cur = loginService.loginStudent(cur.getRollNumber(), cur.getPassword());
            List<Course> mine = cur.getRegisteredCourses();
            avM.clear();
            all.forEach(avM::addElement);
            myM.clear();
            mine.forEach(myM::addElement);
            sAll.setText(String.valueOf(all.size()));
            sMy.setText(String.valueOf(mine.size()));
            sAv.setText(String.valueOf(all.size() - mine.size()));
        }

        void changePassDlg() {
            JPasswordField oldP = mkPass(18), newP = mkPass(18), cfmP = mkPass(18);
            JPanel pn = new JPanel(new GridLayout(3, 2, 12, 12));
            pn.setOpaque(false);
            pn.add(mkLbl("Old Password:", FB, TXT2));
            pn.add(oldP);
            pn.add(mkLbl("New Password:", FB, TXT2));
            pn.add(newP);
            pn.add(mkLbl("Confirm New:", FB, TXT2));
            pn.add(cfmP);
            if (JOptionPane.showConfirmDialog(this, pn, "Change Password",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
                String op = new String(oldP.getPassword());
                String np = new String(newP.getPassword());
                String cp = new String(cfmP.getPassword());
                if (np.isEmpty()) {
                    err("New password cannot be empty.");
                    return;
                }
                if (!np.equals(cp)) {
                    err("Passwords do not match.");
                    return;
                }
                String result = studentService.changePassword(cur.getRollNumber(), op, np);
                if (result.contains("success")) {
                    cur.setPassword(np);
                    suc(result);
                } else
                    err(result);
            }
        }
    }

    // ══════════════════════════════════════════════
    // TEACHER PANEL
    // ══════════════════════════════════════════════
    class TeacherPanel extends JPanel {
        JLabel admL = new JLabel(), stSt = new JLabel("0"), stCr = new JLabel("0");
        DefaultListModel<Student> stuM = new DefaultListModel<>();
        DefaultListModel<Course> crsM = new DefaultListModel<>();
        DefaultListModel<String> detM = new DefaultListModel<>();
        JList<Student> stuL = new JList<>(stuM);
        JList<Course> crsL = new JList<>(crsM);
        JList<String> detL = new JList<>(detM);
        CardLayout lCL = new CardLayout();
        JPanel lCon = new JPanel(lCL);
        Toggle tS, tC;
        // Pagination state
        static final int PAGE_SIZE = 12;
        int studentOffset = 0;
        boolean hasMoreStudents = true;
        Pill loadMoreBtn;
        JTextField searchField;

        TeacherPanel() {
            setOpaque(false);
            setLayout(new BorderLayout());

            JPanel hdr = darkHdr();
            admL.setFont(F1);
            admL.setForeground(TXT);
            Pill lo = new Pill("Logout", CORAL, CORAL_H);
            lo.setPreferredSize(new Dimension(100, 36));
            lo.addActionListener(e -> cl.show(mainPanel, "L"));
            hdr.add(admL, BorderLayout.CENTER);
            hdr.add(lo, BorderLayout.EAST);
            add(hdr, BorderLayout.NORTH);

            JPanel body = new JPanel(new BorderLayout(0, 16));
            body.setOpaque(false);
            body.setBorder(new EmptyBorder(16, 24, 24, 24));

            JPanel stats = new JPanel(new GridLayout(1, 2, 16, 0));
            stats.setOpaque(false);
            stats.add(mkStat("[S]", "Students", stSt));
            stats.add(mkStat("[C]", "Courses", stCr));
            body.add(stats, BorderLayout.NORTH);

            JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
            content.setOpaque(false);

            Card leftP = mkCard("");
            leftP.setLayout(new BorderLayout());
            JPanel tabs = new JPanel(new GridLayout(1, 2, 8, 0));
            tabs.setOpaque(false);
            tabs.setBorder(new EmptyBorder(0, 0, 12, 0));
            tS = new Toggle("Students", true);
            tC = new Toggle("Courses", false);
            tS.addActionListener(e -> swTab(true));
            tC.addActionListener(e -> swTab(false));
            tabs.add(tS);
            tabs.add(tC);
            leftP.add(tabs, BorderLayout.NORTH);

            // ── Student view with search bar ──
            JPanel sv = new JPanel(new BorderLayout(0, 8));
            sv.setOpaque(false);

            // Search bar
            searchField = mkField(18);
            searchField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(INPUT_BORDER),
                    new EmptyBorder(8, 12, 8, 12)));
            JPanel searchRow = new JPanel(new BorderLayout(8, 0));
            searchRow.setOpaque(false);
            JLabel searchLbl = mkLbl("Search Roll No:", FS, TXT2);
            searchRow.add(searchLbl, BorderLayout.WEST);
            searchRow.add(searchField, BorderLayout.CENTER);
            searchField.addActionListener(e -> searchStudent());
            sv.add(searchRow, BorderLayout.NORTH);

            stList(stuL);
            stuL.setCellRenderer(new SRen());
            sv.add(mkScroll(stuL), BorderLayout.CENTER);

            // Bottom buttons: Load More + Refresh
            JPanel sb = new JPanel(new GridLayout(1, 2, 8, 0));
            sb.setOpaque(false);
            loadMoreBtn = new Pill("Load More", INDIGO, INDIGO_H);
            loadMoreBtn.addActionListener(e -> loadMoreStudents());
            Pill rS = new Pill("Refresh", DARK ? new Color(50, 50, 80) : new Color(180, 185, 200),
                    DARK ? new Color(60, 60, 95) : new Color(160, 165, 180));
            rS.setForeground(DARK ? new Color(200, 200, 220) : new Color(80, 80, 100));
            rS.addActionListener(e -> {
                searchField.setText("");
                refAll();
            });
            sb.add(loadMoreBtn);
            sb.add(rS);
            sv.add(sb, BorderLayout.SOUTH);

            // ── Course view ──
            JPanel cv = new JPanel(new BorderLayout(0, 8));
            cv.setOpaque(false);
            stList(crsL);
            crsL.setCellRenderer(new CRen());
            JPanel cb = new JPanel(new GridLayout(1, 2, 8, 0));
            cb.setOpaque(false);
            Pill aC = new Pill("Add Course", EMERALD, EMERALD_H);
            Pill rC = new Pill("Remove", CORAL, CORAL_H);
            aC.addActionListener(e -> addCrsDlg());
            rC.addActionListener(e -> {
                Course c = crsL.getSelectedValue();
                if (c == null) {
                    err("Select a course.");
                    return;
                }
                if (JOptionPane.showConfirmDialog(this, "Remove " + c.getCourseName() + "?", "Confirm",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    teacherService.removeCourse(c.getCourseId());
                    refAll();
                    suc("Course removed.");
                }
            });
            cb.add(aC);
            cb.add(rC);
            cv.add(mkScroll(crsL), BorderLayout.CENTER);
            cv.add(cb, BorderLayout.SOUTH);

            lCon.setOpaque(false);
            lCon.add(sv, "S");
            lCon.add(cv, "C");
            leftP.add(lCon, BorderLayout.CENTER);

            Card rightP = mkCard("Details");
            stList(detL);
            detL.setCellRenderer(new DRen());
            rightP.add(mkScroll(detL), BorderLayout.CENTER);

            content.add(leftP);
            content.add(rightP);
            body.add(content, BorderLayout.CENTER);
            add(body, BorderLayout.CENTER);

            stuL.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    Student s = stuL.getSelectedValue();
                    if (s != null)
                        showStu(s);
                }
            });
            crsL.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    Course c = crsL.getSelectedValue();
                    if (c != null)
                        showCrs(c);
                }
            });
            detL.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        String v = detL.getSelectedValue();
                        if (v != null && v.contains("Course:")) {
                            int a = v.lastIndexOf('('), b = v.lastIndexOf(')');
                            if (a != -1 && b > a) {
                                String id = v.substring(a + 1, b);
                                teacherService.getAllCourses().stream().filter(c -> c.getCourseId().equals(id))
                                        .findFirst().ifPresent(c -> coursePopup(c));
                            }
                        }
                    }
                }
            });
        }

        void swTab(boolean s) {
            lCL.show(lCon, s ? "S" : "C");
            tS.setActive(s);
            tC.setActive(!s);
            detM.clear();
        }

        void showStu(Student s) {
            detM.clear();
            Student f = loginService.loginStudent(s.getRollNumber(), s.getPassword());
            if (f == null) {
                detM.addElement("e:Could not load.");
                return;
            }
            detM.addElement("h:" + f.getName());
            detM.addElement("s:Roll No: " + f.getRollNumber());
            detM.addElement("div");
            detM.addElement("sec:Enrolled Courses");
            if (f.getRegisteredCourses().isEmpty())
                detM.addElement("e:No courses registered.");
            else
                f.getRegisteredCourses()
                        .forEach(c -> detM.addElement("Course: " + c.getCourseName() + " (" + c.getCourseId() + ")"));
        }

        void showCrs(Course c) {
            detM.clear();
            List<Student> en = studentService.getStudentsEnrolledInCourse(c.getCourseId());
            detM.addElement("h:" + c.getCourseName());
            detM.addElement("s:ID: " + c.getCourseId());
            detM.addElement("div");
            detM.addElement("sec:Enrolled Students (" + en.size() + ")");
            if (en.isEmpty())
                detM.addElement("e:No students enrolled.");
            else
                en.forEach(s -> detM.addElement("Student: " + s.getName() + " (" + s.getRollNumber() + ")"));
        }

        void searchStudent() {
            String roll = searchField.getText().trim();
            if (roll.isEmpty()) {
                refAll();
                return;
            }
            Student s = teacherService.searchStudentByRoll(roll);
            stuM.clear();
            if (s != null) {
                stuM.addElement(s);
                loadMoreBtn.setVisible(false);
            } else {
                err("No student found with roll number: " + roll);
                loadMoreBtn.setVisible(false);
            }
        }

        void loadMoreStudents() {
            List<Student> page = teacherService.getStudentsPaginated(studentOffset, PAGE_SIZE);
            page.forEach(stuM::addElement);
            studentOffset += page.size();
            if (page.size() < PAGE_SIZE) {
                hasMoreStudents = false;
                loadMoreBtn.setVisible(false);
            }
        }

        void addCrsDlg() {
            JTextField ci = mkField(18), cn = mkField(18);
            JPanel pn = new JPanel(new GridLayout(2, 2, 12, 12));
            pn.setOpaque(false);
            pn.add(mkLbl("Course ID:", FB, TXT2));
            pn.add(ci);
            pn.add(mkLbl("Course Name:", FB, TXT2));
            pn.add(cn);
            if (JOptionPane.showConfirmDialog(this, pn, "Add Course", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION && !ci.getText().isEmpty()) {
                teacherService.addCourse(ci.getText().trim(), cn.getText().trim());
                refAll();
                suc("Course added!");
            }
        }

        void load(Teacher t) {
            if (t != null)
                admL.setText("Admin - " + t.getName());
            refAll();
            swTab(true);
        }

        void refAll() {
            // Reset pagination and load first page
            studentOffset = 0;
            hasMoreStudents = true;
            stuM.clear();
            List<Student> page = teacherService.getStudentsPaginated(0, PAGE_SIZE);
            page.forEach(stuM::addElement);
            studentOffset = page.size();
            if (page.size() < PAGE_SIZE) {
                hasMoreStudents = false;
            }
            loadMoreBtn.setVisible(hasMoreStudents);

            crsM.clear();
            teacherService.getAllCourses().forEach(crsM::addElement);
            detM.clear();
            long totalStudents = teacherService.getStudentCount();
            stSt.setText(String.valueOf(totalStudents));
            stCr.setText(String.valueOf(crsM.size()));
        }
    }

    // ══════════════════════════════════════════════
    // COURSE DETAILS POPUP
    // ══════════════════════════════════════════════
    private void coursePopup(Course c) {
        List<Student> en = studentService.getStudentsEnrolledInCourse(c.getCourseId());
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        Color bg = DARK ? new Color(25, 25, 50) : new Color(247, 248, 252);
        p.setBackground(bg);
        p.setBorder(new EmptyBorder(24, 30, 24, 30));

        JLabel ic = mkLbl("Course", F1, INDIGO);
        ic.setAlignmentX(CENTER_ALIGNMENT);
        p.add(ic);
        p.add(Box.createVerticalStrut(8));
        JLabel tl = mkLbl(c.getCourseName(), F1, TXT);
        tl.setAlignmentX(CENTER_ALIGNMENT);
        p.add(tl);
        JLabel il = mkLbl("ID: " + c.getCourseId(), FB, TXT2);
        il.setAlignmentX(CENTER_ALIGNMENT);
        p.add(il);
        p.add(Box.createVerticalStrut(14));
        JSeparator sep = new JSeparator();
        sep.setForeground(INPUT_BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        p.add(sep);
        p.add(Box.createVerticalStrut(14));
        JLabel el = mkLbl("Enrolled: " + en.size(), F3, INDIGO);
        el.setAlignmentX(LEFT_ALIGNMENT);
        p.add(el);
        p.add(Box.createVerticalStrut(8));
        if (en.isEmpty()) {
            JLabel nl = mkLbl("  No students enrolled.", FB, TXT3);
            nl.setAlignmentX(LEFT_ALIGNMENT);
            p.add(nl);
        } else
            en.forEach(s -> {
                JLabel sl = mkLbl("  - " + s.getName() + " (" + s.getRollNumber() + ")", FB, TXT);
                sl.setAlignmentX(LEFT_ALIGNMENT);
                p.add(sl);
                p.add(Box.createVerticalStrut(4));
            });
        JScrollPane sp = new JScrollPane(p);
        sp.setBorder(null);
        sp.getViewport().setBackground(bg);
        sp.setPreferredSize(new Dimension(400, Math.min(420, 220 + en.size() * 28)));
        JOptionPane.showMessageDialog(this, sp, "Course Details", JOptionPane.PLAIN_MESSAGE);
    }

    // ══════════════════════════════════════════════
    // CELL RENDERERS
    // ══════════════════════════════════════════════
    class CRen extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
            JLabel lb = (JLabel) super.getListCellRendererComponent(l, v, i, s, f);
            if (v instanceof Course) {
                Course c = (Course) v;
                lb.setText("  " + c.getCourseName() + "  |  " + c.getCourseId());
            }
            lb.setFont(FB);
            lb.setBorder(new EmptyBorder(12, 16, 12, 16));
            lb.setOpaque(true);
            lb.setBackground(s ? SEL_BG : i % 2 == 0 ? ROW_A : ROW_B);
            lb.setForeground(s ? INDIGO : TXT);
            return lb;
        }
    }

    class SRen extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
            JLabel lb = (JLabel) super.getListCellRendererComponent(l, v, i, s, f);
            if (v instanceof Student) {
                Student st = (Student) v;
                lb.setText("  " + st.getName() + "  |  " + st.getRollNumber());
            }
            lb.setFont(FB);
            lb.setBorder(new EmptyBorder(12, 16, 12, 16));
            lb.setOpaque(true);
            lb.setBackground(s ? SEL_BG : i % 2 == 0 ? ROW_A : ROW_B);
            lb.setForeground(s ? INDIGO : TXT);
            return lb;
        }
    }

    class DRen extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
            JLabel lb = (JLabel) super.getListCellRendererComponent(l, v, i, s, f);
            String t = v.toString();
            lb.setOpaque(true);
            lb.setBackground(ROW_A);
            lb.setBorder(new EmptyBorder(6, 16, 6, 16));
            if (t.startsWith("h:")) {
                lb.setText(t.substring(2));
                lb.setFont(F2);
                lb.setForeground(TXT);
                lb.setBorder(new EmptyBorder(12, 16, 2, 16));
            } else if (t.startsWith("s:")) {
                lb.setText(t.substring(2));
                lb.setFont(FS);
                lb.setForeground(TXT2);
            } else if (t.equals("div")) {
                lb.setText(" ");
                lb.setFont(FS.deriveFont(4f));
                lb.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, INPUT_BORDER));
            } else if (t.startsWith("sec:")) {
                lb.setText(t.substring(4));
                lb.setFont(F3);
                lb.setForeground(INDIGO);
                lb.setBorder(new EmptyBorder(10, 16, 4, 16));
            } else if (t.startsWith("e:")) {
                lb.setText(t.substring(2));
                lb.setFont(FB);
                lb.setForeground(TXT3);
            } else {
                lb.setText("  " + t);
                lb.setFont(FB);
                lb.setForeground(TXT);
            }
            return lb;
        }
    }

    // ══════════════════════════════════════════════
    // FACTORY HELPERS
    // ══════════════════════════════════════════════
    private JPanel gradHdr() {
        JPanel h = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, INDIGO, getWidth(), 0, new Color(139, 92, 246)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        h.setBorder(new EmptyBorder(20, 28, 20, 28));
        h.setPreferredSize(new Dimension(0, 80));
        return h;
    }

    private JPanel darkHdr() {
        Card h = new Card(0);
        h.setLayout(new BorderLayout());
        h.setBorder(new EmptyBorder(20, 28, 20, 28));
        h.setPreferredSize(new Dimension(0, 80));
        return h;
    }

    private Card mkCard(String title) {
        Card p = new Card(18);
        p.setLayout(new BorderLayout(0, 10));
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        if (!title.isEmpty())
            p.add(mkLbl(title, F3, TXT), BorderLayout.NORTH);
        return p;
    }

    private Card mkStat(String icon, String label, JLabel val) {
        Card p = new Card(14);
        p.setLayout(new BorderLayout(12, 0));
        p.setBorder(new EmptyBorder(16, 20, 16, 20));
        p.add(mkLbl(icon, F2, INDIGO), BorderLayout.WEST);
        JPanel tx = new JPanel(new GridLayout(2, 1));
        tx.setOpaque(false);
        val.setFont(F2);
        val.setForeground(TXT);
        tx.add(val);
        tx.add(mkLbl(label, FS, TXT2));
        p.add(tx, BorderLayout.CENTER);
        return p;
    }

    static JLabel mkLbl(String t, Font f, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }

    static JTextField mkField(int cols) {
        JTextField f = new JTextField(cols);
        f.setFont(FB);
        f.setBackground(INPUT_BG);
        f.setForeground(TXT);
        f.setCaretColor(TXT);
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(INPUT_BORDER),
                new EmptyBorder(10, 14, 10, 14)));
        return f;
    }

    static JPasswordField mkPass(int cols) {
        JPasswordField f = new JPasswordField(cols);
        f.setFont(FB);
        f.setBackground(INPUT_BG);
        f.setForeground(TXT);
        f.setCaretColor(TXT);
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(INPUT_BORDER),
                new EmptyBorder(10, 14, 10, 14)));
        return f;
    }

    private <T> void stList(JList<T> l) {
        l.setFont(FB);
        l.setOpaque(false);
        l.setForeground(TXT);
        l.setSelectionBackground(SEL_BG);
        l.setSelectionForeground(INDIGO);
        l.setFixedCellHeight(46);
    }

    private JScrollPane mkScroll(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        return sp;
    }

    private void err(String m) {
        JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void suc(String m) {
        JOptionPane.showMessageDialog(this, m, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(App::run);
    }
}