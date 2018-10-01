/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rautosave;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 *
 * @author ADMINIBM
 */
public class RAutosave {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
          JFrame frame = new JFrame();
          frame.setVisible(true);
          frame.setLocation(dim.width/2-new JFrame().getSize().width/2, dim.height/2-new JFrame().getSize().height/2);
    }
    
}
