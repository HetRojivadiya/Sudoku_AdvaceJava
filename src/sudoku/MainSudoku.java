package sudoku;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class MainSudoku extends javax.swing.JFrame {

    private JButton[][] buttons;
    private int[][] sudokuSolution;
    private int[][] userGrid;
    private int LifeLine;
    private JButton selectedButton = null;



    private int selectedNumber = -1; // The number selected by the user

    public MainSudoku() {
        initComponents();
        
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("2-removebg-preview.png")));
        
        LifeLine=Integer.parseInt(lifeline.getText());

        // Generate the Sudoku solution
        sudokuSolution = generateSudokuSolution();

        // Initialize user grid with empty cells
        userGrid = new int[9][9];
        
        populateInitialNumbers();

        buttons = new JButton[9][9];
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                buttons[row][col] = new JButton();
                buttons[row][col].setFont(new java.awt.Font("Segoe UI", 1, 15));
                buttons[row][col].setBackground(Color.white);
                buttons[row][col].setFocusPainted(false); // Remove focus border
                buttons[row][col].addActionListener(new ButtonClickListener(row, col));
                table.add(buttons[row][col]);
                buttons[row][col].setBounds(47 + col * 49, 20 + row * 47, 43, 43);
            }
        }

        updateButtonValues();
        
    }

 private class ButtonClickListener implements ActionListener {
    private int row, col;

    public ButtonClickListener(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (selectedNumber != -1 && userGrid[row][col]==0 || buttons[row][col].getForeground()==Color.BLUE) {
            JButton clickedButton = (JButton) e.getSource();
                
            if (isValidAssignment(row, col, selectedNumber)) {
                userGrid[row][col] = selectedNumber;
                
                selectedButton = clickedButton;
                selectedButton.setForeground(Color.blue); // Set color of the current button to blue
                updateButtonValues();

                if (isSolved()) {
                    congrats c =  new congrats();
                    c.setVisible(true);
                    Timer timer = new Timer(3000,(s) -> {
                          dispose();
                          StartGame m = new StartGame();
                          m.setVisible(true);
                          dispose();
                    });
                    timer.setRepeats(false); 
                    timer.start();
                }
            } else {
                // Show an alert indicating invalid assignment
                JOptionPane.showMessageDialog(MainSudoku.this, "Invalid assignment!", "Alert", JOptionPane.WARNING_MESSAGE);
                LifeLine--;

                if (LifeLine == -1) {
                    JOptionPane.showMessageDialog(MainSudoku.this, "Game Over!", "Alert", JOptionPane.WARNING_MESSAGE);
                    StartGame s = new StartGame();
                    s.setVisible(true);
                    dispose();
                }
                lifeline.setText(String.valueOf(LifeLine));
            }
        }
    }
}

    private boolean isSolved() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (userGrid[row][col] == 0) {
                    return false; // There's an empty cell, puzzle is not solved
                }
            }
        }
        return true; // All cells are filled, puzzle is solved
    }

private boolean isValidAssignment(int row, int col, int num) {
    return isValidInRow(row, num) && isValidInColumn(col, num) && isValidInSubgrid(row, col, num);
}

private boolean isValidInRow(int row, int num) {
    for (int col = 0; col < 9; col++) {
        if (userGrid[row][col] == num) {
            return false;
        }
    }
    return true;
}

private boolean isValidInColumn(int col, int num) {
    for (int row = 0; row < 9; row++) {
        if (userGrid[row][col] == num) {
            return false;
        }
    }
    return true;
}

