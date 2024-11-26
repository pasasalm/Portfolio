import tkinter as tk
from tkinter import ttk, messagebox
import sqlite3
from datetime import datetime
from startscreen import MainPage

class AddPlayerPage(tk.Frame):
    def __init__(self, parent, controller):
        """
        Initializes the AddPlayerPage, sets up UI elements for adding players,
        displaying the list of players, and handling player selection.

        Parameters:
            parent (tkinter.Frame): The parent frame.
            controller (object): The controller object.
        """
        super().__init__(parent)
        self.controller = controller

        # Back button to navigate to the MainPage
        ttk.Button(self, text="⬅️", command=lambda: controller.show_frame("MainPage")).pack(anchor="nw")

        # Frame for player details entry
        self.fields_frame = ttk.Frame(self)
        self.fields_frame.pack(side="left", fill="both", expand=True, padx=10, pady=10)

        # Entry fields for player details
        ttk.Label(self.fields_frame, text="Etunimi:").pack(pady=5)
        self.etunimi_entry = ttk.Entry(self.fields_frame)
        self.etunimi_entry.pack(pady=5)

        ttk.Label(self.fields_frame, text="Sukunimi:").pack(pady=5)
        self.sukunimi_entry = ttk.Entry(self.fields_frame)
        self.sukunimi_entry.pack(pady=5)

        ttk.Label(self.fields_frame, text="Pelinumero:").pack(pady=5)
        self.pelinumero_entry = ttk.Entry(self.fields_frame)
        self.pelinumero_entry.pack(pady=5)

        ttk.Label(self.fields_frame, text="Puhelin numero:").pack(pady=5)
        self.puhelin_numero_entry = ttk.Entry(self.fields_frame)
        self.puhelin_numero_entry.pack(pady=5)
        self.puhelin_numero_entry.insert(0, "+358")  # Default phone number prefix

        # Button to add the player
        ttk.Button(self.fields_frame, text="Lisää Pelaaja", command=self.add_player).pack(pady=5)

        # Frame for displaying the list of players
        self.players_list_frame = ttk.Frame(self)
        self.players_list_frame.pack(side="right", fill="y", padx=10)

        # Label and Treeview to display the list of players
        self.players_list_label = ttk.Label(self.players_list_frame, text="Pelaajat", font=("Arial", 14))
        self.players_list_label.pack()

        self.players_list_tree = ttk.Treeview(self.players_list_frame, columns=("PlayerNumber", "PlayerName"), show="headings")
        self.players_list_tree.heading("PlayerNumber", text="Pelinumero")
        self.players_list_tree.heading("PlayerName", text="Nimi")
        self.players_list_tree.pack(side="left", fill="both", expand=True)

        # Scrollbar for the players list Treeview
        self.players_list_scrollbar = ttk.Scrollbar(self.players_list_frame, orient="vertical", command=self.players_list_tree.yview)
        self.players_list_scrollbar.pack(side="right", fill="y")
        self.players_list_tree.configure(yscrollcommand=self.players_list_scrollbar.set)

        # Bind selection event to the on_player_select method
        self.players_list_tree.bind("<ButtonRelease-1>", self.on_player_select)

        # Update the players list on initialization
        self.update_players_list()

    def add_player(self):
        """
        Adds a new player to the database based on the entered details.
        Updates the players list and notifies other frames.

        Returns:
            None
        """
        try:
            # Get the player details from the entry fields
            nimi = f"{self.etunimi_entry.get()} {self.sukunimi_entry.get()}"
            pelinumero = int(self.pelinumero_entry.get())
            puhelin_numero = self.puhelin_numero_entry.get()

            # Connect to the database
            conn = sqlite3.connect('sakot.db')
            cursor = conn.cursor()

            # Insert the player into the Pelaaja table
            cursor.execute(
                "INSERT INTO Pelaaja (pelaajan_nimi, pelinumero, sakkoja_maksamatta, sakkoja_maksettu, sakot_yhteensa, puhelin_numero) VALUES (?,?, 0, 0, 0,?)",
                (nimi, pelinumero, puhelin_numero))
            conn.commit()
            conn.close()

            # Show a success message
            messagebox.showinfo("Onnistui", f'Pelaaja {nimi}, puh {puhelin_numero}, pelinumerolla {pelinumero} lisätty.')

            # Update related frames and lists
            self.controller.frames["ShowFinesPage"].update_players_list()
            self.controller.frames["AddFinePage"].update_players_list()

        except Exception as e:
            # Show an error message if any exception occurs
            messagebox.showerror("Virhe", f'Virhe: {e}')

        # Update the players list in this frame
        self.update_players_list()

    def update_players_list(self):
        """
        Fetches and displays the list of all players from the database.

        This method connects to the database, executes a query to fetch player details,
        clears the existing players list, and inserts the new player details into the Treeview.

        Returns:
            None
        """
        # Connect to the database
        conn = sqlite3.connect('sakot.db')
        cursor = conn.cursor()

        # Fetch players from the database, ordered by player number
        cursor.execute(
            "SELECT pelinumero, pelaajan_nimi FROM Pelaaja ORDER BY pelinumero")
        players = cursor.fetchall()

        # Clear the existing players list in the Treeview
        self.players_list_tree.delete(*self.players_list_tree.get_children())

        # Insert each player into the Treeview
        for player in players:
            pelinumero, nimi = player
            self.players_list_tree.insert("", "end", values=(pelinumero, nimi))

        # Close the database connection
        conn.close()

    def on_player_select(self, event):
        """
        Handles the event when a player is selected from the list.
        Populates the player number entry field with the selected player's number.

        Parameters:
            event (tkinter.Event): The event object.

        Returns:
            None
        """
        # Get the selected item from the players list Treeview
        selected_item = self.players_list_tree.selection()
        if selected_item:
            # Extract the player number from the selected item
            pelinumero = \
            self.players_list_tree.item(selected_item[0])['values'][0]

            # Clear and insert the player number into the entry field
            self.pelinumero_entry.delete(0, tk.END)
            self.pelinumero_entry.insert(0, pelinumero)


