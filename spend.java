import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class Expense {
    private String description;
    private double amount;
    private LocalDate date;
    private String category;

    public Expense(String description, double amount, String category) {
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = LocalDate.now(); // Automatically set the current date
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Description: " + description + ", Amount: $" + amount + ", Date: " + date + ", Category: " + category;
    }
}

class ExpenseTracker {
    private List<Expense> expenses;
    private double dailyBudget;
    private double weeklyBudget;
    private double monthlyBudget;

    public ExpenseTracker() {
        expenses = new ArrayList<>();
        dailyBudget = 0;
        weeklyBudget = 0;
        monthlyBudget = 0;
    }

    public void logExpense(String description, double amount, String category) {
        expenses.add(new Expense(description, amount, category));
        System.out.println("Expense logged successfully.");
    }

    public void viewExpenses() {
        if (expenses.isEmpty()) {
            System.out.println("No expenses logged yet.");
        } else {
            System.out.println("All Expenses:");
            for (Expense expense : expenses) {
                System.out.println(expense);
            }
        }
    }

    public void viewExpensesByCategory(String category) {
        boolean found = false;
        for (Expense expense : expenses) {
            if (expense.getCategory().equalsIgnoreCase(category)) {
                System.out.println(expense);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No expenses found in the " + category + " category.");
        }
    }

    public void deleteExpense(int index) {
        if (index >= 0 && index < expenses.size()) {
            expenses.remove(index);
            System.out.println("Expense deleted successfully.");
        } else {
            System.out.println("Invalid index. Cannot delete expense.");
        }
    }

    public void editExpense(int index, String newDescription, double newAmount, String newCategory) {
        if (index >= 0 && index < expenses.size()) {
            Expense expense = expenses.get(index);
            expense.setDescription(newDescription);
            expense.setAmount(newAmount);
            // Assuming setters for date and category are available
            System.out.println("Expense edited successfully.");
        } else {
            System.out.println("Invalid index. Cannot edit expense.");
        }
    }

    public void viewTotalExpense() {
        double total = 0;
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }
        System.out.println("Total Expense: $" + total);
    }

    public void viewTotalExpenseByCategory() {
        Map<String, Double> categoryTotals = new HashMap<>();
        for (Expense expense : expenses) {
            String category = expense.getCategory();
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + expense.getAmount());
        }
        System.out.println("Total Expenses by Category:");
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            System.out.println(entry.getKey() + ": $" + entry.getValue());
        }
    }

    public void setBudget(double daily, double weekly, double monthly) {
        this.dailyBudget = daily;
        this.weeklyBudget = weekly;
        this.monthlyBudget = monthly;
        System.out.println("Budgets set successfully.");
    }

    public void viewRemainingBudget(LocalDate date) {
        double totalDaily = 0, totalWeekly = 0, totalMonthly = 0;

        for (Expense expense : expenses) {
            if (expense.getDate().isEqual(date) && expense.getCategory().equalsIgnoreCase("daily")) {
                totalDaily += expense.getAmount();
            }
            if (expense.getCategory().equalsIgnoreCase("weekly")) {
                totalWeekly += expense.getAmount();
            }
            if (expense.getCategory().equalsIgnoreCase("monthly")) {
                totalMonthly += expense.getAmount();
            }
        }

        System.out.println("Remaining Daily Budget: $" + (dailyBudget - totalDaily));
        System.out.println("Remaining Weekly Budget: $" + (weeklyBudget - totalWeekly));
        System.out.println("Remaining Monthly Budget: $" + (monthlyBudget - totalMonthly));
    }
}

public class spend {
    public static void main(String[] args) {
        ExpenseTracker tracker = new ExpenseTracker();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nExpense Tracker Menu:");
            System.out.println("1. Log Expense");
            System.out.println("2. View Expenses");
            System.out.println("3. View Expenses by Category");
            System.out.println("4. View Total Expense");
            System.out.println("5. View Total Expense by Category");
            System.out.println("6. Delete Expense");
            System.out.println("7. Edit Expense");
            System.out.println("8. Set Budget");
            System.out.println("9. View Remaining Budget");
            System.out.println("10. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter expense description: ");
                    String description = scanner.nextLine();
                    System.out.print("Enter expense amount: $");
                    double amount = scanner.nextDouble();
                    System.out.print("Enter expense category (daily, weekly, monthly): ");
                    String category = scanner.next();
                    tracker.logExpense(description, amount, category);
                    break;
                case 2:
                    tracker.viewExpenses();
                    break;
                case 3:
                    System.out.print("Enter category to search (daily, weekly, monthly): ");
                    String searchCategory = scanner.next();
                    tracker.viewExpensesByCategory(searchCategory);
                    break;
                case 4:
                    tracker.viewTotalExpense();
                    break;
                case 5:
                    tracker.viewTotalExpenseByCategory();
                    break;
                case 6:
                    tracker.viewExpenses();
                    System.out.print("Enter the index of the expense to delete: ");
                    int deleteIndex = scanner.nextInt();
                    tracker.deleteExpense(deleteIndex - 1);
                    break;
                case 7:
                    tracker.viewExpenses();
                    System.out.print("Enter the index of the expense to edit: ");
                    int editIndex = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter new description: ");
                    String newDescription = scanner.nextLine();
                    System.out.print("Enter new amount: $");
                    double newAmount = scanner.nextDouble();
                    System.out.print("Enter new category (daily, weekly, monthly): ");
                    String newCategory = scanner.next();
                    tracker.editExpense(editIndex - 1, newDescription, newAmount, newCategory);
                    break;
                case 8:
                    System.out.print("Set daily budget: $");
                    double dailyBudget = scanner.nextDouble();
                    System.out.print("Set weekly budget: $");
                    double weeklyBudget = scanner.nextDouble();
                    System.out.print("Set monthly budget: $");
                    double monthlyBudget = scanner.nextDouble();
                    tracker.setBudget(dailyBudget, weeklyBudget, monthlyBudget);
                    break;
                case 9:
                    System.out.print("Enter the date to view remaining budget (yyyy-mm-dd): ");
                    LocalDate budgetDate = LocalDate.parse(scanner.next());
                    tracker.viewRemainingBudget(budgetDate);
                    break;
                case 10:
                    System.out.println("Exiting...");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 10.");
            }
        }
    }
}
