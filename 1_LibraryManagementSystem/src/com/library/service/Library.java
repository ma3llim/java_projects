package com.library.service;

import com.library.model.Book;
import java.util.ArrayList;
import java.util.List;

public class Library {
    private List<Book> books;
    private List<Book> borrowedBooks;

    // Constructor
    public Library() {
        this.books = new ArrayList<>();
        this.borrowedBooks = new ArrayList<>();
    }

    public void addBook(Book book) {
        if (book != null) {
            books.add(book);
            System.out.println("Book added successfully: " + book.getTitle());
        } else {
            System.out.println("Invalid book data.");
        }
    }

    public void removeBook(String isbn) {
        boolean removed = false;
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getIsbn().equals(isbn)) {
                books.remove(i);
                removed = true;
                break;
            }
        }
        
        if (removed) {
            System.out.println("Book removed successfully.");
        } else {
            System.out.println("Book not found with ISBN: " + isbn);
        }
    }

    public List<Book> searchByTitle(String title) {
        List<Book> results = new ArrayList<>();
        
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                results.add(book);
            }
        }
        
        return results;
    }

    public List<Book> searchByAuthor(String author) {
        List<Book> results = new ArrayList<>();
        
        for (Book book : books) {
            if (book.getAuthor().toLowerCase().contains(author.toLowerCase())) {
                results.add(book);
            }
        }
        
        return results;
    }

    public boolean borrowBook(String isbn) {
        for (Book book : books) {
            if (book.getIsbn().equals(isbn) && book.isAvailable()) {
                book.borrowBook();
                borrowedBooks.add(book);
                System.out.println("Book borrowed successfully: " + book.getTitle());
                return true;
            }
        }
        System.out.println("Book is not available or doesn't exist.");
        return false;
    }

    public boolean returnBook(String isbn) {
        for (int i = 0; i < borrowedBooks.size(); i++) {
            Book book = borrowedBooks.get(i);
            if (book.getIsbn().equals(isbn)) {
                book.returnBook();
                borrowedBooks.remove(i);
                System.out.println("Book returned successfully: " + book.getTitle());
                return true;
            }
        }
        System.out.println("This book was not borrowed or doesn't exist.");
        return false;
    }

    public void displayAvailableBooks() {
        List<Book> availableBooks = new ArrayList<>();
        
        for (Book book : books) {
            if (book.isAvailable()) {
                availableBooks.add(book);
            }
        }

        if (availableBooks.isEmpty()) {
            System.out.println("No books available at the moment.");
            return;
        }

        System.out.println("Available Books:");
        for (Book book : availableBooks) {
            System.out.println(book);
        }
    }

    // Display all books (including borrowed)
    public void displayAllBooks() {
        if (books.isEmpty()) {
            System.out.println("No books in the library.");
            return;
        }

        System.out.println("All Books in Library:");
        
        int availableCount = 0;
        for (Book book : books) {
            System.out.println(book);
            if (book.isAvailable()) {
                availableCount++;
            }
        }
        
        System.out.println("Total Books: " + books.size());
        System.out.println("Available: " + availableCount);
        System.out.println("Borrowed: " + (books.size() - availableCount));
    }

    // Get total number of books
    public int getTotalBooks() {
        return books.size();
    }

    // Get available books count
    public int getAvailableBooksCount() {
        int count = 0;
        for (Book book : books) {
            if (book.isAvailable()) {
                count++;
            }
        }
        return count;
    }
}