private boolean isValidInSubgrid(int row, int col, int num) {
    int startRow = row - row % 3;
    int startCol = col - col % 3;
    for (int i = startRow; i < startRow + 3; i++) {
        for (int j = startCol; j < startCol + 3; j++) {
            if (userGrid[i][j] == num) {
                return false;
            }
        }
    }
    return true;
}

    private void updateButtonValues() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (userGrid[row][col] != 0) {
                    buttons[row][col].setText(Integer.toString(userGrid[row][col]));
                    
                } else {
                    buttons[row][col].setText("");
                }
            }
        }
    }

    
    private void populateInitialNumbers() {
        // Copy some initial numbers from the solution to the user grid
        int numInitialNumbers = 30; // You can adjust the number of initial numbers
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < 81; i++) {
            indexes.add(i);
        }
        Collections.shuffle(indexes);
        
        for (int i = 0; i < 80; i++) {
            int index = indexes.get(i);
            int row = index / 9;
            int col = index % 9;
            userGrid[row][col] = sudokuSolution[row][col];
        }
    }
    
    
    private int[][] generateSudokuSolution() {
        int[][] grid = new int[9][9];
        long seed = System.currentTimeMillis(); // Use the current time as the seed
        Random random = new Random(seed);
        generateSudoku(grid, random, 0, 0); 
        return grid;
    }

    private boolean generateSudoku(int[][] grid, Random random, int row, int col) {
        if (row == 9 - 1 && col == 9) {
        return true;
    }

    if (col == 9) {
        row++;
        col = 0;
    }

    // Generate random numbers between 1 and 9
    Integer[] numbersArray = {1, 2, 3, 4, 5, 6, 7, 8, 9};
    List<Integer> numbers = new ArrayList<>(Arrays.asList(numbersArray));
    for (int num = 1; num <= 9; num++) {
        numbers.add(num);
    }
    Collections.shuffle(numbers, random);

    for (int num : numbers) {
        if (isSafe(grid, row, col, num)) {
            grid[row][col] = num;
            if (generateSudoku(grid, random, row, col + 1)) {
                return true;
            }
            grid[row][col] = 0; // Backtrack
        }
    }
    return false;
    }

    private boolean isSafe(int[][] grid, int row, int col, int num) {
        return !usedInRow(grid, row, num) && !usedInColumn(grid, col, num) && !usedInSubgrid(grid, row - row % 3, col - col % 3, num);
    }

    private boolean usedInRow(int[][] grid, int row, int num) {
        for (int col = 0; col < 9; col++) {
            if (grid[row][col] == num) {
                return true;
            }
        }
        return false;
    }

    private boolean usedInColumn(int[][] grid, int col, int num) {
        for (int row = 0; row < 9; row++) {
            if (grid[row][col] == num) {
                return true;
            }
        }
        return false;
    }

    private boolean usedInSubgrid(int[][] grid, int startRow, int startCol, int num) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (grid[row + startRow][col + startCol] == num) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        table = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton6 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        lifeline = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(700, 600));
        setMinimumSize(new java.awt.Dimension(700, 600));
        getContentPane().setLayout(null);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(null);

        table.setLayout(null);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sudoku/table2.png"))); // NOI18N
        table.add(jLabel1);
        jLabel1.setBounds(40, 10, 460, 440);

        jPanel1.add(table);
        table.setBounds(100, 10, 520, 470);

        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jButton6.setText("1");
        jButton6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton6MouseClicked(evt);
            }
        });
        jPanel2.add(jButton6);

        jButton8.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jButton8.setText("2");
        jButton8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton8MouseClicked(evt);
            }
        });
        jPanel2.add(jButton8);

        jButton9.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jButton9.setText("3");
        jButton9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton9MouseClicked(evt);
            }
        });
        jPanel2.add(jButton9);

        jButton10.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jButton10.setText("4");
        jButton10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton10MouseClicked(evt);
            }
        });
        jPanel2.add(jButton10);

        jButton7.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jButton7.setText("5");
        jButton7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton7MouseClicked(evt);
            }
        });
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton7);

        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jButton5.setText("6");
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton5MouseClicked(evt);
            }
        });
        jPanel2.add(jButton5);

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jButton2.setText("7");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });
        jPanel2.add(jButton2);

        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jButton3.setText("8");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton3MouseClicked(evt);
            }
        });
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton3);

        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jButton4.setText("9");
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton4MouseClicked(evt);
            }
        });
        jPanel2.add(jButton4);

        jPanel1.add(jPanel2);
        jPanel2.setBounds(140, 490, 430, 50);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sudoku/heart.png"))); // NOI18N
        jPanel1.add(jLabel3);
        jLabel3.setBounds(10, 210, 50, 60);

        lifeline.setFont(new java.awt.Font("Arial", 0, 48)); // NOI18N
        lifeline.setText("3");
        jPanel1.add(lifeline);
        lifeline.setBounds(60, 210, 60, 60);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sudoku/back.png"))); // NOI18N
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel2);
        jLabel2.setBounds(0, 10, 160, 60);

        getContentPane().add(jPanel1);
        jPanel1.setBounds(0, 0, 700, 600);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton6MouseClicked
            selectedNumber = Integer.parseInt(jButton6.getText());       // TODO add your handling code here:
    }//GEN-LAST:event_jButton6MouseClicked

    private void jButton8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton8MouseClicked
            selectedNumber = Integer.parseInt(jButton8.getText());       // TODO add your handling code here:
    }//GEN-LAST:event_jButton8MouseClicked

    private void jButton9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton9MouseClicked
            selectedNumber = Integer.parseInt(jButton9.getText());        // TODO add your handling code here:
    }//GEN-LAST:event_jButton9MouseClicked

    private void jButton10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton10MouseClicked
            selectedNumber = Integer.parseInt(jButton10.getText());        // TODO add your handling code here:
    }//GEN-LAST:event_jButton10MouseClicked

    private void jButton7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton7MouseClicked
            selectedNumber = Integer.parseInt(jButton7.getText());        // TODO add your handling code here:
    }//GEN-LAST:event_jButton7MouseClicked

    private void jButton5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton5MouseClicked
            selectedNumber = Integer.parseInt(jButton5.getText());        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5MouseClicked

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        selectedNumber = Integer.parseInt(jButton2.getText());        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2MouseClicked

    private void jButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseClicked
        selectedNumber = Integer.parseInt(jButton3.getText());        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3MouseClicked

    private void jButton4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseClicked
        selectedNumber = Integer.parseInt(jButton4.getText());        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4MouseClicked

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        StartGame s = new StartGame();
        s.setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel2MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set ttableus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setableode (optiontable        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainSudoku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainSudoku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainSudoku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainSudoku.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainSudoku().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lifeline;
    private javax.swing.JPanel table;
    // End of variables declaration//GEN-END:variables
}
