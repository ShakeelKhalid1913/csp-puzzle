import java.util.Objects;

public class Cell {
    int row, col;
    
    Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell cell = (Cell) o;
        return row == cell.row && col == cell.col;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
