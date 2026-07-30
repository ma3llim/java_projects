package com.library.com.library;

import com.library.model.Book;
import com.library.service.Library;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Library library = new Library();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("WELCOME TO LIBRARY MANAGEMENT SYSTEM");
        System.out.println("====================================");

        initializeLibrary();

        int choice;
        do {
            displayMenu();
            choice = getUserChoice();

            switch (choice) {
                case 1:
                    addBook();
                    break;
                case 2:
                    removeBook();
                    break;
                case 3:
                    searchByTitle();
                    break;
                case 4:
                    searchByAuthor();
                    break;
                case 5:
                    borrowBook();
                    break;
                case 6:
                    returnBook();
                    break;
                case 7:
                    library.displayAvailableBooks();
                    break;
                case 8:
                    library.displayAllBooks();
                    break;
                case 9:
                    displayStatistics();
                    break;
                case 0:
                    System.out.println("Thank you for using Library Management System!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);

        scanner.close();
    }

    private static void initializeLibrary() {
        library.addBook(new Book("The Great Gatsby", "F. Scott Fitzgerald", "978-0-7432-7356-5"));
        library.addBook(new Book("To Kill a Mockingbird", "Harper Lee", "978-0-06-112008-4"));
        library.addBook(new Book("1984", "George Orwell", "978-0-452-28423-4"));
        library.addBook(new Book("Pride and Prejudice", "Jane Austen", "978-0-14-143951-8"));
        library.addBook(new Book("The Catcher in the Rye", "J.D. Salinger", "978-0-316-76948-0"));
        System.out.println("Initial books added successfully!");
        System.out.println();
    }

    private static void displayMenu() {
        System.out.println();
        System.out.println("===== MAIN MENU =====");
        System.out.println("1.  Add a new book");
        System.out.println("2.  Remove a book");
        System.out.println("3.  Search book by title");
        System.out.println("4.  Search book by author");
        System.out.println("5.  Borrow a book");
        System.out.println("6.  Return a book");
        System.out.println("7.  Display available books");
        System.out.println("8.  Display all books");
        System.out.println("9.  Display statistics");
        System.out.println("0.  Exit");
        System.out.println("====================");
        System.out.print("Enter your choice: ");
    }

    private static int getUserChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void addBook() {
        System.out.println();
        System.out.println("--- Add New Book ---");
        System.out.print("Enter title: ");
        String title = scanner.nextLine();
        System.out.print("Enter author: ");
        String author = scanner.nextLine();
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            System.out.println("All fields are required!");
            return;
        }

        Book book = new Book(title, author, isbn);
        library.addBook(book);
    }

    private static void removeBook() {
        System.out.println();
        System.out.println("--- Remove Book ---");
        System.out.print("Enter ISBN of the book to remove: ");
        String isbn = scanner.nextLine();
        library.removeBook(isbn);
    }

    private static void searchByTitle() {
        System.out.println();
        System.out.println("--- Search by Title ---");
        System.out.print("Enter title to search: ");
        String title = scanner.nextLine();
        List<Book> results = library.searchByTitle(title);
        displaySearchResults(results);
    }

    private static void searchByAuthor() {
        System.out.println();
        System.out.println("--- Search by Author ---");
        System.out.print("Enter author to search: ");
        String author = scanner.nextLine();
        List<Book> results = library.searchByAuthor(author);
        displaySearchResults(results);
    }

    private static void displaySearchResults(List<Book> results) {
        if (results.isEmpty()) {
            System.out.println("No books found.");
        } else {
            System.out.println("Found " + results.size() + " book(s):");
            for (Book book : results) {
                System.out.println(book);
            }
        }
    }

    private static void borrowBook() {
        System.out.println();
        System.out.println("--- Borrow Book ---");
        library.displayAvailableBooks();
        System.out.print("Enter ISBN of the book to borrow: ");
        String isbn = scanner.nextLine();
        library.borrowBook(isbn);
    }

    private static void returnBook() {
        System.out.println();
        System.out.println("--- Return Book ---");
        System.out.print("Enter ISBN of the book to return: ");
        String isbn = scanner.nextLine();
        library.returnBook(isbn);
    }

    private static void displayStatistics() {
        System.out.println();
        System.out.println("--- Library Statistics ---");
        System.out.println("Total Books: " + library.getTotalBooks());
        System.out.println("Available Books: " + library.getAvailableBooksCount());
        System.out.println("Borrowed Books: " + (library.getTotalBooks() - library.getAvailableBooksCount()));
        System.out.println("--------------------------");
    }
}