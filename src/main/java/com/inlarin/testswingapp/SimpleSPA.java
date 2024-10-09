package com.inlarin.testswingapp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;


/**
 * A simple Swing-based Single Page Application (SPA) that allows users to generate and sort a list of random numbers.
 * The application provides an intro screen where the user specifies how many numbers to generate,
 * and a sorting screen that displays buttons representing those numbers, allowing the user to sort or reset them.
 */
public class SimpleSPA extends JFrame {


    /**
     * Width of each number button element.
     */
    private static final int EL_WIDTH = 60;

    /**
     * Height of each number button element.
     */
    private static final int EL_HEIGHT = 25;

    /**
     * Gap between elements (used for spacing).
     */
    private static final int GAP = 5;

    /**
     * Width of the application window.
     */
    private static final int WIDTH = 1000;

    /**
     * Height of the application window.
     */
    private static final int HEIGHT = 600;

    /**
     * Border padding around the main content inside the window.
     */
    private static final int BORDER = 100;

    /**
     * Width of the buttons (e.g., Sort, Reset) on the right side of the application.
     */
    private static final int BUTTON_WIDTH = 100;

    /**
     * Maximum number of elements (number buttons) that can be displayed in a single column.
     */
    private static final int MAX_ELEMENTS_IN_COL = 10;

    /**
     * Maximum number of columns to display in the numbers panel.
     */
    private static final int MAX_NUMBER_OF_COLS = 10;

    /**
     * Layout manager that switches between panels, simulating a single-page application.
     */
    private final CardLayout cardLayout;

    /**
     * Main panel containing all other panels (e.g., intro and sorting panels).
     */
    private final JPanel mainPanel;

    /**
     * List of buttons representing the numbers displayed in the sorting panel.
     */
    private final List<JButton> numberButtons = new ArrayList<>();

    /**
     * Panel that contains the sorting functionality (buttons and numbers).
     */
    private JPanel sortPanel;

    /**
     * Scroll pane that holds the panel displaying the number buttons.
     */
    private JScrollPane numbersScrollPanel;

    /**
     * Button used to trigger the sorting of numbers.
     */
    private JButton sortButton;

    /**
     * Flag indicating whether the numbers should be sorted in descending order (true) or ascending order (false).
     */
    private boolean descendingOrder = true;

    /**
     * The number of elements (number buttons) to be displayed, determined by user input.
     */
    private int elementsCount;


    /**
     * Constructs the main frame of the application with an intro panel and a sorting panel.
     * It sets the window title, size, and initializes components for number generation and sorting.
     */
    public SimpleSPA() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel introPanel = createIntroPanel();
        sortPanel = createSortPanel();

        mainPanel.add(introPanel, "Intro");
        mainPanel.add(sortPanel, "Sort");

