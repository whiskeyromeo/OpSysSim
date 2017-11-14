package User_space;

import javax.swing.*;
import java.awt.*;

/**
 * @author Will Russell on 11/8/17
 * @project OS_Simulator
 */
public class GUI extends JFrame {

    String[] columnNames = {"Process", "Size", "Arrival Time"
            , "Run Time", "Status", "Command"};

    Object[][] data = {
            {"testProcess", "15", "0", "55", "Running", "Exec"},
            {"secondProcess", "20", "15", "25", "Waiting", "Wait"}
    };


    public GUI()
    {
        setLayout(new FlowLayout());

        JTable table = new JTable(data, columnNames);

        table.setPreferredScrollableViewportSize(new Dimension(500, 50));
        table.setFillsViewportHeight(true);

        JScrollPane tableScrollPane = new JScrollPane(table);

        add(tableScrollPane);
    }

    public int getRowCount() {
        return data.length;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public Object getValueAt(int row, int column) {
        return data[row][column];
    }

    public String getColumnName(int column) {
        return columnNames[column];

    }

}
