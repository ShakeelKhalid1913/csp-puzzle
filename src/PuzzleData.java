import java.util.List;

public class PuzzleData {
    public static List<String> puzzle1(){
        String data = "4\n" +
            "0,0;0,1;1,0->*,24\n" + 
            "0,2;0,3->/,2\n" +
            "1,1;1,2->-,3\n" +
            "1,3;2,3->-,1\n" +
            "2,0;2,1->+,5\n" +
            "2,2;3,2;3,3->+,6\n" +
            "3,0;3,1->-,3";

        return List.of(data.split("\n"));
    }

    public static List<String> puzzle2(){
        String data = "6\n" +
            "0,0;1,0->-,4\n" +
            "0,1;1,1->-,1\n" +
            "0,2;0,3->-,3\n" +
            "0,4;0,5->/,3\n" +
            "2,0;2,1;3,0->+,7\n" +
            "1,2;1,3->-,1\n" +
            "1,4;1,5;2,4->*,150\n" +
            "2,2;2,3->-,2\n" +
            "2,5;3,5->+,5\n" +
            "3,1;3,2->-,1\n" +
            "3,3;3,4->/,3\n" +
            "4,1;4,2;5,2->*,60\n" +
            "4,3;5,3->-,4\n" +
            "4,4;5,4->-,1\n" +
            "4,5;5,5->-,3\n" +
            "5,0;5,1->/,3";

        return List.of(data.split("\n"));
    }

    public static List<String> puzzle3(){
        String data = "9\n" +
            "0,0;1,0;2,0->*,96\n" +
            "0,1;0,2->/,4\n" +
            "0,3;1,3;1,4->*,18\n" +
            "0,4;0,5->-,2\n" +
            "0,6;0,7->+,17\n" +
            "0,8;1,8;1,7->*,294\n" + 
            "1,1;1,2->*,20\n" + 
            "1,5;1,6;2,5;2,6->*,168\n" + 
            "2,1;2,2->-,5\n" + 
            "2,3;3,3->/,3\n" + 
            "2,4;3,4;3,5;3,6->*,70\n" + 
            "2,7;2,8->-,1\n" + 
            "3,0;4,0->-,2\n" + 
            "3,1;4,1->-,3\n" + 
            "3,2;4,2->/,4\n" + 
            "3,7;3,8;4,8->*,32\n" + 
            "4,3;5,3->/,2\n" + 
            "4,4;4,5;5,5->*,18\n" + 
            "4,6;4,7->-,1\n" + 
            "5,0;5,1;6,1;6,2->*,50\n" + 
            "#5,2->7\n" + 
            "5,4;6,4->/,2\n" + 
            "5,6;5,7;6,7->+,15\n" + 
            "5,8;6,8;7,8->+,14\n" + 
            "6,0;7,0->/,4\n" + 
            "#6,3->6\n" + 
            "6,5;6,6->-,2\n" + 
            "7,1;7,2;7,3->+,19\n" + 
            "7,4;8,4->-,2\n" + 
            "7,5;8,5->-,1\n" + 
            "7,6;7,7->+,6\n" + 
            "8,0;8,1->-,4\n" + 
            "8,2;8,3->-,1\n" + 
            "8,6;8,7->/,2\n" + 
            "#8,8->4";

        return List.of(data.split("\n"));
    }
}
