package com.ambulance.util;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.json.JSONArray;
import org.json.JSONObject;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

public class JsonCliRenderer {

    private static final Ansi KEY_COLOR = ansi().fg(CYAN).bold();
    private static final Ansi STRING_VAL = ansi().fg(GREEN);
    private static final Ansi NUMBER_VAL = ansi().fg(YELLOW);
    private static final Ansi BOOLEAN_VAL = ansi().fg(MAGENTA);
    private static final Ansi NULL_VAL = ansi().fg(RED).a("null").reset();
    private static final Ansi BRACKET = ansi().fg(WHITE);
    private static final Ansi COMMA = ansi().fg(WHITE);
    private static final Ansi RESET = ansi().reset();

    /**
     * Parses a JSON string and prints it with pretty colours.
     * If the string is null (e.g., failed CoAP request), a red error indicator is shown.
     */
    public static void render(String jsonString) {
        AnsiConsole.systemInstall();   // safe to call multiple times

        if (jsonString == null) {
            System.out.println(ansi().fg(RED).bold().a("(request failed – no response)").reset());
            return;
        }

        jsonString = jsonString.trim();
        if (jsonString.startsWith("{")) {
            JSONObject obj = new JSONObject(jsonString);
            renderObject(obj, 0);
        } else if (jsonString.startsWith("[")) {
            JSONArray arr = new JSONArray(jsonString);
            renderArray(arr, 0);
        } else {
            System.out.println(jsonString);
        }
        System.out.println();
    }

    private static void renderObject(JSONObject obj, int indent) {
        String pad = "  ".repeat(indent);
        String innerPad = "  ".repeat(indent + 1);

        System.out.println(pad + BRACKET + "{");

        int count = 0;
        for (String key : obj.keySet()) {
            Object value = obj.get(key);
            System.out.print(innerPad + KEY_COLOR + "\"" + key + "\"" + RESET + ": ");

            if (value instanceof JSONObject) {
                renderObject((JSONObject) value, indent + 1);
            } else if (value instanceof JSONArray) {
                renderArray((JSONArray) value, indent + 1);
            } else {
                printValue(value);
                System.out.println(COMMA + ",");
            }
            count++;
        }

        System.out.println(pad + BRACKET + "}" + (indent == 0 ? "" : ","));
    }

    private static void renderArray(JSONArray arr, int indent) {
        String pad = "  ".repeat(indent);
        String innerPad = "  ".repeat(indent + 1);

        System.out.println(pad + BRACKET + "[");

        for (int i = 0; i < arr.length(); i++) {
            Object value = arr.get(i);
            System.out.print(innerPad);

            if (value instanceof JSONObject) {
                renderObject((JSONObject) value, indent + 1);
            } else if (value instanceof JSONArray) {
                renderArray((JSONArray) value, indent + 1);
            } else {
                printValue(value);
                System.out.println(COMMA + ",");
            }
        }

        System.out.println(pad + BRACKET + "]" + (indent == 0 ? "" : ","));
    }

    private static void printValue(Object value) {
        if (value == null || JSONObject.NULL.equals(value)) {
            System.out.print(NULL_VAL);
        } else if (value instanceof String) {
            System.out.print(STRING_VAL + "\"" + escape((String) value) + "\"" + RESET);
        } else if (value instanceof Number) {
            System.out.print(NUMBER_VAL + value.toString() + RESET);
        } else if (value instanceof Boolean) {
            System.out.print(BOOLEAN_VAL + value.toString() + RESET);
        } else {
            System.out.print(STRING_VAL + "\"" + value.toString() + "\"" + RESET);
        }
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}