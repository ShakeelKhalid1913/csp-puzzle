import java.util.*;

public class CSPPuzzleSolver {
    private int N; // Grid size
    private int[][] grid; //N by N 
    private List<Group> groups; // total groups 
    private Map<Cell, Set<Integer>> domains; //csp
    
    public CSPPuzzleSolver(int N) {
        this.N = N;
        this.grid = new int[N][N];
        this.groups = new ArrayList<>();
        this.domains = new HashMap<>();
    }

    public int getN() {
        return N;
    }
    
    public void addGroup(List<Cell> cells, char operator, int target) {
        groups.add(new Group(cells, operator, target));
    }
    
    private void initializeDomains() {
        // Initialize domains for all cells with values 1 to N
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                Cell cell = new Cell(i, j);
                Set<Integer> domain = new HashSet<>();
                for (int k = 1; k <= N; k++) {
                    domain.add(k);
                }
                domains.put(cell, domain);
            }
        }
    }
    
    private boolean isValid(Cell cell, int value) {
        // Check row and column constraints
        for (int i = 0; i < N; i++) {
            //each row and column should have a unique value.
            if (grid[cell.row][i] == value && i != cell.col) return false;
            if (grid[i][cell.col] == value && i != cell.row) return false;
        }
        
        // Check group constraints
        for (Group group : groups) {
            if (group.cells.contains(cell) && group.operator == '+') {
                if (!checkAdditionConstraint(group, cell, value)) {
                    return false;
                }
            }
            else if (group.cells.contains(cell) && group.operator == '*') {
                if (!checkMultiplicationConstraint(group, cell, value)) {
                    return false;
                }
            }
            // divide
            else if (group.cells.contains(cell) && group.operator == '/') {
                if (!checkDivisionConstraint(group, cell, value)) {
                    return false;
                }
            }
            // subtract
            else if (group.cells.contains(cell) && group.operator == '-') {
                if (!checkSubtractionConstraint(group, cell, value)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private boolean checkAdditionConstraint(Group group, Cell currentCell, int value) {
        int sum = 0;
        boolean allFilled = true;
        
        // Calculate sum of filled cells
        for (Cell cell : group.cells) {
            if (cell.equals(currentCell)) {
                sum += value;
            } else {
                int cellValue = grid[cell.row][cell.col];
                if (cellValue == 0) {
                    allFilled = false;
                }
                sum += cellValue;
            }
        }
        
        // If all cells are filled (including the current one), check if sum equals target
        if (allFilled) {
            return sum == group.target;
        }
        
        // If not all cells are filled, check if current sum is less than target
        return sum <= group.target;
    }

    private boolean checkMultiplicationConstraint(Group group, Cell currentCell, int value) {
        int product = 1;
        boolean allFilled = true;
        
        // Calculate product of filled cells
        for (Cell cell : group.cells) {
            if (cell.equals(currentCell)) {
                product *= value;
            } else {
                int cellValue = grid[cell.row][cell.col];
                if (cellValue == 0) {
                    allFilled = false;
                } else {
                    product *= cellValue;
                }
            }
        }
        
        // If all cells are filled (including the current one), check if product equals target
        if (allFilled) {
            return product == group.target;
        }
        
        // If not all cells are filled, check if current product is a factor of target
        return group.target % product == 0;
    }

    private boolean checkDivisionConstraint(Group group, Cell currentCell, int value) {
        // Division groups should only have 2 cells
        if (group.cells.size() != 2) {
            return false;
        }

        Cell otherCell = group.cells.get(0).equals(currentCell) ? group.cells.get(1) : group.cells.get(0);
        int otherValue = grid[otherCell.row][otherCell.col];

        // If other cell is not filled yet
        if (otherValue == 0) {
            return true; // Allow this value for now
        }

        // Try both orderings (larger/smaller) since division is not commutative
        int larger = Math.max(value, otherValue);
        int smaller = Math.min(value, otherValue);

        // Check if larger divided by smaller equals target
        // Also ensure no division by zero
        return smaller != 0 && larger / smaller == group.target && larger % smaller == 0;
    }

    private boolean checkSubtractionConstraint(Group group, Cell currentCell, int value) {
        // Subtraction groups should only have 2 cells
        if (group.cells.size() != 2) {
            return false;
        }

        Cell otherCell = group.cells.get(0).equals(currentCell) ? group.cells.get(1) : group.cells.get(0);
        int otherValue = grid[otherCell.row][otherCell.col];

        // If other cell is not filled yet
        if (otherValue == 0) {
            return true; // Allow this value for now
        }

        // Try both orderings since we want the absolute difference
        // The target should be the absolute difference between the two numbers
        int diff1 = Math.abs(value - otherValue);
        int diff2 = Math.abs(otherValue - value);

        // Check if either difference equals the target
        return diff1 == group.target || diff2 == group.target;
    }
    
    private Cell selectUnassignedVariable() {
        // Implementation of MRV (Minimum Remaining Values) heuristic
        Cell selectedCell = null;
        int minDomainSize = Integer.MAX_VALUE;
        
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (grid[i][j] == 0) {  // unassigned cell
                    Cell cell = new Cell(i, j);
                    int domainSize = domains.get(cell).size();
                    if (domainSize < minDomainSize) {
                        minDomainSize = domainSize;
                        selectedCell = cell;
                    }
                }
            }
        }
        
        return selectedCell;
    }
    
    private boolean solve() {
        Cell cell = selectUnassignedVariable(); //mrv 
        if (cell == null) return true;  // All variables are assigned
        
        for (int value : domains.get(cell)) {
            if (isValid(cell, value)) {
                grid[cell.row][cell.col] = value;
                
                // Forward checking
                Map<Cell, Set<Integer>> savedDomains = new HashMap<>();
                if (forwardCheck(cell, value, savedDomains)) {
                    if (solve()) {
                        return true;
                    }
                }
                
                // Backtrack
                grid[cell.row][cell.col] = 0;
                restoreDomains(savedDomains);
            }
        }
        
        return false;
    }
    
    private boolean forwardCheck(Cell cell, int value, Map<Cell, Set<Integer>> savedDomains) {
        // Save and update domains of related cells (same row, column, and group)
        for (int i = 0; i < N; i++) {
            // Check row
            if (i != cell.col && grid[cell.row][i] == 0) {
                Cell neighbor = new Cell(cell.row, i);
                updateDomain(neighbor, value, savedDomains);
                if (domains.get(neighbor).isEmpty()) return false;
            }
            // Check column
            if (i != cell.row && grid[i][cell.col] == 0) {
                Cell neighbor = new Cell(i, cell.col);
                updateDomain(neighbor, value, savedDomains);
                if (domains.get(neighbor).isEmpty()) return false;
            }
        }
        return true;
    }
    
    private void updateDomain(Cell cell, int value, Map<Cell, Set<Integer>> savedDomains) {
        Set<Integer> domain = domains.get(cell);
        if (!savedDomains.containsKey(cell)) {
            savedDomains.put(cell, new HashSet<>(domain));
        }
        domain.remove(value);
    }
    
    //restore domain
    private void restoreDomains(Map<Cell, Set<Integer>> savedDomains) {
        for (Map.Entry<Cell, Set<Integer>> entry : savedDomains.entrySet()) {
            domains.put(entry.getKey(), entry.getValue());
        }
    }
    
    private boolean ac3() {
        Queue<Arc> queue = new LinkedList<>();
        
        // Add all arcs to queue
        // Row and column arcs
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                Cell cell1 = new Cell(i, j);
                // Row arcs
                for (int k = 0; k < N; k++) {
                    if (k != j) {
                        queue.add(new Arc(cell1, new Cell(i, k)));
                    }
                }
                // Column arcs
                for (int k = 0; k < N; k++) {
                    if (k != i) {
                        queue.add(new Arc(cell1, new Cell(k, j)));
                    }
                }
            }
        }
        
        // Group arcs
        for (Group group : groups) {
            for (Cell cell1 : group.cells) {
                for (Cell cell2 : group.cells) {
                    if (!cell1.equals(cell2)) {
                        queue.add(new Arc(cell1, cell2));
                    }
                }
            }
        }
        
        // Process all arcs
        while (!queue.isEmpty()) {
            Arc arc = queue.poll();
            if (removeInconsistentValues(arc)) {
                if (domains.get(arc.cell1).isEmpty()) {
                    return false;
                }
                // Add all neighboring arcs
                for (Cell neighbor : getNeighbors(arc.cell1)) {
                    if (!neighbor.equals(arc.cell2)) {
                        queue.add(new Arc(neighbor, arc.cell1));
                    }
                }
            }
        }
        return true;
    }
    
    private boolean removeInconsistentValues(Arc arc) {
        boolean removed = false;
        Set<Integer> cell1Domain = domains.get(arc.cell1);
        Set<Integer> cell2Domain = domains.get(arc.cell2);
        
        Set<Integer> valuesToRemove = new HashSet<>();
        for (int x : cell1Domain) {
            boolean foundConsistentY = false;
            for (int y : cell2Domain) {
                if (isConsistent(arc.cell1, x, arc.cell2, y)) {
                    foundConsistentY = true;
                    break;
                }
            }
            if (!foundConsistentY) {
                valuesToRemove.add(x);
                removed = true;
            }
        }
        
        cell1Domain.removeAll(valuesToRemove);
        return removed;
    }
    
    private boolean isConsistent(Cell cell1, int value1, Cell cell2, int value2) {
        // Check row/column constraints
        if (cell1.row == cell2.row || cell1.col == cell2.col) {
            return value1 != value2;
        }
        
        // Check group constraints
        for (Group group : groups) {
            if (group.cells.contains(cell1) && group.cells.contains(cell2)) {
                return checkGroupConsistency(group, cell1, value1, cell2, value2);
            }
        }
        
        return true;
    }
    
    private boolean checkGroupConsistency(Group group, Cell cell1, int value1, Cell cell2, int value2) {
        switch (group.operator) {
            case '+':
                return true; // Addition is always consistent between two values
            case '*':
                return true; // Multiplication is always consistent between two values
            case '-':
                return group.cells.size() == 2 && 
                       (Math.abs(value1 - value2) == group.target || 
                        Math.abs(value2 - value1) == group.target);
            case '/':
                if (group.cells.size() != 2) return false;
                int larger = Math.max(value1, value2);
                int smaller = Math.min(value1, value2);
                return smaller != 0 && larger / smaller == group.target && larger % smaller == 0;
            default:
                return true;
        }
    }
    
    private List<Cell> getNeighbors(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        
        // Add row neighbors
        for (int j = 0; j < N; j++) {
            if (j != cell.col) {
                neighbors.add(new Cell(cell.row, j));
            }
        }
        
        // Add column neighbors
        for (int i = 0; i < N; i++) {
            if (i != cell.row) {
                neighbors.add(new Cell(i, cell.col));
            }
        }
        
        // Add group neighbors
        for (Group group : groups) {
            if (group.cells.contains(cell)) {
                for (Cell groupCell : group.cells) {
                    if (!groupCell.equals(cell)) {
                        neighbors.add(groupCell);
                    }
                }
            }
        }
        
        return neighbors;
    }
    
    public boolean solvePuzzle() {
        initializeDomains();
        // Run AC-3 before starting the search
        if (!ac3()) {
            return false;
        }
        return solve();
    }
    
    public void printGrid() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }
    
    public int getValueAt(int row, int col) {
        return grid[row][col];
    }
}
