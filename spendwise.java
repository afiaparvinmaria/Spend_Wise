import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

class Expense {
    private String description;
    private double amount;
    private final LocalDate date;
    private String category;

    Expense(String description, double amount, String category) {
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = LocalDate.now();
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public void update(String description, double amount, String category) {
        this.description = description;
        this.amount = amount;
        this.category = category;
    }

    @Override
    public String toString() {
        return "Description: " + description
            + ", Amount: " + amount
            + ", Date: " + date
            + ", Category: " + category;
    }
}

interface BudgetStrategy {
    boolean matches(Expense expense, LocalDate referenceDate);
}

class DailyBudgetStrategy implements BudgetStrategy {
    @Override
    public boolean matches(Expense expense, LocalDate referenceDate) {
        return expense.getDate().equals(referenceDate);
    }
}

class WeeklyBudgetStrategy implements BudgetStrategy {
    @Override
    public boolean matches(Expense expense, LocalDate referenceDate) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        return expense.getDate().get(weekFields.weekBasedYear()) == referenceDate.get(weekFields.weekBasedYear())
            && expense.getDate().get(weekFields.weekOfWeekBasedYear()) == referenceDate.get(weekFields.weekOfWeekBasedYear());
    }
}

class MonthlyBudgetStrategy implements BudgetStrategy {
    @Override
    public boolean matches(Expense expense, LocalDate referenceDate) {
        return expense.getDate().getYear() == referenceDate.getYear()
            && expense.getDate().getMonth() == referenceDate.getMonth();
    }
}

class ExpenseTracker {
    private final List<Expense> expenses = new ArrayList<>();
    private final Map<String, Double> budgets = new LinkedHashMap<>();
    private final Map<String, BudgetStrategy> budgetStrategies = new LinkedHashMap<>();

    ExpenseTracker() {
        budgetStrategies.put("daily", new DailyBudgetStrategy());
        budgetStrategies.put("weekly", new WeeklyBudgetStrategy());
        budgetStrategies.put("monthly", new MonthlyBudgetStrategy());

        for (String category : budgetStrategies.keySet()) {
            budgets.put(category, 0.0);
        }
    }

    public void logExpense(String description, double amount, String category) {
        expenses.add(new Expense(description, amount, category));
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public List<Expense> getExpensesByCategory(String category) {
        List<Expense> filteredExpenses = new ArrayList<>();
        for (Expense expense : expenses) {
            if (expense.getCategory().equalsIgnoreCase(category)) {
                filteredExpenses.add(expense);
            }
        }
        return filteredExpenses;
    }

    public boolean deleteExpense(int index) {
        if (index < 0 || index >= expenses.size()) {
            return false;
        }
        expenses.remove(index);
        return true;
    }

    public boolean editExpense(int index, String newDescription, double newAmount, String newCategory) {
        if (index < 0 || index >= expenses.size()) {
            return false;
        }

        expenses.get(index).update(newDescription, newAmount, newCategory);
        return true;
    }

    public Map<String, Double> getTotalExpensesByCategory() {
        Map<String, Double> totals = new LinkedHashMap<>();

        for (String category : budgetStrategies.keySet()) {
            totals.put(category, 0.0);
        }

        for (Expense expense : expenses) {
            String category = expense.getCategory().toLowerCase(Locale.ROOT);
            totals.put(category, totals.getOrDefault(category, 0.0) + expense.getAmount());
        }

        return totals;
    }

    public void setBudget(double dailyBudget, double weeklyBudget, double monthlyBudget) {
        budgets.put("daily", dailyBudget);
        budgets.put("weekly", weeklyBudget);
        budgets.put("monthly", monthlyBudget);
    }

    public Map<String, Double> getRemainingBudget(LocalDate date) {
        Map<String, Double> remainingBudget = new LinkedHashMap<>();

        for (Map.Entry<String, BudgetStrategy> entry : budgetStrategies.entrySet()) {
            String category = entry.getKey();
            BudgetStrategy strategy = entry.getValue();
            double spentAmount = 0.0;

            for (Expense expense : expenses) {
                if (expense.getCategory().equalsIgnoreCase(category) && strategy.matches(expense, date)) {
                    spentAmount += expense.getAmount();
                }
            }

            remainingBudget.put(category, budgets.get(category) - spentAmount);
        }

        return remainingBudget;
    }

    public boolean isValidCategory(String category) {
        return budgetStrategies.containsKey(category.toLowerCase(Locale.ROOT));
    }

    public String getSupportedCategories() {
        return String.join(", ", budgetStrategies.keySet());
    }
}

interface MenuAction {
    String getLabel();
    void execute();
}

class ConsoleMenuAction implements MenuAction {
    private final String label;
    private final Runnable action;

    ConsoleMenuAction(String label, Runnable action) {
        this.label = label;
        this.action = action;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void execute() {
        action.run();
    }
}

public class spendwise {
    private final ExpenseTracker tracker = new ExpenseTracker();
    private final Scanner scanner = new Scanner(System.in);
    private final Map<Integer, MenuAction> actions = new LinkedHashMap<>();
    private boolean running = true;

    public static void main(String[] args) {
        new spendwise().run();
    }

    spendwise() {
        registerActions();
    }

    public void run() {
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");
            MenuAction action = actions.get(choice);

            if (action == null) {
                System.out.println("Invalid choice. Please enter a number between 1 and " + actions.size() + ".");
                continue;
            }

            action.execute();
        }
    }

