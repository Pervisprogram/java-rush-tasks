import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AntiProcrastinationApp extends JFrame {
    private final DefaultListModel<String> taskModel = new DefaultListModel<>();
    private final DefaultListModel<String> doneModel = new DefaultListModel<>();
    private final DefaultTableModel habitModel = new DefaultTableModel(new Object[]{"Привычка", "Серия (дней)", "Последняя отметка"}, 0);

    private final JLabel totalTasksLabel = new JLabel("0");
    private final JLabel doneTasksLabel = new JLabel("0");
    private final JLabel focusSessionsLabel = new JLabel("0");
    private final JProgressBar progressBar = new JProgressBar(0, 100);

    private final JLabel timerLabel = new JLabel("25:00", SwingConstants.CENTER);
    private final JComboBox<String> modeBox = new JComboBox<>(new String[]{"Фокус 25 мин", "Короткий перерыв 5 мин", "Длинный перерыв 15 мин"});

    private Timer timer;
    private int remainingSeconds = 25 * 60;
    private int focusSessions = 0;

    public AntiProcrastinationApp() {
        setTitle("АнтиПрокрастинация — фокус и привычки");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1050, 700);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 247, 252));
        root.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(root);

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createMainContent(), BorderLayout.CENTER);

        refreshStats();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(10, 10));
        header.setOpaque(false);

        JLabel title = new JLabel("Планируй. Действуй. Побеждай прокрастинацию.");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(40, 54, 94));

        JLabel date = new JLabel("Сегодня: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        date.setFont(new Font("SansSerif", Font.PLAIN, 15));
        date.setForeground(new Color(90, 102, 138));

        header.add(title, BorderLayout.WEST);
        header.add(date, BorderLayout.EAST);
        return header;
    }

    private JComponent createMainContent() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBorder(null);
        splitPane.setDividerLocation(600);

        JPanel left = new JPanel(new BorderLayout(12, 12));
        left.setOpaque(false);
        left.add(createStatsPanel(), BorderLayout.NORTH);
        left.add(createTasksPanel(), BorderLayout.CENTER);

        JPanel right = new JPanel(new BorderLayout(12, 12));
        right.setOpaque(false);
        right.add(createTimerPanel(), BorderLayout.NORTH);
        right.add(createHabitsPanel(), BorderLayout.CENTER);

        splitPane.setLeftComponent(left);
        splitPane.setRightComponent(right);
        return splitPane;
    }

    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(227, 231, 240)),
                new EmptyBorder(12, 12, 12, 12)));
        return card;
    }

    private JPanel createStatsPanel() {
        JPanel panel = createCard();
        panel.setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("Дашборд продуктивности");
        header.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel grid = new JPanel(new GridLayout(1, 3, 10, 10));
        grid.setOpaque(false);
        grid.add(metric("Задач всего", totalTasksLabel, new Color(58, 125, 219)));
        grid.add(metric("Выполнено", doneTasksLabel, new Color(40, 167, 69)));
        grid.add(metric("Фокус-сессий", focusSessionsLabel, new Color(255, 145, 77)));

        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(108, 99, 255));

        panel.add(header, BorderLayout.NORTH);
        panel.add(grid, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel metric(String name, JLabel value, Color color) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(false);
        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(new Color(95, 105, 130));

        value.setHorizontalAlignment(SwingConstants.CENTER);
        value.setFont(new Font("SansSerif", Font.BOLD, 30));
        value.setForeground(color);

        item.add(nameLabel, BorderLayout.NORTH);
        item.add(value, BorderLayout.CENTER);
        return item;
    }

    private JPanel createTasksPanel() {
        JPanel panel = createCard();
        panel.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("План задач");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JTextField taskField = new JTextField();
        taskField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JButton addBtn = button("Добавить", new Color(58, 125, 219));

        JPanel input = new JPanel(new BorderLayout(8, 0));
        input.setOpaque(false);
        input.add(taskField, BorderLayout.CENTER);
        input.add(addBtn, BorderLayout.EAST);

        JList<String> todoList = new JList<>(taskModel);
        JList<String> completedList = new JList<>(doneModel);
        todoList.setBorder(BorderFactory.createTitledBorder("Активные"));
        completedList.setBorder(BorderFactory.createTitledBorder("Сделано"));

        JButton doneBtn = button("Отметить выполненной", new Color(40, 167, 69));
        JButton deleteBtn = button("Удалить", new Color(220, 53, 69));

        JPanel controls = new JPanel(new GridLayout(1, 2, 8, 8));
        controls.setOpaque(false);
        controls.add(doneBtn);
        controls.add(deleteBtn);

        JSplitPane lists = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(todoList), new JScrollPane(completedList));
        lists.setResizeWeight(0.6);

        addBtn.addActionListener(e -> {
            String text = taskField.getText().trim();
            if (!text.isEmpty()) {
                taskModel.addElement(text + "  (" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + ")");
                taskField.setText("");
                refreshStats();
            }
        });

        doneBtn.addActionListener(e -> {
            int i = todoList.getSelectedIndex();
            if (i >= 0) {
                doneModel.addElement("✅ " + taskModel.getElementAt(i));
                taskModel.remove(i);
                refreshStats();
            }
        });

        deleteBtn.addActionListener(e -> {
            int i = todoList.getSelectedIndex();
            if (i >= 0) {
                taskModel.remove(i);
                refreshStats();
            }
        });

        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);
        top.add(title, BorderLayout.NORTH);
        top.add(input, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(lists, BorderLayout.CENTER);
        panel.add(controls, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTimerPanel() {
        JPanel panel = createCard();
        panel.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Pomodoro таймер");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 56));
        timerLabel.setForeground(new Color(108, 99, 255));

        JButton startBtn = button("Старт", new Color(40, 167, 69));
        JButton pauseBtn = button("Пауза", new Color(255, 145, 77));
        JButton resetBtn = button("Сброс", new Color(108, 117, 125));

        JPanel buttons = new JPanel(new GridLayout(1, 3, 8, 8));
        buttons.setOpaque(false);
        buttons.add(startBtn);
        buttons.add(pauseBtn);
        buttons.add(resetBtn);

        modeBox.addActionListener(e -> resetTimer());

        startBtn.addActionListener(e -> startTimer());
        pauseBtn.addActionListener(e -> {
            if (timer != null) timer.stop();
        });
        resetBtn.addActionListener(e -> resetTimer());

        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);
        top.add(title, BorderLayout.NORTH);
        top.add(modeBox, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(timerLabel, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createHabitsPanel() {
        JPanel panel = createCard();
        panel.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Трекер привычек");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));

        JTextField habitField = new JTextField();
        JButton add = button("Новая привычка", new Color(58, 125, 219));
        JButton mark = button("Отметить сегодня", new Color(40, 167, 69));

        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setOpaque(false);
        top.add(habitField, BorderLayout.CENTER);
        top.add(add, BorderLayout.EAST);

        JTable habitsTable = new JTable(habitModel);
        habitsTable.setRowHeight(26);

        add.addActionListener(e -> {
            String h = habitField.getText().trim();
            if (!h.isEmpty()) {
                habitModel.addRow(new Object[]{h, 0, "—"});
                habitField.setText("");
            }
        });

        mark.addActionListener(e -> {
            int row = habitsTable.getSelectedRow();
            if (row >= 0) {
                int streak = Integer.parseInt(habitModel.getValueAt(row, 1).toString());
                habitModel.setValueAt(streak + 1, row, 1);
                habitModel.setValueAt(LocalDate.now().toString(), row, 2);
            }
        });

        JPanel head = new JPanel(new BorderLayout(0, 8));
        head.setOpaque(false);
        head.add(title, BorderLayout.NORTH);
        head.add(top, BorderLayout.SOUTH);

        panel.add(head, BorderLayout.NORTH);
        panel.add(new JScrollPane(habitsTable), BorderLayout.CENTER);
        panel.add(mark, BorderLayout.SOUTH);
        return panel;
    }

    private JButton button(String text, Color color) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        return b;
    }

    private void startTimer() {
        if (timer != null && timer.isRunning()) return;

        timer = new Timer(1000, e -> {
            remainingSeconds--;
            updateTimerLabel();
            if (remainingSeconds <= 0) {
                timer.stop();
                Toolkit.getDefaultToolkit().beep();
                if (modeBox.getSelectedIndex() == 0) {
                    focusSessions++;
                    refreshStats();
                }
                JOptionPane.showMessageDialog(this, "Сессия завершена! Отличная работа 🚀");
                resetTimer();
            }
        });
        timer.start();
    }

    private void resetTimer() {
        if (timer != null) timer.stop();
        remainingSeconds = switch (modeBox.getSelectedIndex()) {
            case 1 -> 5 * 60;
            case 2 -> 15 * 60;
            default -> 25 * 60;
        };
        updateTimerLabel();
    }

    private void updateTimerLabel() {
        int m = remainingSeconds / 60;
        int s = remainingSeconds % 60;
        timerLabel.setText(String.format("%02d:%02d", m, s));
    }

    private void refreshStats() {
        int total = taskModel.size() + doneModel.size();
        int done = doneModel.size();

        totalTasksLabel.setText(String.valueOf(total));
        doneTasksLabel.setText(String.valueOf(done));
        focusSessionsLabel.setText(String.valueOf(focusSessions));

        int progress = total == 0 ? 0 : (done * 100 / total);
        progressBar.setValue(progress);
        progressBar.setString("Прогресс дня: " + progress + "%");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new AntiProcrastinationApp().setVisible(true);
        });
    }
}
