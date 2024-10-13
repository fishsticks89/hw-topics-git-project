import java.util.ArrayList;

public class Print {

    static ArrayList<String> stack = new ArrayList<>();

    public static void push(String name) {

        System.out.println("<" + name + ">");

        stack.add(name);

    }

    public static void print(Object o) {

        for (int index = 0; index < stack.size(); index++)

            System.out.print("  ");

        System.out.println(o);

    }

    public static void pop() {

        System.out.println("<" + stack.remove(stack.size() - 1) + "/>");

    }

}
