package view;

import main.Controller;
import vo.JobPosting;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SwingView extends JFrame implements View {
    
    private Controller controller;
    
    // UI компоненти
    private JComboBox<String> locationComboBox;
    private JComboBox<String> positionComboBox;
    private JButton searchButton;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    
    public SwingView() {
        setupUI();
    }
    
    private void setupUI() {
        setTitle("Java Job Aggregator");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // === ПАНЕЛЬ ФІЛЬТРІВ (ВЕРХ) ===
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("🔍 Search Filters"));
        
        // Dropdown локацій
        JLabel locationLabel = new JLabel("Location:");
        String[] locations = {"Dallas, TX", "Austin, TX", "San Francisco, CA", "New York, NY"};
        locationComboBox = new JComboBox<>(locations);
        
        // Dropdown позицій
        JLabel positionLabel = new JLabel("Position:");
        String[] positions = {"Java Developer", "Senior Java Developer", "Java Engineer"};
        positionComboBox = new JComboBox<>(positions);
        
        // Кнопка пошуку
        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> onSearchClicked());
        
        filterPanel.add(locationLabel);
        filterPanel.add(locationComboBox);
        filterPanel.add(positionLabel);
        filterPanel.add(positionComboBox);
        filterPanel.add(searchButton);
        
        add(filterPanel, BorderLayout.NORTH);
        
        // === ТАБЛИЦЯ РЕЗУЛЬТАТІВ (ЦЕНТР) ===
        String[] columnNames = {"Title", "Company", "Location", "Website"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Заборонити редагування
            }
        };
        
        resultsTable = new JTable(tableModel);
        resultsTable.setRowHeight(30);
        resultsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("📊 Results"));
        
        add(scrollPane, BorderLayout.CENTER);
        
        // === ПАНЕЛЬ СТАТУСУ (НИЗ) ===
        JPanel bottomPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Ready. Select filters and click Search.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JButton exportButton = new JButton("Export to HTML");
        exportButton.addActionListener(e -> exportToHtml());
        
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(exportButton, BorderLayout.EAST);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        setLocationRelativeTo(null); // Центрувати вікно
    }
    
    private void onSearchClicked() {
        String location = (String) locationComboBox.getSelectedItem();
        String position = (String) positionComboBox.getSelectedItem();
        
        statusLabel.setText("Searching for " + position + " in " + location + "...");
        searchButton.setEnabled(false);
        
        // Запускаємо пошук в окремому потоці (щоб UI не зависав)
        new Thread(() -> {
            controller.onCitySelected(location); // Поки що тільки location
            
            SwingUtilities.invokeLater(() -> {
                searchButton.setEnabled(true);
            });
        }).start();
    }
    
    @Override
    public void update(List<JobPosting> vacancies) {
        SwingUtilities.invokeLater(() -> {
            // Очистити таблицю
            tableModel.setRowCount(0);
            
            // Додати нові вакансії
            for (JobPosting job : vacancies) {
                Object[] row = {
                    job.getTitle(),
                    job.getCompanyName(),
                    job.getCity(),
                    job.getWebsiteName()
                };
                tableModel.addRow(row);
            }
            
            statusLabel.setText("Found " + vacancies.size() + " jobs");
        });
    }
    
    @Override
    public void setController(Controller controller) {
        this.controller = controller;
    }
    
    private void exportToHtml() {
        // TODO: Викликати HtmlView для експорту (опціонально)
        JOptionPane.showMessageDialog(this, "Export feature - coming soon!");
    }
}