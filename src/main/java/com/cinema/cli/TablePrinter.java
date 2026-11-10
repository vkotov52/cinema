package com.cinema.cli;

import org.springframework.stereotype.Component;

import java.util.List;

/** Собирает таблицу из заголовков и строк. */
@Component
public class TablePrinter {

    public String render(String title, List<String> headers, List<List<String>> rows) {
        StringBuilder text = new StringBuilder("\n").append(title).append("\n");
        if (rows.isEmpty()) {
            return text.append("  нет данных").toString();
        }
        int[] widths = new int[headers.size()];
        for (int column = 0; column < widths.length; column++) {
            widths[column] = headers.get(column).length();
            for (List<String> row : rows) {
                widths[column] = Math.max(widths[column], row.get(column).length());
            }
        }
        String border = border(widths);
        text.append(border).append(line(headers, widths)).append(border);
        for (List<String> row : rows) {
            text.append(line(row, widths));
        }
        return text.append(border).toString().stripTrailing();
    }

    private String border(int[] widths) {
        StringBuilder border = new StringBuilder("+");
        for (int width : widths) {
            border.append("-".repeat(width + 2)).append("+");
        }
        return border.append("\n").toString();
    }

    private String line(List<String> cells, int[] widths) {
        StringBuilder line = new StringBuilder("|");
        for (int column = 0; column < widths.length; column++) {
            line.append(" ").append(String.format("%-" + widths[column] + "s", cells.get(column))).append(" |");
        }
        return line.append("\n").toString();
    }
}
