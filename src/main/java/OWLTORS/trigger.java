package OWLTORS;

import com.thesis.rdbtoowl.interfaces.*;
import com.thesis.rdbtoowl.tasks.CreateSchemaJENA;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by abhi.pandey on 4/19/14.
 */
public class trigger {
    public static void main(String args[]){
        System.out.print("Starting Conversion\n");
        JTextArea console = new JTextArea();
        ArrayList<com.thesis.rdbtoowl.interfaces.Table> tables = new CreateSchemaJENA(console).getTables();
    }

}
