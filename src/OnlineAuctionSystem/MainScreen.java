package OnlineAuctionSystem;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainScreen {
    private JButton Customer;
    private JButton Admin;
    private JPanel auctionPanel;
    private JFrame auctionF;

    public MainScreen() {
        auctionF = new JFrame("Online Auction System");
        auctionF.setContentPane(auctionPanel);
        auctionF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        auctionF.pack();
        auctionF.setLocationRelativeTo(null);
        auctionF.setVisible(true);

        Customer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Customer();
            }
        });

        Admin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Admin();
            }
        });
    }



}
