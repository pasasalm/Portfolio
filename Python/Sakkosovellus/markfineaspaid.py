import tkinter as tk
from tkinter import ttk, messagebox
from database import get_connection

class MarkFineAsPaidPage(tk.Frame):
    def __init__(self, parent, controller):
        """
        Initializes the MarkFineAsPaidPage, sets up UI elements for marking fines as paid,
        displaying the list of players, and showing their unpaid fines.

        Parameters:
            parent (tkinter.Frame): The parent frame.
            controller (object): The controller object.
        """
        super().__init__(parent)
        self.controller = controller

        # Back button to navigate to the MainPage
        ttk.Button(self, text="⬅️", command=lambda: controller.show_frame("MainPage")).pack(anchor="nw")

        # Buttons to mark fines as paid - moved to the top
        button_frame = ttk.Frame(self)
        button_frame.pack(side="top", fill="x", pady=10)
        ttk.Button(button_frame, text="Kirjaa valittu sakko maksetuksi", command=self.mark_selected_fines_as_paid).pack(side="left", padx=10)
        ttk.Button(button_frame, text="Kirjaa kaikki sakot maksetuiksi", command=self.mark_all_fines_as_paid).pack(side="left", padx=10)

        # Player list frame with scrollbar on the right
        self.player_list_frame = ttk.Frame(self)
        self.player_list_frame.pack(side="left", fill="y", padx=10)

        self.players_list_label = ttk.Label(self.player_list_frame, text="Pelaajat", font=("Arial", 14))
        self.players_list_label.pack()

        # Treeview to display the list of players
        self.players_list_tree = ttk.Treeview(self.player_list_frame, columns=("PlayerNumber", "PlayerName"), show="headings")
        self.players_list_tree.heading("PlayerNumber", text="Pelinumero")
        self.players_list_tree.heading("PlayerName", text="Nimi")
        self.players_list_tree.pack(side="left", fill="both", expand=True)

        # Scrollbar now positioned on the right of the player list
        self.players_list_scrollbar = ttk.Scrollbar(self.player_list_frame, orient="vertical", command=self.players_list_tree.yview)
        self.players_list_scrollbar.pack(side="right", fill="y")
        self.players_list_tree.configure(yscrollcommand=self.players_list_scrollbar.set)

        # Bind selection event
        self.players_list_tree.bind("<ButtonRelease-1>", self.on_player_select)

        # Fines list frame
        self.fines_list_frame = ttk.Frame(self)
        self.fines_list_frame.pack(side="right", fill="both", expand=True)

        # Treeview to display the fines
        self.fines_tree = ttk.Treeview(self.fines_list_frame, columns=("Reason", "Amount", "Date"), show="headings")
        self.fines_tree.heading("Reason", text="Syy")
        self.fines_tree.heading("Amount", text="Määrä")
        self.fines_tree.heading("Date", text="Pvm")
        self.fines_tree.pack(side="left", fill="both", expand=True)

        # Scrollbar is now correctly placed on the right of the fines list
        self.fines_scrollbar = ttk.Scrollbar(self.fines_list_frame, orient="vertical", command=self.fines_tree.yview)
        self.fines_scrollbar.pack(side="right", fill="y")
        self.fines_tree.configure(yscrollcommand=self.fines_scrollbar.set)

        # Update the player list on initialization
        self.update_players_list()

    def update_players_list(self):
        """
        Fetches and displays the list of all players from the database.

        Returns:
            None
        """
        conn = get_connection()  # Use the imported function to get a connection
        cursor = conn.cursor()

        # Fetch players from the database
        cursor.execute("SELECT pelinumero, pelaajan_nimi FROM Pelaaja ORDER BY pelinumero")
        players = cursor.fetchall()

        # Clear previous players
        self.players_list_tree.delete(*self.players_list_tree.get_children())
        for player in players:
            pelinumero, nimi = player
            self.players_list_tree.insert("", "end", values=(pelinumero, nimi))

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
        selected_item = self.players_list_tree.selection()
        if selected_item:
            pelinumero = self.players_list_tree.item(selected_item[0])['values'][0]
            # Automatically show fines for the selected player
            self.show_fines(pelinumero)

    def show_fines(self, pelinumero):
        """
        Fetches and displays the unpaid fines for the given player number.

        Parameters:
            pelinumero (str): The player number.

        Returns:
            None
        """
        conn = get_connection()  # Use the imported function to get a connection
        cursor = conn.cursor()

        # Fetch unpaid fines for the player from the database
        cursor.execute("SELECT * FROM Sakko WHERE pelinumero =? AND maksettu = 0 ORDER BY pvm DESC", (pelinumero,))
        fines = cursor.fetchall()

        # Clear previous fines
        self.fines_tree.delete(*self.fines_tree.get_children())
        for fine in fines:
            # Insert fine information
            self.fines_tree.insert("", "end", iid=fine[0], values=(fine[2], f"{fine[3]:.2f} €", fine[4]))

        conn.close()

    def mark_selected_fines_as_paid(self):
        """
        Marks the selected fines as paid and updates the player's unpaid and paid fines totals.

        Returns:
            None
        """
        selected_items = self.fines_tree.selection()  # Get selected row(s)
        if not selected_items:
            messagebox.showwarning("Varoitus", "Valitse vähintään yksi sakko.")
            return

        total_paid = 0  # Track the total amount paid
        for item in selected_items:
            values = self.fines_tree.item(item, "values")  # Get the selected fine's values
            amount_str = values[1].replace(' €', '').replace(',', '.')  # Handle potential comma in amounts
            amount_paid = float(amount_str)  # Convert to float
            total_paid += amount_paid  # Add to total paid

            # Mark the fine as paid in the database
            conn = get_connection()  # Use the imported function to get a connection
            cursor = conn.cursor()
            cursor.execute("UPDATE Sakko SET maksettu = 1 WHERE id =?", (item,))
            cursor.execute("""
                UPDATE Pelaaja SET sakkoja_maksamatta = sakkoja_maksamatta -?, sakkoja_maksettu = sakkoja_maksettu +?
                WHERE pelinumero IN (SELECT pelinumero FROM Sakko WHERE id =?)
            """, (amount_paid, amount_paid, item))
            conn.commit()
            conn.close()

            # Remove the fine from the TreeView
            self.fines_tree.delete(item)

        messagebox.showinfo("Onnistui", f"Valittujen sakkojen summa: {total_paid:.2f} € on merkitty maksetuiksi.")

        # Refresh the fines list for the player
        if selected_items:
            pelinumero = self.players_list_tree.item(self.players_list_tree.selection()[0])['values'][0]
            self.show_fines(pelinumero)

    def mark_all_fines_as_paid(self):
        """
        Marks all unpaid fines for the currently selected player as paid and updates the player's unpaid and paid fines totals.

        Returns:
            None
        """
        all_items = self.fines_tree.get_children()  # Get all fines for the player
        if not all_items:
            messagebox.showwarning("Varoitus", "Ei kirjattavia sakkoja.")
            return

        total_paid = 0  # Track the total amount paid
        for item in all_items:
            values = self.fines_tree.item(item, "values")  # Get the fine's values
            amount_str = values[1].replace(' €', '').replace(',', '.')  # Handle potential comma in amounts
            amount_paid = float(amount_str)  # Convert to float
            total_paid += amount_paid  # Add to total paid

            # Mark the fine as paid in the database
            conn = get_connection()  # Use the imported function to get a connection
            cursor = conn.cursor()
            cursor.execute("UPDATE Sakko SET maksettu = 1 WHERE id =?", (item,))
            cursor.execute("""
                UPDATE Pelaaja SET sakkoja_maksamatta = sakkoja_maksamatta -?, sakkoja_maksettu = sakkoja_maksettu +?
                WHERE pelinumero IN (SELECT pelinumero FROM Sakko WHERE id =?)
            """, (amount_paid, amount_paid, item))
            conn.commit()
            conn.close()

            # Remove the fine from the TreeView
            self.fines_tree.delete(item)

        messagebox.showinfo("Onnistui", f"Kaikki sakot yhteensä {total_paid:.2f} € on merkitty maksetuiksi.")

        # Refresh the fines list for the player
        if all_items:
            pelinumero = self.players_list_tree.item(self.players_list_tree.selection()[0])['values'][0]
            self.show_fines(pelinumero)
