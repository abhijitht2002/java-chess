package com.chess.utils;

import javax.swing.*;
import java.awt.*;

public class DialogUtils {

    public static boolean showConfirmationDialog(Component parent, String message, String title){
        int response = JOptionPane.showConfirmDialog(
          parent,
          message,
          title,
          JOptionPane.YES_NO_OPTION
        );
        return response == JOptionPane.YES_OPTION;
    }
}
