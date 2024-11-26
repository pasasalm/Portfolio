import tkinter as tk
from tkinter import ttk, messagebox
import sqlite3

def initialize_database():
    """
    Initializes the database by creating the necessary tables if they do not exist.

    This function connects to the 'sakot.db' database, executes SQL scripts to create the Pelaaja and Sakko tables,
    and then commits the changes before closing the connection.

    Returns:
        None
    """

    conn = sqlite3.connect('sakot.db')
    cursor = conn.cursor()

    cursor.executescript('''
    CREATE TABLE IF NOT EXISTS Pelaaja (
        pelinumero INTEGER PRIMARY KEY,
        pelaajan_nimi TEXT NOT NULL,
        sakkoja_maksamatta REAL NOT NULL,
        sakkoja_maksettu REAL NOT NULL,
        sakot_yhteensa REAL NOT NULL,
        puhelin_numero TEXT NOT NULL
    );

    CREATE TABLE IF NOT EXISTS Sakko (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        pelinumero INTEGER NOT NULL,
        sakon_syy TEXT NOT NULL,
        sakon_maara REAL NOT NULL,
        pvm TEXT NOT NULL,
        maksettu INTEGER DEFAULT 0,
        FOREIGN KEY (pelinumero) REFERENCES Pelaaja (pelinumero)
    );
    ''')

    conn.commit()
    conn.close()

def get_connection():
    return sqlite3.connect('sakot.db')