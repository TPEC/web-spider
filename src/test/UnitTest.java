package test;

import org.junit.jupiter.api.Test;
import src.utils.command.CommandObserver;
import src.utils.command.CommandUtil;

public class UnitTest {
    @Test
    public void CommandTest() {
        CommandUtil commandUtil = new CommandUtil();
        commandUtil.register("a", Integer.class, (CommandObserver<Integer>) i -> {
            System.out.println(i * 100);
        });
        commandUtil.register("b", String.class, (CommandObserver<String>) s -> {
            System.out.println(s + "asdf");
        });

        commandUtil.doCmd(new String[]{"-a", "123", "-b", "asdf"});
    }
}
