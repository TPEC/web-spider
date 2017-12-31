package src.utils.command;

import java.util.HashMap;
import java.util.Map;

public class CommandUtil {
    private char prefix = '-';
    private final Map<String, Arg> argTypes = new HashMap<>();
    private CommandObserver<String> errorObserver;

    public void register(String name, CommandObserver<String> observer) {
        register(name, observer, "");
    }

    public void register(String name, CommandObserver<String> observer, String usage) {
        register(name, String.class, observer, usage);
    }

    public void register(String name, Class<?> type, CommandObserver<?> observer) {
        register(name, type, observer, "");
    }

    public void register(String name, Class<?> type, CommandObserver<?> observer, String usage) {
        if (type == null) {
            type = NoneArgs.class;
        }
        argTypes.put(name, new Arg(type, observer, usage));
    }

    public void registerError(CommandObserver<String> observer) {
        this.errorObserver = observer;
    }

    public void printUsage() {
        for (Map.Entry<String, Arg> e : argTypes.entrySet()) {
            System.out.println("-" + e.getKey() + "\t" + e.getValue().usage);
        }
    }

    public void unregister(String name) {
        argTypes.remove(name);
    }

    public boolean doCmd(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String c = args[i];
            if (c != null && !c.isEmpty()) {
                if (c.charAt(0) == prefix) {
                    c = c.substring(1);
                    if (argTypes.containsKey(c)) {
                        Class<?> type = argTypes.get(c).type;
                        CommandObserver co = argTypes.get(c).observer;
                        if (type.isAssignableFrom(NoneArgs.class)) {
                            co.command(null);
                        } else {
                            i++;
                            String arg = args[i];
                            if (type.isAssignableFrom(Integer.class)) {
                                co.command(Integer.valueOf(arg));
                            } else if (type.isAssignableFrom(Long.class)) {
                                co.command(Long.valueOf(arg));
                            } else if (type.isAssignableFrom(Float.class)) {
                                co.command(Float.valueOf(arg));
                            } else if (type.isAssignableFrom(Double.class)) {
                                co.command(Double.valueOf(arg));
                            } else if (type.isAssignableFrom(String.class)) {
                                co.command(arg);
                            } else if (type.isAssignableFrom(Boolean.class)) {
                                co.command(Boolean.valueOf(arg));
                            } else {
                                co.command(arg);
                            }
                        }
                    } else {
                        if (errorObserver != null) {
                            errorObserver.command("No such command: " + c + ".");
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    class Arg {
        Class<?> type;
        CommandObserver<?> observer;
        String usage;

        public Arg(Class<?> type, CommandObserver<?> observer, String usage) {
            this.type = type;
            this.observer = observer;
            this.usage = usage;
        }
    }

}
