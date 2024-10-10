package com.inlarin.testswingapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.JTextField;
import javax.swing.JButton;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for the SimpleSPA class.
 * This class contains tests for the functionalities of the SimpleSPA,
 * including random number generation, sorting functionality, and input validation.
 * It's also better to use private modifier for methods, but not in the one-class application.
 */
class SimpleSPATest {

    /**
     * Variable that contains application logic
     */
    private SimpleSPA spa;

    /**
     * Buttons with values less than this element can reset array and with values bigger than this should throw error message.
     */
    private static final int ARRAY_SPECIFIC_ELEMENT = 30;

    /**
     * The minimum button value that can be generated
     */
    private static final int ARRAY_MIN_NUMBER = 1;

    /**
     * The maximum button value that can be generated
     */
    private static final int ARRAY_MAX_NUMBER = 1000;


    /**
     * Initializes the SimpleSPA instance before each test.
     */
    @BeforeEach
    void setUp() {
        spa = new SimpleSPA();
    }

    /**
     * Tests the generation of random numbers with a valid count.
     * Ensures the generated array length matches the specified count,
     * all numbers are within the range of 1 to 1000,
     * and at least one number is less than or equal to 30.
     */
    @Test
    void testGenerateRandomNumbers_ValidCount() {
        int count = 10;
        int[] numbers = spa.generateRandomNumbers(count);

        assertEquals(count, numbers.length, "Generated numbers length should match the count");
        assertTrue(IntStream.of(numbers).allMatch(num -> num >= ARRAY_MIN_NUMBER && num <= ARRAY_MAX_NUMBER), "All numbers should be between 1 and 1000");
        assertTrue(IntStream.of(numbers).anyMatch(num -> num <= ARRAY_SPECIFIC_ELEMENT), "At least one number should be <= 30");
    }

    /**
     * Tests that generated random numbers include at least one number
     * that is less than or equal to 30.
     */
    @Test
    void testGenerateRandomNumbers_EnsuresLowNumber() {
        int count = 5;
        int[] numbers = spa.generateRandomNumbers(count);
        assertTrue(IntStream.of(numbers).anyMatch(num -> num <= ARRAY_SPECIFIC_ELEMENT), "Generated numbers should include at least one <= 30");
    }

    /**
     * Tests the sorting action of the number buttons in the SimpleSPA.
     * It verifies both ascending and descending order sorting of numbers.
     */
    @Test
    void testSortAction() {
        int[] inputNumbers = {5, 2, 9, 1, 7};
        spa.initNumbersPanel(inputNumbers);
        spa.setDescendingOrder(false);
        spa.getSortButton().doClick();

        await().atMost(3, TimeUnit.SECONDS).until(() ->
                Arrays.equals(new Integer[]{1, 2, 5, 7, 9},
                        spa.getNumberButtons().stream()
                                .map(jbutton -> Integer.parseInt(jbutton.getText()))
                                .toArray(Integer[]::new))
        );

        spa.setDescendingOrder(true);
        spa.getSortButton().doClick();
        await().atMost(3, TimeUnit.SECONDS).until(() ->
                Arrays.equals(new Integer[]{9, 7, 5, 2, 1},
                        spa.getNumberButtons().stream()
                                .map(jbutton -> Integer.parseInt(jbutton.getText()))
                                .toArray(Integer[]::new))
        );
    }

    /**
     * Tests the handling of invalid input in the SimpleSPA.
     * Verifies that entering non-numeric input results a null elements count.
     */
    @Test
    void testInvalidInputHandling() {
        JTextField inputField = new JTextField();
        inputField.setText("abc");
        spa.createIntroPanel();
        JButton enterButton = spa.getIntroButton();
        enterButton.doClick();

        assertNull(spa.getElementsCount());
    }

    /**
     * Tests the handling of valid input in the SimpleSPA.
     * Verifies that entering a valid number updates the elements count correctly.
     */
    @Test
    void testValidInput() {
        Integer value = 1000;
        spa.createIntroPanel();
        spa.getElementsCountInput().setText(value.toString());
        JButton enterButton = spa.getIntroButton();

        enterButton.doClick();

        assertEquals(value, spa.getElementsCount());
    }
}
