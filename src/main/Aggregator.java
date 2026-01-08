package main;

import model.MuseStrategy;
import model.Model;
import model.Provider;
//import view.HtmlView;
import view.SwingView;

import javax.swing.*;

public class Aggregator {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SwingView view = new SwingView();

            Model model = new Model(view, new Provider(new MuseStrategy()));
            Controller controller = new Controller(model);

            view.setController(controller);

            view.setVisible(true);
        });
        
    }
}