class AddFinePage(tk.Frame):
    def __init__(self, parent, controller):
        """
        Initializes the AddFinePage, sets up UI elements for adding fines,
        displaying the list of players, and handling player selection.

        Parameters:
            parent (tkinter.Frame): The parent frame.
            controller (object): The controller object.
        """
        super().__init__(parent)
        self.controller = controller

        # Back button to navigate to the MainPage
        ttk.Button(self, text="⬅️", command=lambda: controller.show_frame("MainPage")).pack(anchor="nw")

        # Main content frame
        self.content_frame = ttk.Frame(self)
        self.content_frame.pack(fill="both", expand=True)

        # Frame for fine details entry
        self.fields_frame = ttk.Frame(self.content_frame)
        self.fields_frame.pack(side="left", fill="both", expand=True, padx=10, pady=10)

        # Entry fields for fine details
        ttk.Label(self.fields_frame, text="Pelinumero:").pack(pady=5)
        self.pelinumero_entry = ttk.Entry(self.fields_frame)
        self.pelinumero_entry.pack(pady=5)

        ttk.Label(self.fields_frame, text="Sakon Syy:").pack(pady=5)
        self.syy_entry = ttk.Entry(self.fields_frame)
        self.syy_entry.pack(pady=5)

        ttk.Label(self.fields_frame, text="Sakon Määrä:").pack(pady=5)
        self.maara_entry = ttk.Entry(self.fields_frame)
        self.maara_entry.pack(pady=5)

        # Button to add the fine
        ttk.Button(self.fields_frame, text="Lisää Sakko", command=self.add_fine).pack(pady=5)

        # Frame for displaying the list of players
        self.players_list_frame = ttk.Frame(self.content_frame)
        self.players_list_frame.pack(side="right", fill="y", padx=10)

        # Label and Treeview to display the list of players
        self.players_list_label = ttk.Label(self.players_list_frame, text="Pelaajat", font=("Arial", 14))
        self.players_list_label.pack()

        self.players_list_tree = ttk.Treeview(self.players_list_frame, columns=("PlayerNumber", "PlayerName"), show="headings")
        self.players_list_tree.heading("PlayerNumber", text="Pelinumero")
        self.players_list_tree.heading("PlayerName", text="Nimi")
        self.players_list_tree.pack(side="left", fill="both", expand=True)

        # Scrollbar for the players list Treeview
        self.players_list_scrollbar = ttk.Scrollbar(self.players_list_frame, orient="vertical", command=self.players_list_tree.yview)
        self.players_list_scrollbar.pack(side="right", fill="y")
        self.players_list_tree.configure(yscrollcommand=self.players_list_scrollbar.set)

        # Bind selection event to the on_player_select method
        self.players_list_tree.bind("<ButtonRelease-1>", self.on_player_select)

        # Update the players list on initialization
        self.update_players_list()

    def add_fine(self):
        """
        Adds a new fine to the database based on the entered details.
        Updates the player's unpaid fines and total fines.

        Returns:
            None
        """
        try:
            # Get the fine details from the entry fields
            pelinumero = int(self.pelinumero_entry.get())
            syy = self.syy_entry.get()
            maara = int(self.maara_entry.get())
            pvm = datetime.now().strftime('%d.%m.%Y')

            # Connect to the database
            conn = sqlite3.connect('sakot.db')
            cursor = conn.cursor()

            # Insert the fine into the Sakko table
            cursor.execute(
                "INSERT INTO Sakko (pelinumero, sakon_syy, sakon_maara, pvm) VALUES (?,?,?,?)",
                (pelinumero, syy, maara, pvm))

            # Update the player's unpaid fines and total fines
            cursor.execute(
                "UPDATE Pelaaja SET sakkoja_maksamatta = sakkoja_maksamatta +?, sakot_yhteensa = sakot_yhteensa +? WHERE pelinumero =?",
                (maara, maara, pelinumero))

            # Commit changes and close the connection
            conn.commit()
            conn.close()

            # Show a success message
            messagebox.showinfo("Onnistui",
                                f'Sakko lisätty pelaajalle {pelinumero}. Syy: {syy}, Määrä: {maara}')

            # Update related frames and lists
            self.controller.frames["ShowFinesPage"].update_players_list()
            self.controller.frames["MainPage"].update_total_fines()
            self.controller.frames["MainPage"].update_recent_fines()
            self.controller.frames["EditFinePage"].load_fines()

            # Update the players list in this frame
            self.update_players_list()  # Päivitä lista
        except Exception as e:
            # Show an error message if any exception occurs
            messagebox.showerror("Virhe", f'Virhe: {e}')

    def update_players_list(self):
        """
        Fetches and displays the list of all players from the database.

        This method connects to the database, executes a query to fetch player details,
        clears the existing players list, and inserts the new player details into the Treeview.

        Returns:
            None
        """
        # Connect to the database
        conn = sqlite3.connect('sakot.db')
        cursor = conn.cursor()

        # Fetch players from the database, ordered by player number
        cursor.execute(
            "SELECT pelinumero, pelaajan_nimi FROM Pelaaja ORDER BY pelinumero")
        players = cursor.fetchall()

        # Clear the existing players list in the Treeview
        self.players_list_tree.delete(*self.players_list_tree.get_children())

        # Insert each player into the Treeview
        for player in players:
            pelinumero, nimi = player
            self.players_list_tree.insert("", "end", values=(pelinumero, nimi))

        # Close the database connection
        conn.close()

    def on_player_select(self, event):
        """
        Handles the event when a player is selected from the list.
        Populates the player number entry field with the selected player's number.

        Parameters:
            event (tkinter.Event): The event object.

        Returns:
            None
        """
        # Get the selected item from the players list Treeview
        selected_item = self.players_list_tree.selection()
        if selected_item:
            # Extract the player number from the selected item
            pelinumero = \
            self.players_list_tree.item(selected_item[0])['values'][0]

            # Clear and insert the player number into the entry field
            self.pelinumero_entry.delete(0, tk.END)
            self.pelinumero_entry.insert(0, pelinumero)
