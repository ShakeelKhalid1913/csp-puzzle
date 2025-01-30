import java.util.Objects;

public class Arc {
    Cell cell1, cell2;
    
    Arc(Cell cell1, Cell cell2) {
        this.cell1 = cell1;
        this.cell2 = cell2;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Arc)) return false;
        Arc arc = (Arc) o;
        return cell1.equals(arc.cell1) && cell2.equals(arc.cell2);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(cell1, cell2);
    }
}
