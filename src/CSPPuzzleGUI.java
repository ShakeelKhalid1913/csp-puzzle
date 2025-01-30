import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class CSPPuzzleGUI extends JFrame {
    private static final int CELL_SIZE = 60;
    private static int GRID_SIZE;
    private final List<Color> groupColors;
    private Random random;
    private List<String> data = PuzzleData.puzzle3();
    
    private Color generateRandomPastelColor() {
        // Generate pastel colors by mixing with white
        // Base color components between 0.4 and 0.8 for better visibility
        float hue = random.nextFloat();
        float saturation = 0.3f + random.nextFloat() * 0.3f; // 0.3-0.6
        float brightness = 0.9f + random.nextFloat() * 0.1f; // 0.9-1.0
        
        return Color.getHSBColor(hue, saturation, brightness);
    }
    
    private Color generateDistinctColor(List<Color> existingColors) {
        if (existingColors.isEmpty()) {
            return generateRandomPastelColor();
        }
        
        Color newColor;
        double maxMinDistance = 0;
        Color bestColor = null;
        
        // Try 20 random colors and pick the one with maximum minimum distance to existing colors
        for (int i = 0; i < 20; i++) {
            newColor = generateRandomPastelColor();
            double minDistance = Double.MAX_VALUE;
            
            // Calculate minimum distance to existing colors
            for (Color existing : existingColors) {
                double distance = getColorDistance(newColor, existing);
                minDistance = Math.min(minDistance, distance);
            }
            
            // Keep the color with the largest minimum distance
            if (minDistance > maxMinDistance) {
                maxMinDistance = minDistance;
                bestColor = newColor;
            }
        }
        
        return bestColor;
    }
    
    private double getColorDistance(Color c1, Color c2) {
        // Calculate color distance using RGB values
        double rmean = (c1.getRed() + c2.getRed()) / 2.0;
        int r = c1.getRed() - c2.getRed();
        int g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        
        // Weighted distance using human perception weights
        return Math.sqrt((2 + rmean/256.0) * r*r + 4.0 * g*g + (2 + (255-rmean)/256.0) * b*b);
    }
    
    private CSPPuzzleSolver solver;
    private Map<Cell, Integer> groupColorMap;
    private JPanel gridPanel;
    private List<Group> groups;

    public CSPPuzzleGUI() {
        solver = new CSPPuzzleSolver(GRID_SIZE);
        groupColorMap = new HashMap<>();
        groups = new ArrayList<>();
        random = new Random();
        groupColors = new ArrayList<>();
        
        setupGroups();
        initializeGUI();
    }

    private void setupGroups() {
        GRID_SIZE = Integer.parseInt(data.get(0));

        solver = new CSPPuzzleSolver(GRID_SIZE);
        for(int i = 1; i < data.size(); i++){
            if (data.get(i).startsWith("#")) {
                continue;
            }

            String[] data1 = data.get(i).split("->");

            List<Cell> cells = new ArrayList<>();
            String[] cellsData = data1[0]
                        .split(";");

            for(String cellData : cellsData){
                String[] cords = cellData.split(",");

                Cell cell = new Cell(Integer.parseInt(cords[0]),
                            Integer.parseInt(cords[1]));

                cells.add(cell);
            }

            String[] result = data1[1].split(",");

            char operator = result[0].charAt(0);
            int target = Integer.parseInt(result[1]);
            
            addGroup(cells, operator, target, i);
        }    
    }

    private void addGroup(List<Cell> cells, char operator, int target, int colorIndex) {
        Group group = new Group(cells, operator, target);
        groups.add(group);
        solver.addGroup(cells, operator, target);
        
        // Generate a new distinct color if needed
        while (groupColors.size() <= colorIndex) {
            groupColors.add(generateDistinctColor(groupColors));
        }
        
        // Assign color to each cell in the group
        for (Cell cell : cells) {
            groupColorMap.put(cell, colorIndex);
        }
    }

    private void initializeGUI() {
        setTitle("CSP Puzzle Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create grid cells
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                JPanel cellPanel = createCellPanel(i, j);
                gridPanel.add(cellPanel);
            }
        }
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(gridPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        JButton solveButton = new JButton("Solve Puzzle");
        solveButton.addActionListener(e -> solvePuzzle());
        buttonPanel.add(solveButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createCellPanel(int row, int col) {
        JPanel cellPanel = new JPanel(new BorderLayout());
        cellPanel.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        cellPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // Set background color based on group
        Cell cell = new Cell(row, col);
        Integer colorIndex = groupColorMap.get(cell);
        if (colorIndex != null) {
            cellPanel.setBackground(groupColors.get(colorIndex));
        } else {
            cellPanel.setBackground(Color.WHITE);
        }
        
        // Add operator and target for first cell in each group
        for (Group group : groups) {
            if (group.cells.get(0).equals(cell)) {
                JLabel operatorLabel = new JLabel(group.operator + " " + group.target);
                operatorLabel.setFont(new Font("Arial", Font.BOLD, 12));
                cellPanel.add(operatorLabel, BorderLayout.NORTH);
                break;
            }
        }
        
        return cellPanel;
    }

    private void solvePuzzle() {
        if (solver.solvePuzzle()) {
            updateGrid();
        } else {
            JOptionPane.showMessageDialog(this, "No solution exists!", "Solution Not Found", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateGrid() {
        gridPanel.removeAll();
        
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                JPanel cellPanel = createCellPanel(i, j);
                
                // Add solved number
                int value = solver.getValueAt(i, j);
                if (value != 0) {
                    JLabel numberLabel = new JLabel(String.valueOf(value));
                    numberLabel.setFont(new Font("Arial", Font.BOLD, 20));
                    numberLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    cellPanel.add(numberLabel, BorderLayout.CENTER);
                }
                
                gridPanel.add(cellPanel);
            }
        }
        
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CSPPuzzleGUI gui = new CSPPuzzleGUI();
            gui.setVisible(true);
        });
    }
}