        setTitle("Single Page Application");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * The main method, which launches the application using SwingUtilities to ensure
     * that the UI updates are handled on the Event Dispatch Thread.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimpleSPA::new);
    }

    /**
     * Creates the intro panel where the user specifies the number of random numbers to generate.
     *
     * @return JPanel containing the input field and button to proceed to the sorting panel.
     */
    private JPanel createIntroPanel() {
        JPanel panel = new JPanel(new GridBagLayout());

        JLabel promptLabel = new JLabel("How many numbers to display?");
        JTextField inputField = new JTextField(MAX_NUMBER_OF_COLS);

        JButton enterButton = new JButton("Enter");
        enterButton.setBackground(Color.BLUE);
        enterButton.setForeground(Color.WHITE);
        enterButton.setFocusPainted(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(GAP, GAP, GAP, GAP);

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(promptLabel, gbc);

        gbc.gridy = 1;
        panel.add(inputField, gbc);

        gbc.gridy = 2;
        panel.add(enterButton, gbc);

        enterButton.addActionListener(e -> {
            String input = inputField.getText();
            int count;
            try {
                count = Integer.parseInt(input.trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
                return;
            }

            if (count < 1 || count > 1000) {
                JOptionPane.showMessageDialog(null, "Please enter a number between 1 and 1000.");
                return;
            }

            elementsCount = count;
            initNumbersPanel(generateRandomNumbers(elementsCount));
            cardLayout.show(mainPanel, "Sort");
        });

        return panel;
    }

    /**
     * Creates the sorting panel where the randomly generated numbers are displayed as buttons.
     * The panel includes buttons for sorting the numbers and resetting the application.
     *
     * @return JPanel containing the sorting interface.
     */
    private JPanel createSortPanel() {
        sortPanel = new JPanel(new BorderLayout());
        sortPanel.setBorder(new EmptyBorder(BORDER, BORDER, BORDER, BORDER));

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setPreferredSize(new Dimension(BUTTON_WIDTH, (EL_HEIGHT) * MAX_NUMBER_OF_COLS));
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

        JButton resetButton = new JButton("Reset");
        resetButton.setForeground(Color.WHITE);
        resetButton.setBackground(Color.GREEN);
        resetButton.setMaximumSize(new Dimension(BUTTON_WIDTH, EL_HEIGHT));

        sortButton = new JButton("Sort");
        sortButton.setForeground(Color.WHITE);
        sortButton.setBackground(Color.GREEN);
        sortButton.setMaximumSize(new Dimension(BUTTON_WIDTH, EL_HEIGHT));

        buttonsPanel.add(sortButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, GAP)));
        buttonsPanel.add(resetButton);

        sortButton.addActionListener(new SortAction());

        resetButton.addActionListener(e -> {
            descendingOrder = true;
            numberButtons.clear();
            cardLayout.show(mainPanel, "Intro");
        });

        sortPanel.add(buttonsPanel, BorderLayout.EAST);

        return sortPanel;
    }

    /**
     * Generates an array of random numbers based on the specified count.
     * Ensures that at least one number is less than or equal to 30.
     *
     * @param count the number of random numbers to generate
     * @return an array of random integers
     */
    private int[] generateRandomNumbers(int count) {
        Random rand = new Random();
        int[] numbers = new int[count];
        boolean hasLowNumber = false;

        for (int i = 0; i < count; i++) {
            numbers[i] = rand.nextInt(1000) + 1;
            if (numbers[i] <= 30) {
                hasLowNumber = true;
            }
        }

        if (!hasLowNumber) {
            numbers[rand.nextInt(count)] = rand.nextInt(30) + 1;
        }

        return numbers;
    }

    /**
     * Initializes the panel that displays the generated random numbers as buttons.
     *
     * @param numbers the array of numbers to display
     */
    private void initNumbersPanel(int[] numbers) {
        JPanel numbersPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(GAP, 0, GAP, GAP);
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        numbersPanel.removeAll();
        for (int num : numbers) {
            JButton numButton = getButton(num);
            numberButtons.add(numButton);

            numbersPanel.add(numButton, gbc);
            gbc.gridy++;
            if (gbc.gridy == MAX_ELEMENTS_IN_COL) {
                gbc.gridy = 0;
                gbc.gridx++;
            }
        }

        if(numbersScrollPanel == null) {
            numbersScrollPanel = new JScrollPane(numbersPanel);
            numbersScrollPanel.setBorder(null);

            sortPanel.add(numbersScrollPanel, BorderLayout.WEST);
        }
        updateJScrollPane(numbersPanel);

        sortPanel.revalidate();
        sortPanel.repaint();
    }

    /**
     * Creates a button for a specific number. If the number is less than or equal to 30,
     * clicking the button will generate a new set of random numbers.
     *
     * @param num the number to be displayed on the button
     * @return a JButton representing the number
     */
    private JButton getButton(int num) {
        JButton numButton = new JButton(String.valueOf(num));
        numButton.setBackground(Color.BLUE);
        numButton.setForeground(Color.WHITE);
        numButton.setPreferredSize(new Dimension(EL_WIDTH, EL_HEIGHT));
        numButton.addActionListener(e -> {
            if (Integer.parseInt(numButton.getText()) <= 30) {
                numberButtons.clear();
                initNumbersPanel(generateRandomNumbers(elementsCount));
            } else {
                JOptionPane.showMessageDialog(null, "Please select a value smaller or equal to 30.");
            }
        });
        return numButton;
    }

    /**
     * Initializes the scroll pane for displaying the number buttons.
     *
     * @param jPanel the panel containing the number buttons
     */
    private void updateJScrollPane(JPanel jPanel) {
        numbersScrollPanel.setViewportView(jPanel);
        int countOfCols = (int) Math.ceil(numberButtons.size() / 10.0);
        numbersScrollPanel.setHorizontalScrollBarPolicy(countOfCols > MAX_NUMBER_OF_COLS ? JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS : JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        numbersScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        countOfCols = Math.min(countOfCols, MAX_NUMBER_OF_COLS);
        numbersScrollPanel.setPreferredSize(new Dimension((EL_WIDTH + GAP)* countOfCols, (EL_HEIGHT + GAP * 2) * MAX_ELEMENTS_IN_COL));
        numbersScrollPanel.getHorizontalScrollBar().setPreferredSize(new Dimension(EL_WIDTH, 5));
    }

    /**
     * ActionListener implementation that handles the sorting of number buttons when the "Sort" button is clicked.
     * The numbers can be sorted in ascending or descending order depending on the current state.
     */
    private class SortAction implements ActionListener {

        /**
         * Handles the action when the "Sort" button is clicked. It toggles the sorting order (ascending/descending),
         * disables the "Sort" button, and starts a new thread to perform the sorting operation using quicksort.
         *
         * @param e the event triggered when the "Sort" button is clicked.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            descendingOrder = !descendingOrder;
            sortButton.setEnabled(false);
            int[] arr = new int[numberButtons.size()];

            IntStream.range(0, numberButtons.size())
                    .forEach(i -> arr[i] = Integer.parseInt(numberButtons.get(i).getText()));

            new Thread(() -> {
                quickSort(arr, 0, arr.length - 1);
                sortButton.setEnabled(true);
            }).start();
        }

        /**
         * Performs a non-recursive quicksort on the given array using an explicit stack to manage subarray bounds.
         *
         * @param arr  the array of integers to be sorted
         * @param low  the starting index of the subarray to be sorted
         * @param high the ending index of the subarray to be sorted
         */
        private void quickSort(int[] arr, int low, int high) {
            if (arr == null || arr.length == 0 || low >= high)
                return;

            Stack<Integer> stack = new Stack<>();
            stack.push(low);
            stack.push(high);

            while (!stack.isEmpty()) {
                high = stack.pop();
                low = stack.pop();

                int pivotIndex = partition(arr, low, high);

                if (pivotIndex - 1 > low) {
                    stack.push(low);
                    stack.push(pivotIndex - 1);
                }

                if (pivotIndex + 1 < high) {
                    stack.push(pivotIndex + 1);
                    stack.push(high);
                }
            }
        }

        /**
         * Partitions the array around a pivot element, sorting elements so that all values smaller (or larger,
         * depending on the order) than the pivot are on the left and all larger (or smaller) values are on the right.
         *
         * @param arr   the array to be partitioned
         * @param low   the starting index of the subarray
         * @param high  the ending index of the subarray
         * @return the index of the pivot element after partitioning
         */
        private int partition(int[] arr, int low, int high) {
            int middle = low + (high - low) / 2;
            int pivot = arr[middle];

            swap(arr, middle, high);

            int i = low;
            for (int j = low; j < high; j++) {
                boolean condition = descendingOrder ? arr[j] <= pivot : arr[j] >= pivot;
                if (condition) {
                    swap(arr, i, j);
                    i++;
                }
            }

            swap(arr, i, high);

            return i;
        }

        /**
         * Swaps two elements in the array and updates the corresponding buttons to reflect the change.
         *
         * @param arr    the array in which to swap elements
         * @param index1 the index of the first element
         * @param index2 the index of the second element
         */
        private void swap(int[] arr, int index1, int index2) {
            int temp = arr[index1];
            arr[index1] = arr[index2];
            arr[index2] = temp;

            swapButtons(index1, index2);
        }

        /**
         * Swaps the text of two number buttons, reflecting the swap in the underlying array.
         *
         * @param i the index of the first button
         * @param j the index of the second button
         */
        private void swapButtons(int i, int j) {
            String tempText = numberButtons.get(i).getText();
            numberButtons.get(i).setText(numberButtons.get(j).getText());
            numberButtons.get(j).setText(tempText);
        }
    }

}
