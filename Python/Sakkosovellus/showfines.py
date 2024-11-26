import tkinter as tk
from tkinter import ttk
import sqlite3

class ShowFinesPage(tk.Frame):
    def __init__(self, parent, controller):
        """
        Initializes the ShowFinesPage, sets up UI elements for searching players,
        displaying the list of players, and showing their fines.

        Parameters:
            parent (tkinter.Frame): The parent frame.
            controller (object): The controller object.
        """
        super().__init__(parent)
        self.controller = controller

        # Back button to navigate to the MainPage
        ttk.Button(self, text="⬅️", command=lambda: controller.show_frame("MainPage")).pack(anchor="nw")

        # Main content frame
        self.main_frame = ttk.Frame(self)
        self.main_frame.pack(fill="both", expand=True)

        # Frame for player information
        self.player_info_frame = ttk.Frame(self.main_frame)
        self.player_info_frame.pack(fill="x", pady=10)

        self.player_info_label = ttk.Label(self.player_info_frame, text="", font=("Arial", 16))
        self.player_info_label.pack(pady=10)

        self.pelinumero_entry = ttk.Entry(self.player_info_frame, width=20)
        self.pelinumero_entry.pack(pady=5)

        # Button to search for fines based on the entered player number
        ttk.Button(self.player_info_frame, text="Hae", command=self.show_fines).pack(pady=5)

        # Frame for displaying the list of players
        self.players_list_frame = ttk.Frame(self.main_frame)
        self.players_list_frame.pack(side="left", fill="y", padx=10)

        self.players_list_label = ttk.Label(self.players_list_frame, text="Pelaajat", font=("Arial", 14))
        self.players_list_label.pack()

        # Treeview to display the list of players
        self.players_list_tree = ttk.Treeview(self.players_list_frame, columns=("PlayerNumber", "PlayerName"), show="headings")
        self.players_list_tree.heading("PlayerNumber", text="Pelinumero")
        self.players_list_tree.heading("PlayerName", text="Nimi")
        self.players_list_tree.pack(side="left", fill="both", expand=True)

        # Scrollbar for the players list Treeview
        self.players_list_scrollbar = ttk.Scrollbar(self.players_list_frame, orient="vertical", command=self.players_list_tree.yview)
        self.players_list_scrollbar.pack(side="right", fill="y")
        self.players_list_tree.configure(yscrollcommand=self.players_list_scrollbar.set)

        # Bind the selection event to the on_player_select method
        self.players_list_tree.bind("<ButtonRelease-1>", self.on_player_select)

        # Frame for displaying the fines
        self.fines_frame = ttk.Frame(self.main_frame)
        self.fines_frame.pack(side="right", fill="both", expand=True, padx=10)

        # Treeview to display the fines
        self.fines_tree = ttk.Treeview(self.fines_frame, columns=("Reason", "Amount", "Date"), show="headings")
        self.fines_tree.heading("Reason", text="Syy")
        self.fines_tree.heading("Amount", text="Määrä")
        self.fines_tree.heading("Date", text="Pvm")
        self.fines_tree.pack(side="left", fill="both", expand=True)

        # Scrollbar for the fines Treeview
        self.fines_scrollbar = ttk.Scrollbar(self.fines_frame, orient="vertical", command=self.fines_tree.yview)
        self.fines_scrollbar.pack(side="right", fill="y")
        self.fines_tree.configure(yscrollcommand=self.fines_scrollbar.set)

        # Update the players list on initialization
        self.update_players_list()

    def show_fines(self):
        """
        Fetches and displays the fines for the player with the entered player number.
        Updates the player info and fines list.

        Returns:
            None
        """
        # Get the player number from the entry field
        pelinumero = self.pelinumero_entry.get()
        conn = sqlite3.connect('sakot.db')
        cursor = conn.cursor()

        # Fetch player details from the database
        cursor.execute("SELECT * FROM Pelaaja WHERE pelinumero =?", (pelinumero,))
        player = cursor.fetchone()

        if player:
            # Fetch fines for the player from the database
            cursor.execute("SELECT * FROM Sakko WHERE pelinumero =? ORDER BY pvm DESC", (pelinumero,))
            fines = cursor.fetchall()

            # Extract player details
            player_name = player[1]
            unpaid_fines = player[2]
            paid_fines = player[3]
            total_fines = unpaid_fines + paid_fines

            # Update the player info label
            self.player_info_label.config(
                text=f"Pelaaja: {player_name}\nPelinumero: #{pelinumero}\n\n"
                     f"Sakkoja maksamatta: {unpaid_fines} €\n"
                     f"Sakkoja maksettu: {paid_fines} €\n"
                     f"Sakot yhteensä: {total_fines} €"
            )

            # Clear and update the fines Treeview
            self.fines_tree.delete(*self.fines_tree.get_children())
            for fine in fines:
                self.fines_tree.insert("", "end", iid=fine[0], values=(fine[2], f"{fine[3]} €", fine[4]))

        else:
            # Display a message if the player is not found
            self.player_info_label.config(text="Pelaajaa ei löytynyt.")
            self.fines_tree.delete(*self.fines_tree.get_children())

        # Close the database connection
        conn.close()

    def update_players_list(self):
        """
        Fetches and displays the list of all players from the database.

        Returns:
            None
        """
        # Connect to the database
        conn = sqlite3.connect('sakot.db')
        cursor = conn.cursor()

        # Fetch players from the database
        cursor.execute("SELECT pelinumero, pelaajan_nimi FROM Pelaaja ORDER BY pelinumero")
        players = cursor.fetchall()

        # Clear and update the players list Treeview
        self.players_list_tree.delete(*self.players_list_tree.get_children())
        for player in players:
            pelinumero, nimi = player
            self.players_list_tree.insert("", "end", values=(pelinumero, nimi))

        # Close the database connection
        conn.close()

    def on_player_select(self, event):
        """
        Handles the event when a player is selected from the list.
        Fetches the player number and triggers the display of fines.

        Parameters:
            event (tkinter.Event): The event object.

        Returns:
            None
        """
        # Get the selected item from the players list Treeview
        selected_item = self.players_list_tree.selection()
        if selected_item:
            # Extract the player number from the selected item
            pelinumero = self.players_list_tree.item(selected_item[0])['values'][0]
            # Clear and insert the player number into the entry field
            self.pelinumero_entry.delete(0, tk.END)
            self.pelinumero_entry.insert(0, pelinumero)
            # Trigger the display of fines for the selected player
            self.show_fines()
