import tkinter as tk
from tkinter import ttk, messagebox
import sqlite3
from datetime import datetime
import hashlib
import webbrowser

class MainPage(tk.Frame):
    def __init__(self, parent, controller):
        """Initialize the MainPage frame.

        This method sets up the main content frame, recent fines frame, and buttons for navigating to different pages.
        It also updates the total fines and recent fines on initialization.

        Parameters:
            parent (tkinter.Frame): The parent frame.
            controller (object): The controller object.
        """
        super().__init__(parent)
        self.controller = controller

        # Label to display the total fines
        self.total_fines_label = ttk.Label(self, text="Sakkoja kerätty: ")
        self.total_fines_label.pack()
        self.update_total_fines()

        # Main content frame
        self.main_content_frame = ttk.Frame(self)
        self.main_content_frame.pack(fill="both", expand=True)

        # Frame for recent fines
        self.recent_fines_frame = ttk.Frame(self.main_content_frame)
        self.recent_fines_frame.pack(side="right", fill="both", padx=10, expand=True)

        # Frame for Treeview and Scrollbar
        self.recent_tree_frame = ttk.Frame(self.recent_fines_frame)
        self.recent_tree_frame.pack(fill="both", expand=True)

        # Treeview table to display recent fines
        self.recent_fines_tree = ttk.Treeview(self.recent_tree_frame, columns=(
            "ID", "Player", "Reason", "Amount", "Date"), show="headings")
        self.recent_fines_tree.heading("ID", text="ID")
        self.recent_fines_tree.heading("Player", text="Pelaaja")
        self.recent_fines_tree.heading("Reason", text="Syy")
        self.recent_fines_tree.heading("Amount", text="Määrä")
        self.recent_fines_tree.heading("Date", text="Pvm")

        # Pack the Treeview
        self.recent_fines_tree.pack(side="left", fill="both", expand=True)

        # Scrollbar for the Treeview
        self.scrollbar = ttk.Scrollbar(self.recent_tree_frame, orient="vertical", command=self.recent_fines_tree.yview)
        self.scrollbar.pack(side="right", fill="y")

        # Configure Treeview to use the scrollbar
        self.recent_fines_tree.configure(yscrollcommand=self.scrollbar.set)

        # Initialize variables for scrolling and loading
        self.last_id = 0
        self.batch_size = 10
        self.loading = False

        # Buttons to navigate to different pages
        ttk.Button(self, text="Hae Sakkoja", command=lambda: controller.show_frame("ShowFinesPage")).pack(pady=10)
        ttk.Button(self, text="Lisää Sakkoja", command=lambda: controller.show_frame("AddFinePage")).pack(pady=10)
        ttk.Button(self, text="Lisää Pelaaja", command=lambda: controller.show_frame("AddPlayerPage")).pack(pady=10)
        ttk.Button(self, text="Lähetä yksittäiset sakot", command=lambda: controller.show_frame("SendFinesPage")).pack(pady=10)

        ttk.Button(self, text="Kuukauden sakot", command=self.show_unpaid_fines).pack(pady=10)
        ttk.Button(self, text="Muokkaa Sakkoa", command=lambda: controller.show_frame("EditFinePage")).pack(pady=10)
        ttk.Button(self, text="Sakko Maksettu", command=lambda: controller.show_frame("MarkFineAsPaidPage")).pack()

        # Update recent fines on initialization
        self.update_recent_fines()

    def update_total_fines(self):
        """Update the total fines label.

        This method fetches the sum of unpaid and paid fines from the database and updates the label with the total fines.

        Returns:
            None
        """
        # Connect to the database
        conn = sqlite3.connect('sakot.db')
        cursor = conn.cursor()

        # Fetch the sum of unpaid fines
        cursor.execute("SELECT SUM(sakon_maara) FROM Sakko WHERE maksettu = 0")
        total_unpaid_fines = cursor.fetchone()[0]

        if total_unpaid_fines is None:
            total_unpaid_fines = 0

        # Fetch the sum of paid fines
        cursor.execute("SELECT SUM(sakon_maara) FROM Sakko WHERE maksettu = 1")
        total_paid_fines = cursor.fetchone()[0]

        if total_paid_fines is None:
            total_paid_fines = 0

        # Calculate the total fines
        total_fines = total_unpaid_fines + total_paid_fines

        # Update the label with the total fines
        self.total_fines_label.config(
            text=f"Maksamattomat sakot: {total_unpaid_fines}€\n"
                 f"Maksetut sakot: {total_paid_fines}€\n"
                 f"Sakot yhteensä: {total_fines}€",
            font=("Arial", 16)
        )

        # Close the database connection
        conn.close()

    def update_recent_fines(self):
        """Update the recent fines Treeview.

        This method fetches recent fines from the database and inserts them into the Treeview.

        Returns:
            None
        """
        # Check if loading is in progress
        if self.loading:
            return

        # Connect to the database
        conn = sqlite3.connect('sakot.db')
        cursor = conn.cursor()

        # Fetch recent fines from the database
        cursor.execute(
            "SELECT Sakko.id, Pelaaja.pelaajan_nimi, Sakko.sakon_syy, Sakko.sakon_maara, Sakko.pvm FROM Sakko JOIN Pelaaja ON Sakko.pelinumero = Pelaaja.pelinumero WHERE Sakko.id >? ORDER BY Sakko.id DESC LIMIT? ",
            (self.last_id, self.batch_size))

        fines = cursor.fetchall()

        if fines:
            # Insert the fetched fines into the Treeview
            for fine in fines:
                self.recent_fines_tree.insert("", "end", values=fine)
            self.last_id = fines[-1][0]

        # Close the database connection
        conn.close()

    def show_unpaid_fines(self):
        """Show unpaid fines for each player.

        This method fetches unpaid fines for each player, calculates the total amount, and writes the details to a file.
        It also displays a message box with the summary of unpaid fines.

        Returns:
            None
        """
        # Connect to the database
        conn = sqlite3.connect('sakot.db')
        cursor = conn.cursor()

        # Fetch all player IDs and names
        cursor.execute("SELECT pelinumero, pelaajan_nimi FROM Pelaaja")
        players = cursor.fetchall()

        # Dictionary to hold total unpaid fines for each player
        total_fines_summary = {}

        for pelinumero, nimi in players:
            # Fetch unpaid fines for the current player
            cursor.execute("""
                SELECT sakon_maara 
                FROM Sakko 
                WHERE pelinumero =? AND maksettu = 0
            """, (pelinumero,))
            fines_data = cursor.fetchall()

            if fines_data:
                # Calculate the total amount of unpaid fines for the player
                total_amount = sum(amount for (amount,) in fines_data)
                total_fines_summary[nimi] = total_amount

        # File name for the output
        filename = "maksamattomat_sakot.txt"

        with open(filename, 'w', encoding='utf-8') as file:
            file.write("Maksamattomat sakot:\n\n")

            # Write the total unpaid fines for each player to the file
            for name, total in total_fines_summary.items():
                file.write(f"{name}: {total} €\n")

            file.write("\nPelaajien yksittäiset sakot:\n\n")

            for pelinumero, nimi in players:
                # Fetch unpaid fines details for the current player
                cursor.execute("""
                    SELECT sakon_syy, sakon_maara, pvm 
                    FROM Sakko 
                    WHERE pelinumero =? AND maksettu = 0
                """, (pelinumero,))
                fines_data = cursor.fetchall()

                if fines_data:
                    # Calculate the total amount of unpaid fines for the player
                    total_amount = sum(amount for _, amount, _ in fines_data)

                    file.write(f"{nimi}: {total_amount} €\n")

                    # Write each fine detail to the file
                    for reason, amount, date in fines_data:
                        file.write(f"{reason} - {amount} € - {date}\n")

                    file.write(
                        f"Yhteensä: {total_amount} €\n\n")
                else:
                    file.write(
                        f"{nimi}: Ei maksamattomia sakkoja.\n\n")

        # Construct the notification message
        notification_message = "Tiedot tallennettu tiedostoon!\n\nMaksamattomat sakot:\n"
        for name, total in total_fines_summary.items():
            notification_message += f"{name}: {total} €\n"

        # Show a message box with the notification
        messagebox.showinfo("Maksamattomat sakot", notification_message)

        # Close the database connection
        conn.close()
