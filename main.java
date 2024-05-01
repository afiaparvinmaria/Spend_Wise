import java.util.Scanner;

class Expense {
    private String description;
    private double amount;

    public Expense(String description, double amount) {
        this.description = description;
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Description: " + description + ", Amount: $" + amount;
    }
}

class ExpenseTracker {
    private Expense[] expenses;
    private int size;
    private static final int MAX_EXPENSES = 100;

    public ExpenseTracker() {
        expenses = new Expense[MAX_EXPENSES];
        size = 0;
    }

    public void logExpense(String description, double amount) {
        if (size < MAX_EXPENSES) {
            expenses[size++] = new Expense(description, amount);
            System.out.println("Expense logged successfully.");
        } else {
            System.out.println("Cannot log expense. Expense tracker is full.");
        }
    }

    public void viewExpenses() {
        if (size == 0) {
            System.out.println("No expenses logged yet.");
        } else {
            System.out.println("All Expenses:");
            for (int i = 0; i < size; i++) {
                System.out.println(expenses[i]);
            }
        }
    }

    public void viewTotalExpense() {
        double total = 0;
        for (int i = 0; i < size; i++) {
            total += expenses[i].getAmount();
        }
        System.out.println("Total Expense: $" + total);
    }
}

public class main {
    public static void main(String[] args) {
        ExpenseTracker tracker = new ExpenseTracker();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nExpense Tracker Menu:");
            System.out.println("1. Log Expense");
            System.out.println("2. View Expenses");
            System.out.println("3. View Total Expense");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter expense description: ");
                    String description = scanner.nextLine();
                    System.out.print("Enter expense amount: $");
                    double amount = scanner.nextDouble();
                    tracker.logExpense(description, amount);
                    break;
                case 2:
                    tracker.viewExpenses();
                    break;
                case 3:
                    tracker.viewTotalExpense();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 4.");
            }
        }
    }
}
