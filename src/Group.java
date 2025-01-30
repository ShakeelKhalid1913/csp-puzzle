import java.util.List;

public class Group {
    List<Cell> cells; 
    char operator;
    int target;
    
    Group(List<Cell> cells, char operator, int target) {
        this.cells = cells;
        this.operator = operator;
        this.target = target;
    }
}