    private void registerActions() {
        actions.put(1, new ConsoleMenuAction("Log Expense", this::logExpense));
        actions.put(2, new ConsoleMenuAction("View Expenses", this::viewExpenses));
        actions.put(3, new ConsoleMenuAction("View Expenses by Category", this::viewExpensesByCategory));
        actions.put(4, new ConsoleMenuAction("View Total Expense by Category", this::viewTotalExpenseByCategory));
        actions.put(5, new ConsoleMenuAction("Delete Expense", this::deleteExpense));
        actions.put(6, new ConsoleMenuAction("Edit Expense", this::editExpense));
        actions.put(7, new ConsoleMenuAction("Set Budget", this::setBudget));
        actions.put(8, new ConsoleMenuAction("View Remaining Budget", this::viewRemainingBudget));
        actions.put(9, new ConsoleMenuAction("Exit", this::exitApplication));
    }

    private void printMenu() {
        System.out.println("\nExpense Tracker Menu:");
        for (Map.Entry<Integer, MenuAction> entry : actions.entrySet()) {
            System.out.println(entry.getKey() + ". " + entry.getValue().getLabel());
        }
    }

    private void logExpense() {
        String description = readLine("Enter expense description: ");
        double amount = readNonNegativeDouble("Enter expense amount: ");
        String category = readCategory("Enter expense category (" + tracker.getSupportedCategories() + "): ");

        tracker.logExpense(description, amount, category);
        System.out.println("Expense logged successfully.");
    }

    private void viewExpenses() {
        if (tracker.getExpenses().isEmpty()) {
            System.out.println("No expenses logged yet.");
            return;
        }

        printExpenses(tracker.getExpenses());
    }

    private void viewExpensesByCategory() {
        String category = readCategory("Enter category to search (" + tracker.getSupportedCategories() + "): ");
        List<Expense> expenses = tracker.getExpensesByCategory(category);

        if (expenses.isEmpty()) {
            System.out.println("No expenses found in the " + category + " category.");
            return;
        }

        printExpenses(expenses);
    }

    private void viewTotalExpenseByCategory() {
        if (tracker.getExpenses().isEmpty()) {
            System.out.println("No expenses logged yet.");
            return;
        }

        Map<String, Double> totals = tracker.getTotalExpensesByCategory();
        System.out.println("Total Expenses by Category:");
        for (Map.Entry<String, Double> entry : totals.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    private void deleteExpense() {
        if (tracker.getExpenses().isEmpty()) {
            System.out.println("No expenses logged yet.");
            return;
        }

        printExpenses(tracker.getExpenses());
        int index = readInt("Enter the index of the expense to delete: ") - 1;

        if (tracker.deleteExpense(index)) {
            System.out.println("Expense deleted successfully.");
        } else {
            System.out.println("Invalid index. Cannot delete expense.");
        }
    }

    private void editExpense() {
        if (tracker.getExpenses().isEmpty()) {
            System.out.println("No expenses logged yet.");
            return;
        }

        printExpenses(tracker.getExpenses());
        int index = readInt("Enter the index of the expense to edit: ") - 1;
        String newDescription = readLine("Enter new description: ");
        double newAmount = readNonNegativeDouble("Enter new amount: ");
        String newCategory = readCategory("Enter new category (" + tracker.getSupportedCategories() + "): ");

        if (tracker.editExpense(index, newDescription, newAmount, newCategory)) {
            System.out.println("Expense edited successfully.");
        } else {
            System.out.println("Invalid index. Cannot edit expense.");
        }
    }

    private void setBudget() {
        double dailyBudget = readNonNegativeDouble("Set daily budget: ");
        double weeklyBudget = readNonNegativeDouble("Set weekly budget: ");
        double monthlyBudget = readNonNegativeDouble("Set monthly budget: ");

        tracker.setBudget(dailyBudget, weeklyBudget, monthlyBudget);
        System.out.println("Budgets set successfully.");
    }

    private void viewRemainingBudget() {
        LocalDate date = readDate("Enter the date to view remaining budget (yyyy-mm-dd): ");
        Map<String, Double> remainingBudget = tracker.getRemainingBudget(date);

        System.out.println("Remaining Daily Budget: " + remainingBudget.get("daily"));
        System.out.println("Remaining Weekly Budget: " + remainingBudget.get("weekly"));
        System.out.println("Remaining Monthly Budget: " + remainingBudget.get("monthly"));
    }

    private void exitApplication() {
        System.out.println("Exiting...");
        running = false;
    }

    private void printExpenses(List<Expense> expenses) {
        System.out.println("Expenses:");
        for (int index = 0; index < expenses.size(); index++) {
            System.out.println((index + 1) + ". " + expenses.get(index));
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();

            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private double readNonNegativeDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();

            try {
                double amount = Double.parseDouble(value);
                if (amount < 0) {
                    System.out.println("Amount cannot be negative.");
                    continue;
                }
                return amount;
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private String readCategory(String prompt) {
        while (true) {
            System.out.print(prompt);
            String category = scanner.nextLine().trim().toLowerCase(Locale.ROOT);

            if (tracker.isValidCategory(category)) {
                return category;
            }

            System.out.println("Invalid category. Use one of: " + tracker.getSupportedCategories());
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String dateInput = scanner.nextLine().trim();

            try {
                return LocalDate.parse(dateInput);
            } catch (DateTimeParseException exception) {
                System.out.println("Invalid date format. Please use yyyy-mm-dd.");
            }
        }
    }
}
